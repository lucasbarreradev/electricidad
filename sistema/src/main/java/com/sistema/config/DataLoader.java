package com.sistema.config;

import com.sistema.model.Usuario;
import com.sistema.repository.UsuarioRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class DataLoader {

    @Bean
    CommandLineRunner initDatabase(
            UsuarioRepository usuarioRepo,
            PasswordEncoder passwordEncoder,
            Environment env) {

        return args -> {

            // =========================
            // ADMIN
            // =========================
            String adminUser = env.getProperty("app.admin.user");
            String adminPass = env.getProperty("app.admin.pass");

            if (adminUser != null && adminPass != null &&
                    !usuarioRepo.existsByUsername(adminUser)) {

                Usuario admin = new Usuario(
                        adminUser,
                        passwordEncoder.encode(adminPass),
                        "Administrador",
                        "Sistema",
                        Usuario.Rol.ADMIN
                );
                usuarioRepo.save(admin);
            }

   
            String empUser = env.getProperty("app.empleado.user");
            String empPass = env.getProperty("app.empleado.pass");

            if (!usuarioRepo.existsByUsername(empUser)) {

                Usuario empleado = new Usuario(
                        empUser,
                        passwordEncoder.encode(empPass),
                        "Empleado",
                        "Sistema",
                        Usuario.Rol.EMPLEADO
                );
                usuarioRepo.save(empleado);
            }
        };
    }


}
