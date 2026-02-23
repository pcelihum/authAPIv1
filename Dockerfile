# Usamos Java 17 con JDK
FROM eclipse-temurin:17-jdk-jammy

# Directorio de trabajo dentro del contenedor
WORKDIR /app

# Copiamos el JAR generado por Maven
COPY target/class-0.0.1-SNAPSHOT.jar app.jar

# Exponer puerto de la aplicación
EXPOSE 8080

# Variables de entorno (puedes sobrescribirlas desde docker-compose)
ENV DB_URL=jdbc:postgresql://postgres:5432/proyecto_megatech
ENV DB_USERNAME=patricio.celi
ENV DB_PASSWORD=Marias6681721.

# Healthcheck para que Docker sepa si la app está lista
HEALTHCHECK --interval=10s --timeout=5s --start-period=10s \
  CMD curl -f http://localhost:8080/actuator/health || exit 1

# Comando para correr la app
ENTRYPOINT ["java","-jar","app.jar"]