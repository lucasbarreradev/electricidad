package com.sistema.config;

import com.sistema.model.Usuario;
import com.sistema.repository.UsuarioRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class DataLoader {

    @Bean
    CommandLineRunner initDatabase(UsuarioRepository usuarioRepo,
                                   PasswordEncoder passwordEncoder) {
        return args -> {
            // Crear admin por defecto si no existe
            if (!usuarioRepo.existsByUsername("admin")) {
                Usuario admin = new Usuario(
                        "admin",
                        passwordEncoder.encode("admin123"),
                        "Administrador",
                        "Sistema",
                        Usuario.Rol.ADMIN
                );
                usuarioRepo.save(admin);
                System.out.println("✅ Usuario ADMIN creado: admin / admin123");
            }

            // Crear empleado de ejemplo
            if (!usuarioRepo.existsByUsername("empleado")) {
                Usuario empleado = new Usuario(
                        "empleado",
                        passwordEncoder.encode("emp123"),
                        "Juan",
                        "Pérez",
                        Usuario.Rol.EMPLEADO
                );
                usuarioRepo.save(empleado);
                System.out.println("✅ Usuario EMPLEADO creado: empleado / emp123");
            }
        };
    }
}
