package com.findfix.find_fix_app.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.Customizer;
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

                                .requestMatchers(
                                        "/usuario/modificar-datos",
                                        "/usuario/modificar-password",
                                        "/usuario/ver-perfil",
                                        "/usuario/ver-ciudades-disponibles")
                                .hasAnyRole("ADMIN", "CLIENTE", "ESPECIALISTA")

                                .requestMatchers("/usuario/**").hasRole("ADMIN")

                                // DELETE | OFICIOS | ROLES
                                .requestMatchers("/admin/**", "/oficios/**", "/roles/**").hasRole("ADMIN") // todo admin

                                //TRABAJOS EXTERNOS (para especialistas)
                                .requestMatchers("/trabajos-externos/**").hasRole("ESPECIALISTA")

                                //TRABAJO APP
                                .requestMatchers(
                                        "/trabajos-app/cliente/mis-trabajos",
                                        "/trabajos-app/cliente/ficha-trabajo/{id}",
                                        "/trabajos-app/cliente/filtrar/{estado}")
                                .hasRole("CLIENTE")

                                .requestMatchers("/trabajos-app/**").hasRole("ESPECIALISTA")

                                //TRABAJOS
                                .requestMatchers("/trabajos").hasRole("ESPECIALISTA")

                                //SOLICITUD TRABAJO
                                .requestMatchers(
                                        "/solicitud-trabajo/registrar",
                                        "/solicitud-trabajo/enviadas/mis-solicitudes",
                                        "/solicitud-trabajo/eliminar/{id}",
                                        "/solicitud-trabajo/filtrar/enviadas")
                                .hasRole("CLIENTE")

                                .requestMatchers("/solicitud-trabajo/{id}").hasAnyRole("ESPECIALISTA", "CLIENTE")

                                .requestMatchers(
                                        "/solicitud-trabajo/recibidas/mis-solicitudes",
                                        "/solicitud-trabajo/actualizar-estado/{id}",
                                        "/solicitud-trabajo/filtrar/recibidas")
                                .hasRole("ESPECIALISTA")

                                //FAVORITOS
                                .requestMatchers("/favoritos/**").hasRole("CLIENTE")

                                //SOLICITUD ESPECIALISTA
                                .requestMatchers(
                                        "/solicitud-especialista/mis-solicitudes",
                                        "/solicitud-especialista/enviar",
                                        "/solicitud-especialista/eliminar/{id}")
                                .hasRole("CLIENTE")

                                .requestMatchers("/solicitud-especialista/filtrar").hasAnyRole("CLIENTE", "ADMIN")

                                .requestMatchers(
                                        "/solicitud-especialista",
                                        "/solicitud-especialista/actualizar/{id}")
                                .hasRole("ADMIN")

                                .requestMatchers(HttpMethod.PATCH, "/solicitud-especialista/{id}").hasRole("ADMIN")
                                //ESPECIALISTAS
                                .requestMatchers("/especialistas/disponibles").hasAnyRole("CLIENTE", "ESPECIALISTA")

                                .requestMatchers(
                                        "/especialistas/ver-perfil",
                                        "/especialistas/actualizar/mis-datos",
                                        "/especialistas/actualizar/mis-oficios")
                                .hasRole("ESPECIALISTA")

                                .requestMatchers("/especialistas/filtrar").hasAnyRole("CLIENTE", "ESPECIALISTA", "ADMIN")

                                .requestMatchers(
                                        "/especialistas",
                                        "/especialistas/actualizar/{email}",
                                        "/especialistas/actualizar/oficios/{email}",
                                        "/especialistas/eliminar/{email}")
                                .hasRole("ADMIN")

                                //RESEÑA
                                .requestMatchers(
                                        "/resenas/registrar",
                                        "/resenas/buscar/{id}",
                                        "/resenas/enviadas")
                                .hasAnyRole("CLIENTE", "ESPECIALISTA")

                                .requestMatchers("/resenas/eliminar/{id}").hasRole("CLIENTE")

                                .requestMatchers(
                                        "/resenas/trabajo/{titulo}",
                                        "/resenas/recibidas")
                                .hasRole("ESPECIALISTA")

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
