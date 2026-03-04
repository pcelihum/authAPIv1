package com.example.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;

@SpringBootApplication
@EnableMethodSecurity
public class ClassApplication {

    public static void main(String[] args) {

        // En Docker normalmente NO existe .env dentro del contenedor.
        // Esto evita que la app se caiga si falta.
        Dotenv dotenv = Dotenv.configure()
                .ignoreIfMissing()
                .load();

        // Solo setea properties si existen (para local dev).
        setIfPresent("DB_URL", dotenv.get("DB_URL"));
        setIfPresent("DB_USER", dotenv.get("DB_USER"));     // <-- antes tenías DB_USERNAME
        setIfPresent("DB_PASS", dotenv.get("DB_PASS"));     // <-- antes DB_PASSWORD
        setIfPresent("SERVER_PORT", dotenv.get("SERVER_PORT"));

        SpringApplication.run(ClassApplication.class, args);
    }

    private static void setIfPresent(String key, String value) {
        if (value != null && !value.isBlank()) {
            System.setProperty(key, value);
        }
    }
}