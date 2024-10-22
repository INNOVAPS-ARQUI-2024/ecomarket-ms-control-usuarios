package com.example.ecomarket_servicio_control_usuarios.config;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;

@Configuration
public class FirebaseConfig {

    @Bean
    public FirebaseApp initializeFirebase() throws IOException {
        ClassLoader classLoader = getClass().getClassLoader();
        InputStream serviceAccount = classLoader
                .getResourceAsStream("ecomarket-arqui-2024-firebase-adminsdk-1vey1-c717b062bb.json");

        if (serviceAccount == null) {
            throw new FileNotFoundException("Archivo JSON no encontrado en resources");
        }

        FirebaseOptions options = new FirebaseOptions.Builder()
                .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                .setDatabaseUrl("https://ecomarket-arqui-2024-default-rtdb.firebaseio.com")
                .build();

        return FirebaseApp.initializeApp(options);
    }
}
