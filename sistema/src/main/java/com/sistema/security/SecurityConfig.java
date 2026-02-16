package com.sistema.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration @EnableWebSecurity @EnableMethodSecurity // Habilita @PreAuthorize
public class SecurityConfig {
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(); }
    @Bean
    public AuthenticationManager authenticationManager( AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager(); }
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.GET, "/login").permitAll()
                        .requestMatchers(HttpMethod.POST, "/login").permitAll()
                .requestMatchers("/WEB-INF/**").permitAll()
                .requestMatchers("/css/**", "/js/**", "/img/**", "/vendor/**", "/webjars/**").permitAll()
                .requestMatchers("/error").permitAll()

                //Roles

                .requestMatchers("/usuarios/**").hasRole("ADMIN")
                        .requestMatchers("/reportes/**").hasRole("ADMIN") // ========================================== // ADMIN y EMPLEADO // ==========================================

                        .requestMatchers(
                                "/ventas/**",
                                "/presupuestos/**",
                                "/productos/**",
                                "/clientes/**",
                                "/proveedores/**"
                        ) .hasAnyRole("ADMIN", "EMPLEADO")
                        // ========================================== // TODO LO DEMÁS requiere autenticación // ==========================================

                .anyRequest().authenticated()
        )
                .formLogin(form -> form
                        .loginPage("/login")
                        .loginProcessingUrl("/login")
                        .defaultSuccessUrl("/", true)
                        .failureUrl("/login?error=true")
                        .permitAll() )
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/login?logout=true")
                        .invalidateHttpSession(true)
                        .deleteCookies("JSESSIONID")
                )
                .exceptionHandling(ex -> ex
                        .accessDeniedPage("/acceso-denegado")
                )
                .csrf(csrf -> csrf.disable());
        return http.build();
    } }
