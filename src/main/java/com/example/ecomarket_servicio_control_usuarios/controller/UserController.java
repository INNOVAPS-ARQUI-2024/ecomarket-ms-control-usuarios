package com.example.ecomarket_servicio_control_usuarios.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/usuarios")
public class UserController {

    // 1.1. Registro de Usuarios
    // Método HTTP POST para registrar un nuevo usuario
    @PostMapping("/registro")
    public ResponseEntity<String> registrarUsuario(@RequestBody String nuevoUsuario) {
        // La lógica incluiría la validación del correo electrónico y la creación del registro

        // Comentado: Retorno de ejemplo para que puedas implementar la lógica más tarde
        /*
        if (correoEnUso) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("El correo ya está en uso.");
        }
        return ResponseEntity.status(HttpStatus.CREATED).body("Usuario creado con éxito.");
        */
        return ResponseEntity.status(HttpStatus.CREATED).body("Registro de usuario (pendiente de implementación)");
    }

    // 1.2. Inicio de Sesión
    // Método HTTP POST para iniciar sesión
    @PostMapping("/ingreso")
    public ResponseEntity<String> inicioSesion(@RequestBody String credenciales) {
        // La lógica debería generar un token JWT si las credenciales son correctas

        // Comentado: Retorno de ejemplo para que puedas implementar la lógica más tarde
        /*
        if (credencialesInvalidas) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Credenciales incorrectas.");
        }
        return ResponseEntity.ok("Inicio de sesión exitoso. Token: <TOKEN>");
        */
        return ResponseEntity.ok("Inicio de sesión (pendiente de implementación)");
    }

    // 1.3. Aprobación de Nuevos Registros
    // Método HTTP PUT para que un administrador apruebe un nuevo registro
    @PutMapping("/aprobacion/{idUsuario}")
    public ResponseEntity<String> aprobarRegistroUsuario(@PathVariable Long idUsuario) {
        // Verifica que el usuario exista y que el administrador tenga permisos para aprobar

        // Comentado: Retorno de ejemplo para que puedas implementar la lógica más tarde
        /*
        if (usuarioNoEncontrado) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Usuario no encontrado.");
        }
        return ResponseEntity.ok("Registro del usuario aprobado.");
        */
        return ResponseEntity.ok("Aprobación de registro de usuario (pendiente de implementación)");
    }

    @GetMapping("/public/info")
    public String publicInfo() {
        return "Información pública";
    }
    @GetMapping("/users")
    public String getUsers() {
        return "Lista de usuarios";
    }
}
