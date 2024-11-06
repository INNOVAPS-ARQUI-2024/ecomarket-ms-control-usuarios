package com.example.ecomarket_servicio_control_usuarios.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.ecomarket_servicio_control_usuarios.model.Usuario;
import com.example.ecomarket_servicio_control_usuarios.service.UserService;

import java.util.Map;

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

    // Endpoint para iniciar sesión
    @PostMapping("/ingreso")
    public ResponseEntity<String> inicioSesion(@RequestBody Map<String, String> credenciales) {
        try {
            String token = userService.iniciarSesion(credenciales);
            return ResponseEntity.ok(token); // 200 OK
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(401).body(e.getMessage()); // 401 Unauthorized
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error al iniciar sesión"); // 500 Internal Server Error
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
