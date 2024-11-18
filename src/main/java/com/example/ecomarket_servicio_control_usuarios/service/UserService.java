package com.example.ecomarket_servicio_control_usuarios.service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import org.springframework.stereotype.Service;

import com.example.ecomarket_servicio_control_usuarios.model.Usuario;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.UserRecord;
import com.google.firebase.auth.UserRecord.CreateRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

@Service
public class UserService {

    private final DatabaseReference firebaseDb;
    private final FirebaseAuth firebaseAuth;

    // Constructor para inicializar Firebase
    public UserService(FirebaseApp firebaseApp) {
        this.firebaseDb = FirebaseDatabase.getInstance(firebaseApp).getReference();
        this.firebaseAuth = FirebaseAuth.getInstance(firebaseApp);
    }

    // Método para registrar un nuevo usuario
    public String registrarUsuario(Usuario nuevoUsuario) throws ExecutionException, InterruptedException {
        CompletableFuture<String> future = new CompletableFuture<>();

        firebaseDb.child("users").orderByChild("email").equalTo(nuevoUsuario.getEmail())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            future.completeExceptionally(new IllegalArgumentException("El correo ya está en uso."));
                        } else {
                            try {
                                CreateRequest request = new CreateRequest()
                                        .setEmail(nuevoUsuario.getEmail())
                                        .setPassword("123456")
                                        .setDisplayName(nuevoUsuario.getName())
                                        .setEmailVerified(false)
                                        .setDisabled(false);

                                UserRecord userRecord = firebaseAuth.createUser(request);
                                nuevoUsuario.setUserId(userRecord.getUid());
                                nuevoUsuario.setCreatedAt(new Date());
                                nuevoUsuario.setUpdatedAt(new Date());

                                firebaseDb.child("users").child(userRecord.getUid()).setValueAsync(nuevoUsuario);
                                future.complete("Usuario registrado con éxito en Authentication y Realtime Database.");
                            } catch (FirebaseAuthException e) {
                                future.completeExceptionally(new IllegalStateException("Error en Firebase Authentication: " + e.getMessage()));
                            }
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        future.completeExceptionally(new IllegalStateException("Error en la base de datos: " + databaseError.getMessage()));
                    }
                });

        return future.get();
    }

// Método para iniciar sesión (validar credenciales y obtener información del usuario)
    public Map<String, Object> iniciarSesion(Map<String, String> credenciales) throws FirebaseAuthException, InterruptedException, ExecutionException {
        String email = credenciales.get("email");
        String password = credenciales.get("password");

        if (email == null || password == null) {
            throw new IllegalArgumentException("Correo y contraseña son obligatorios.");
        }

        // Obtener el usuario desde Firebase Authentication por email
        UserRecord userRecord = firebaseAuth.getUserByEmail(email);
        if (userRecord == null) {
            throw new IllegalArgumentException("Usuario no encontrado.");
        }

        // Recuperar más información del usuario desde Firebase Realtime Database
        DatabaseReference userRef = firebaseDb.child("users").child(userRecord.getUid());
        CompletableFuture<Map<String, Object>> future = new CompletableFuture<>();

        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    Map<String, Object> userData = new HashMap<>();
                    userData.put("uid", userRecord.getUid());
                    userData.put("email", userRecord.getEmail());
                    userData.put("profilePicture", dataSnapshot.child("profilePicture").getValue(String.class));
                    userData.put("role", dataSnapshot.child("role").getValue(String.class));

                    try {
                        // Generar un token de sesión (esto es un ejemplo usando Firebase)
                        String sessionToken = FirebaseAuth.getInstance().createCustomToken(userRecord.getUid());

                        // Agregar el token a la respuesta
                        userData.put("token", sessionToken);

                        future.complete(userData);
                    } catch (FirebaseAuthException e) {
                        future.completeExceptionally(new IllegalStateException("Error al generar el token: " + e.getMessage()));
                    }

                } else {
                    future.completeExceptionally(new IllegalArgumentException("No se encontró el perfil del usuario en la base de datos."));
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                future.completeExceptionally(new IllegalStateException("Error en la base de datos: " + databaseError.getMessage()));
            }
        });

        return future.get();  // Espera la operación asíncrona y devuelve el resultado
    }

    public void logout(String uid) throws FirebaseAuthException {
        // Revocar todos los tokens de sesión activos del usuario
        FirebaseAuth.getInstance().revokeRefreshTokens(uid);
    }

    public void eliminarUsuario(String uid) throws FirebaseAuthException {
        // Eliminar el usuario de Firebase Authentication
        firebaseAuth.deleteUser(uid);

        // Eliminar el usuario de Firebase Realtime Database
        firebaseDb.child("users").child(uid).removeValueAsync();
    }

    public void actualizarCorreoEnAuth(String uid, String nuevoEmail) throws FirebaseAuthException {
        UserRecord.UpdateRequest request = new UserRecord.UpdateRequest(uid).setEmail(nuevoEmail);
        firebaseAuth.updateUser(request);
    }

}
