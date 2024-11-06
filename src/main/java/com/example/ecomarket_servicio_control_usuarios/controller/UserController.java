package com.example.ecomarket_servicio_control_usuarios.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.ecomarket_servicio_control_usuarios.model.Usuario;
import com.example.ecomarket_servicio_control_usuarios.service.UserService;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;

@RestController
@RequestMapping("/api/usuarios")
public class UserController {

    @Autowired
    private UserService userService;

    // Endpoint para obtener información pública
    @GetMapping("/public/info")
    public String publicInfo() {
        return "Información pública";
    }

    // Endpoint para registrar un nuevo usuario
    @PostMapping("/registro")
    public ResponseEntity<String> registrarUsuario(@RequestBody Usuario nuevoUsuario) {
        try {
            String response = userService.registrarUsuario(nuevoUsuario);
            return ResponseEntity.status(201).body(response); // 201 Created
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(400).body(e.getMessage()); // 400 Bad Request
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error al registrar usuario"); // 500 Internal Server Error
        }
    }

    @PostMapping("/actualizar-email-auth")
    public ResponseEntity<String> actualizarEmailEnAuth(@RequestBody Map<String, String> body) {
        String uid = body.get("uid");
        String nuevoEmail = body.get("nuevoEmail");

        try {
            userService.actualizarCorreoEnAuth(uid, nuevoEmail);
            return ResponseEntity.status(200).body("Correo actualizado correctamente en Firebase Authentication");
        } catch (FirebaseAuthException e) {
            return ResponseEntity.status(500).body("Error al actualizar el correo en Auth: " + e.getMessage());
        }
    }

// Endpoint para iniciar sesión
    @PostMapping("/ingreso")
    public ResponseEntity<Map<String, Object>> inicioSesion(@RequestBody Map<String, String> credenciales) {
        Map<String, Object> response = new HashMap<>(); // Usamos Object en lugar de String para permitir diferentes tipos de valores
        try {
            // Obtener los datos del usuario autenticado desde el servicio
            Map<String, Object> userData = userService.iniciarSesion(credenciales);

            // Crear un token personalizado basado en el UID del usuario
            String sessionToken = FirebaseAuth.getInstance().createCustomToken(userData.get("uid").toString());

            // Devolver los datos del usuario junto con el token
            response.put("message", "Inicio de sesión exitoso");
            response.put("token", sessionToken);
            response.put("user", userData); // Agregamos los datos del usuario

            return ResponseEntity.status(200).body(response);  // 200 OK
        } catch (IllegalArgumentException e) {
            // Error de autenticación o credenciales incorrectas
            response.put("error", "Credenciales inválidas. Verifica tu email y contraseña.");
            return ResponseEntity.status(401).body(response);  // 401 Unauthorized
        } catch (Exception e) {
            // Error genérico del servidor
            response.put("error", "Error inesperado en el servidor. Inténtalo de nuevo más tarde.");
            return ResponseEntity.status(500).body(response);  // 500 Internal Server Error
        }
    }

    // Endpoint para cerrar sesión
    @PostMapping("/logout")
    public ResponseEntity<String> logout(@RequestBody Map<String, String> body) {
        try {
            String uid = body.get("uid"); // Obtener UID desde el cuerpo de la solicitud
            userService.logout(uid);  // Revocar el token en el servicio
            return ResponseEntity.status(200).body("Sesión cerrada exitosamente"); // 200 OK
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error al cerrar sesión"); // 500 Internal Server Error
        }
    }

// UserController: validar token recibido del frontend
    @PostMapping("/validar-token")
    public ResponseEntity<String> validarToken(@RequestBody Map<String, String> body) {
        try {
            String token = body.get("token");
            FirebaseToken decodedToken = FirebaseAuth.getInstance().verifyIdToken(token);
            String uid = decodedToken.getUid();

            // Aquí puedes realizar más lógica, como verificar el rol del usuario en la base de datos
            return ResponseEntity.status(200).body("Token validado, UID: " + uid);
        } catch (FirebaseAuthException e) {
            return ResponseEntity.status(401).body("Token no válido");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error en la validación del token");
        }
    }

    @DeleteMapping("/eliminar/{uid}")
    public ResponseEntity<String> eliminarUsuario(@PathVariable String uid) {
        try {
            userService.eliminarUsuario(uid);
            return ResponseEntity.status(200).body("Usuario eliminado exitosamente");
        } catch (FirebaseAuthException e) {
            return ResponseEntity.status(500).body("Error al eliminar usuario: " + e.getMessage());
        }
    }

    // Endpoint para aprobar el registro de un usuario
    /*@PutMapping("/aprobacion/{idUsuario}")
    public ResponseEntity<String> aprobarRegistroUsuario(@PathVariable String idUsuario) {
        try {
            userService.aprobarUsuario(idUsuario);
            return ResponseEntity.ok("Usuario aprobado con éxito"); // 200 OK
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(404).body(e.getMessage()); // 404 Not Found
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error al aprobar usuario"); // 500 Internal Server Error
        }
    }*/
}
