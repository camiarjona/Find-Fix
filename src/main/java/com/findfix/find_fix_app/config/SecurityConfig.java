package com.findfix.find_fix_app.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableMethodSecurity(securedEnabled = true)
public class SecurityConfig {
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http.authorizeHttpRequests(
                        request -> request
                                .requestMatchers("/usuario/registrar").permitAll()
                                .requestMatchers("/usuario", "/roles/**", "/usuario/eliminar").hasRole("ADMIN")
                                .requestMatchers("/usuario/modificar-datos", "/usuario/modificar-password", "/usuario/ver-perfil", "/usuario/ver-ciudades-disponibles").hasAnyRole("ADMIN", "CLIENTE", "ESPECIALISTA")
                                .requestMatchers("/oficios").permitAll()
                                .requestMatchers("/trabajosExternos/**").permitAll()
                                .anyRequest().authenticated()
                )
                .csrf(AbstractHttpConfigurer::disable)
                .httpBasic(Customizer.withDefaults())
                .build();
    }
}
