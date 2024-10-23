package com.example.ecomarket_servicio_control_usuarios.service;

import java.util.Date;
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

    // Método para iniciar sesión (validar credenciales)
    public String iniciarSesion(Map<String, String> credenciales) throws FirebaseAuthException {
        String email = credenciales.get("email");
        String password = credenciales.get("password");

        if (email == null || password == null) {
            throw new IllegalArgumentException("Correo y contraseña son obligatorios.");
        }

        UserRecord userRecord = firebaseAuth.getUserByEmail(email);
        if (userRecord == null) {
            throw new IllegalArgumentException("Usuario no encontrado.");
        }

        return "Inicio de sesión exitoso. UID: " + userRecord.getUid();
    }

    // Método para aprobar usuarios
    /*public void aprobarUsuario(String idUsuario) throws ExecutionException, InterruptedException {
        CompletableFuture<Void> future = new CompletableFuture<>();

        firebaseDb.child("users").child(idUsuario).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    firebaseDb.child("users").child(idUsuario).child("aprobado").setValueAsync(true);
                    future.complete(null);
                } else {
                    future.completeExceptionally(new IllegalArgumentException("Usuario no encontrado."));
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                future.completeExceptionally(new IllegalStateException("Error en la base de datos: " + databaseError.getMessage()));
            }
        });

        future.get();
    }*/

}
