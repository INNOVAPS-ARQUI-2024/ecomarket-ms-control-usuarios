package com.example.ecomarket_servicio_control_usuarios.controller;

import com.example.ecomarket_servicio_control_usuarios.EcomarketServicioControlUsuariosApplication;
import com.example.ecomarket_servicio_control_usuarios.controller.UserController;
import com.example.ecomarket_servicio_control_usuarios.model.Usuario;
import com.example.ecomarket_servicio_control_usuarios.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.ws.rs.core.MediaType;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.HashMap;
import java.util.Map;

@WebMvcTest(controllers = UserController.class)
@ContextConfiguration(classes = EcomarketServicioControlUsuariosApplication.class)
public class UserControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @Autowired
    private ObjectMapper objectMapper; // Para convertir objetos a JSON

    @Test
    public void testPublicInfo() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/usuarios/public/info")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().string("Información pública"));
    }

    @Test
    public void testRegistrarUsuario() throws Exception {
        Usuario mockUsuario = new Usuario();
        mockUsuario.setName("John Doe");
        mockUsuario.setEmail("johndoe@example.com");
        mockUsuario.setRole("Comprador");

        Mockito.when(userService.registrarUsuario(Mockito.any(Usuario.class))).thenReturn("Usuario registrado con éxito");

        mockMvc.perform(MockMvcRequestBuilders.post("/api/usuarios/registro")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(mockUsuario)))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.content().string("Usuario registrado con éxito"));
    }

    @Test
    public void testRegistrarUsuarioBadRequest() throws Exception {
        Mockito.when(userService.registrarUsuario(Mockito.any(Usuario.class)))
                .thenThrow(new IllegalArgumentException("Datos inválidos"));

        Usuario mockUsuario = new Usuario();
        mockUsuario.setName(""); // Nombre vacío para provocar error

        mockMvc.perform(MockMvcRequestBuilders.post("/api/usuarios/registro")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(mockUsuario)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().string("Datos inválidos"));
    }

    @Test
    public void testInicioSesion() throws Exception {
        Map<String, String> credenciales = new HashMap<>();
        credenciales.put("email", "johndoe@example.com");
        credenciales.put("password", "12345");

        Mockito.when(userService.iniciarSesion(Mockito.anyMap())).thenReturn("fake-jwt-token");

        mockMvc.perform(MockMvcRequestBuilders.post("/api/usuarios/ingreso")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(credenciales)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().string("fake-jwt-token"));
    }

    @Test
    public void testInicioSesionUnauthorized() throws Exception {
        Map<String, String> credenciales = new HashMap<>();
        credenciales.put("email", "johndoe@example.com");
        credenciales.put("password", "wrongpassword");

        Mockito.when(userService.iniciarSesion(Mockito.anyMap()))
                .thenThrow(new IllegalArgumentException("Credenciales inválidas"));

        mockMvc.perform(MockMvcRequestBuilders.post("/api/usuarios/ingreso")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(credenciales)))
                .andExpect(MockMvcResultMatchers.status().isUnauthorized())
                .andExpect(MockMvcResultMatchers.content().string("Credenciales inválidas"));
    }


}

