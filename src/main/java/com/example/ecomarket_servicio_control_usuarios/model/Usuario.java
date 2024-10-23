package com.example.ecomarket_servicio_control_usuarios.model;

import java.util.Date;
import java.util.List;

import lombok.Data;

@Data
public class Usuario {
    private String userId; // Puede ser opcional al inicio
    private String name;
    private String email;
    private String role;  // "Comprador" o "Vendedor"
    private String profilePicture;  // Imagen de perfil opcional
    private String phone;  // Número de contacto opcional
    private Date createdAt;
    private Date updatedAt;
    private boolean isActive;
    private List<String> tiposVendedor;  // Lista de tipos de vendedor como ["producto", "servicio", "evento"]
}