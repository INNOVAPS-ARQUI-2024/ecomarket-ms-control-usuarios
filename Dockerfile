# Usar una imagen base de OpenJDK
FROM openjdk:17-jre-slim

# Crear un directorio para la aplicación
WORKDIR /app

# Copiar el archivo JAR generado por Maven en el contenedor
COPY target/ecomarket-servicio-control-usuarios.jar app.jar

# Exponer el puerto que utiliza Spring Boot
EXPOSE 8080

# Definir el comando para ejecutar la aplicación
ENTRYPOINT ["java", "-jar", "app.jar"]
