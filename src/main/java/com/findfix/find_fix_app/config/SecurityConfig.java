package com.findfix.find_fix_app.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
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
                                //USUARIO
                                .requestMatchers("/usuario/registrar").permitAll()
                                .requestMatchers("/usuario/modificar-datos", "/usuario/modificar-password", "/usuario/ver-perfil", "/usuario/ver-ciudades-disponibles").hasAnyRole("ADMIN", "CLIENTE", "ESPECIALISTA")
                                .requestMatchers("/usuario/**", "/roles/**").hasRole("ADMIN")
                                //OFICIOS
                                .requestMatchers("/oficios/**").hasRole("ADMIN")
                                //TRABAJOS EXTERNOS
                                .requestMatchers("/trabajos-externos/**").hasRole("ESPECIALISTA")
                                //TRABAJO APP
                                .requestMatchers("/trabajos-app/cliente/mis-trabajos").hasRole("CLIENTE")
                                .requestMatchers("/trabajos-app/**").hasRole("ESPECIALISTA")
                                //TRABAJOS
                                .requestMatchers("/trabajos").hasRole("ESPECIALISTA")
                                //SOLICITUD TRABAJO
                                .requestMatchers("/solicitud-trabajo/registrar-solicitud", "/solicitud-trabajo/mis-solicitudes-enviadas", "/solicitud-trabajo/eliminar-solicitud/").hasRole("CLIENTE")
                                .requestMatchers("/solicitud-trabajo/mis-solicitudes-recibidas", "/solicitud-trabajo/actualizar-estado/").hasRole("ESPECIALISTA")
                                .requestMatchers("/solicitud-trabajo/filtrar", "/solicitud-trabajo/{id}").hasAnyRole("CLIENTE", "ESPECIALISTA")
                                //FAVORITOS
                                .requestMatchers("/favoritos/**").hasRole("CLIENTE")
                                //SOLICITUD ESPECIALISTA
                                .requestMatchers("/solicitud-especialista/mis-solicitudes", "/solicitud-especialista/enviar-solicitud", "/solicitud-especialista/{id}").hasAnyRole("CLIENTE", "ESPECIALISTA")
                                .requestMatchers("/solicitud-especialista/filtrar").hasAnyRole("CLIENTE", "ESPECIALISTA", "ADMIN")
                                .requestMatchers("/solicitud-especialista").hasRole("ADMIN")
                                .requestMatchers(HttpMethod.PATCH, "/solicitud-especialista/{id}").hasRole("ADMIN")
                                //ESPECIALISTAS
                                .requestMatchers("/especialista/disponibles").hasRole("CLIENTE")
                                .requestMatchers(HttpMethod.PATCH,"/especialista", "/especialista/actualizar-oficios", "/especialista/ver-perfil").hasRole("ESPECIALISTA")
                                .requestMatchers("/especialista/filtrar").hasAnyRole("CLIENTE", "ESPECIALISTA", "ADMIN")
                                .requestMatchers(HttpMethod.GET, "/especialista").hasRole("ADMIN")
                                .requestMatchers( HttpMethod.DELETE,"/especialista/{email}").hasRole("ADMIN")
                                .requestMatchers( HttpMethod.PATCH, "/especialista/{email}").hasRole("ADMIN")
                                .requestMatchers("/especialista/actualizar-oficios/{email} ").hasRole("ADMIN")
                                .anyRequest().authenticated()
                )
                .exceptionHandling(exception -> exception
                        // Manejar 401 Unauthorized (credenciales inválidas o no proporcionadas)
                        .authenticationEntryPoint((request, response, authException) -> {
                            response.setStatus(HttpStatus.UNAUTHORIZED.value());
                            response.setContentType("application/json");
                            response.getWriter().write("""
                        {
                            "error": "No autorizado",
                            "mensaje": "Credenciales inválidas o faltantes. Por favor, inicia sesión o registrate."
                        }
                        """);
                        })
                        // Manejar 403 Forbidden (sin permisos debidos)
                        .accessDeniedHandler((request, response, accessDeniedException) -> {
                            response.setStatus(HttpStatus.FORBIDDEN.value());
                            response.setContentType("application/json");
                            response.getWriter().write("""
                        {
                            "error": "Acceso denegado",
                            "mensaje": "No tenés permisos para acceder a este recurso."
                        }
                        """);
                        })
                )
                .csrf(AbstractHttpConfigurer::disable)
                .httpBasic(Customizer.withDefaults())
                .build();
    }
}
