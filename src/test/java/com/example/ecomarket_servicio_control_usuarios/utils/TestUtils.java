package com.example.ecomarket_servicio_control_usuarios.utils;

import com.example.ecomarket_servicio_control_usuarios.model.Usuario;

import java.util.*;

public class TestUtils {


        public static Usuario mockUsuario() {
            Usuario usuario = new Usuario();
            usuario.setUserId("user123"); // Opcional si el ID se genera automáticamente
            usuario.setName("John Doe");
            usuario.setEmail("johndoe@example.com");
            usuario.setRole("Vendedor");
            usuario.setProfilePicture("http://example.com/profile.jpg"); // Opcional
            usuario.setPhone("+123456789"); // Opcional
            usuario.setCreatedAt(new Date());
            usuario.setUpdatedAt(new Date());
            usuario.setActive(true);
            usuario.setTiposVendedor(Arrays.asList("producto", "servicio"));
            return usuario;
        }
}
