package com.findfix.find_fix_app.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http.authorizeHttpRequests(
                        request -> request
                                .requestMatchers("/usuario/registrar").permitAll()
                                .requestMatchers("/usuario", "/roles/**", "/usuario/eliminar").hasRole("ADMIN")
                                .requestMatchers("/usuario/modificar-datos", "/usuario/modificar-password").hasAnyRole("ADMIN", "CLIENTE", "ESPECIALISTA")
                                .requestMatchers("/oficios").permitAll()
                                .requestMatchers("/trabajosExternos/**").permitAll()
                                .requestMatchers("/trabajosApp/misTrabajosC").hasRole("CLIENTE")
                                .requestMatchers("/trabajosApp/**").hasRole("ESPECIALISTA")
                                .requestMatchers("/resenas", "/resenas/trabajo/**", "/resenas/mis-resenas", "/resenas/{id}").hasRole("CLIENTE")
                                .requestMatchers("/resenas", "/resenas/trabajo/**", "/resenas/titulo", "/resenas/mis-resenas", "/resenas/mis-trabajos", "/resenas/{id}").hasRole("ESPECIALISTA")
                                .anyRequest().authenticated()
                )
                .csrf(AbstractHttpConfigurer::disable)
                .httpBasic(Customizer.withDefaults())
                .build();
    }
}
