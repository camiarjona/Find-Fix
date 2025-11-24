package com.findfix.find_fix_app.config;

import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import jakarta.servlet.http.Cookie;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableMethodSecurity(securedEnabled = true)
public class SecurityConfig {
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
                .cors(Customizer.withDefaults())
                .authorizeHttpRequests(
                        request -> request

                                .requestMatchers("/usuario/registrar", "/usuario/login", "/usuario/logout","/especialistas/publico").permitAll()
                                
                                .requestMatchers(
                                        "/usuario/modificar-datos",
                                        "/usuario/modificar-password",
                                        "/usuario/ver-perfil",
                                        "/usuario/ver-ciudades-disponibles")
                                .hasAnyRole("ADMIN", "CLIENTE", "ESPECIALISTA")

                                .requestMatchers("/usuario",
                                        "/usuario/modificar/{email}",
                                        "/usuario/filtrar")
                                .hasRole("ADMIN")

                                //OFICIOS ESPECIALISTA/CLIENTE
                                .requestMatchers("/oficios/disponibles").hasAnyRole("ESPECIALISTA", "CLIENTE")

                                .requestMatchers("/oficios").hasAnyRole("ESPECIALISTA", "CLIENTE", "ADMIN")

                                // DELETE | OFICIOS | ROLES
                                .requestMatchers("/admin/**",
                                        "/oficios/buscar/{id}",
                                        "/oficios/agregar",
                                        "/oficios/actualizar/{id}",
                                        "/oficios/eliminar/{id}",
                                        "/oficios/nombre/{nombre}",
                                        "/roles/**")
                                .hasRole("ADMIN")

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
                                        "/solicitud-especialista/eliminar/{id}",
                                        "/especialistas/detalle")
                                .hasRole("CLIENTE")

                                .requestMatchers("/solicitud-especialista/filtrar").hasAnyRole("CLIENTE", "ADMIN")

                                .requestMatchers(
                                        "/solicitud-especialista",
                                        "/solicitud-especialista/actualizar/{id}")
                                .hasRole("ADMIN")

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
                 .logout(logout -> logout
                        .logoutUrl("/usuario/logout")
                        .invalidateHttpSession(true)
                        .clearAuthentication(true)
                        .addLogoutHandler((request, response, authentication) -> {
                            // FUERZA BRUTA: Sobrescribir cookie manualmente
                            // Esto arregla problemas de 'Path' que a veces ocurren
                            Cookie cookie = new Cookie("JSESSIONID", null);
                            cookie.setPath("/"); // Importante: Asegura que borre la cookie global
                            cookie.setHttpOnly(true);
                            cookie.setMaxAge(0); // 0 segundos de vida = borrar inmediatamente
                            response.addCookie(cookie);
                            System.out.println(">>> LOGOUT: Cookie JSESSIONID borrada manualmente.");
                        })
                        .logoutSuccessHandler((request, response, authentication) -> {
                            response.setStatus(HttpStatus.OK.value());
                        })
                )
                .exceptionHandling(exception -> exception
                        // Tu exceptionHandling personalizado está perfecto, no lo toques.
                        // Ahora respetará la configuración de CORS.
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
                //.httpBasic(Customizer.withDefaults())
                .build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        configuration.setAllowedOrigins(List.of("http://localhost:4200"));

        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("*")); // Permite todos los headers

        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration); // Aplica a TODAS las rutas

        return source;
    }
}