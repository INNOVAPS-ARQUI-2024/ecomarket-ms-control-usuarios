package com.example.ecomarket_servicio_control_usuarios.controller;

import com.example.ecomarket_servicio_control_usuarios.model.Usuario;
import com.example.ecomarket_servicio_control_usuarios.service.UserService;
import com.example.ecomarket_servicio_control_usuarios.utils.TestUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.Map;

import static org.springframework.test.web.client.match.MockRestRequestMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(UserController.class)
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @Test
    public void testPublicInfoEndpoint() throws Exception {

        mockMvc.perform(MockMvcRequestBuilders.get("/api/usuarios/public/info")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    public void testRegistrarUsuario() throws Exception {
        // Arrange
        Usuario nuevoUsuario = TestUtils.mockUsuario(); // Completa con los datos del usuario de prueba
        String respuestaEsperada = "Usuario registrado con éxito";

        Mockito.when(userService.registrarUsuario(nuevoUsuario)).thenReturn(respuestaEsperada);

        // Act
        mockMvc.perform(MockMvcRequestBuilders.post("/api/usuarios/registro")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(nuevoUsuario)))
                .andExpect(status().isCreated()); // 201

    }
    @Test
    void testInicioSesionExitoso() throws Exception {
        Map<String, String> credenciales = Map.of("email", "test@example.com", "password", "password123");
        Mockito.when(userService.iniciarSesion(credenciales)).thenReturn("mockToken");

        mockMvc.perform(MockMvcRequestBuilders.post("/api/usuarios/ingreso")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(credenciales)))
                .andExpect(status().isOk());
    }

    @Test
    void testInicioSesionCredencialesIncorrectas() throws Exception {
        Map<String, String> credenciales = Map.of("email", "test@example.com", "password", "wrongPassword");
        Mockito.when(userService.iniciarSesion(credenciales)).thenThrow(new IllegalArgumentException("Credenciales inválidas"));

        mockMvc.perform(MockMvcRequestBuilders.post("/api/usuarios/ingreso")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(credenciales)))
                .andExpect(status().isUnauthorized());

    }

    @Test
    void testInicioSesionErrorInterno() throws Exception {
        Map<String, String> credenciales = Map.of("email", "test@example.com", "password", "password123");
        Mockito.when(userService.iniciarSesion(credenciales)).thenThrow(new RuntimeException("Error inesperado"));

        mockMvc.perform(MockMvcRequestBuilders.post("/api/usuarios/ingreso")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(credenciales)))
                .andExpect(status().isInternalServerError());
    }

}
