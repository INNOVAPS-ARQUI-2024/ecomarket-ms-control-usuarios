package com.example.ecomarket_servicio_control_usuarios.service;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value; 
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders; 
import org.springframework.http.HttpMethod; 
import org.springframework.http.MediaType; 
import org.springframework.stereotype.Service; 
import org.springframework.web.client.RestTemplate; 
import org.springframework.web.multipart.MultipartFile; 

@Service
public class AyrshareService {

    @Value("${ayrshare.api.key}") 
    private String apiKey; 

    private final RestTemplate restTemplate = new RestTemplate(); 
    
    public String schedulePost(String postContent, List<String> platforms, String scheduleDate, MultipartFile file) throws Exception { 
        HttpHeaders headers = new HttpHeaders(); 
        headers.setContentType(MediaType.APPLICATION_JSON); 
        headers.setBearerAuth(apiKey); 
        
        Map<String, Object> payload = new HashMap<>(); 
        payload.put("post", postContent); 
        payload.put("platforms", platforms); 
        payload.put("scheduleDate", scheduleDate); 

        if (file != null) { 
            String fileUrl = uploadFileToStorage(file); // Función para subir archivo a un almacenamiento externo y obtener la URL 
            payload.put("mediaUrls", Collections.singletonList(fileUrl)); 
        } 

        HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(payload, headers); 
        String url = "https://api.ayrshare.com/v2/post"; 
        return restTemplate.exchange(url, HttpMethod.POST, requestEntity, String.class).getBody(); 
    } 

    // Función para subir archivo a un almacenamiento externo (puedes usar Amazon S3, Google Cloud Storage, etc.) 
    private String uploadFileToStorage(MultipartFile file) { // Implementa la lógica de subida aquí y devuelve la URL del archivo 
        return "https://url-del-archivo-subido"; 
    }
    
}
