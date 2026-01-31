package com.findfix.find_fix_app.config;

import com.findfix.find_fix_app.utils.security.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableMethodSecurity(securedEnabled = true)
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthFilter;
    private final AuthenticationProvider authenticationProvider;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(Customizer.withDefaults())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(
                        request -> request
                                //rutas públicas
                                .requestMatchers("/auth/**", "/especialistas/publico").permitAll()

                                //foto de perfil usuario
                                .requestMatchers(HttpMethod.POST, "/api/usuarios/foto/**").permitAll()
                                .requestMatchers(HttpMethod.GET, "/api/usuarios/foto/**").permitAll()

                                //rutas usuario comun
                                .requestMatchers(
                                        "/usuario/**")
                                .hasAnyRole("CLIENTE", "ESPECIALISTA")

                                //admin usuario controller
                                .requestMatchers("/admin/usuarios/**")
                                .hasRole("ADMIN")

                                //OFICIOS ESPECIALISTA/CLIENTE
                                .requestMatchers("/oficios/**").hasAnyRole("ESPECIALISTA", "CLIENTE", "ADMIN")

                                // OFICIOS ADMIN
                                .requestMatchers("/admin/oficios/**").hasAnyRole("ADMIN")

                                // ROLES ADMIN
                                .requestMatchers("/roles/**").hasRole("ADMIN")

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

                                //BARRIOS

                                .requestMatchers(HttpMethod.GET, "/api/barrios/**").permitAll()

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
                .authenticationProvider(authenticationProvider) // Usamos el provider de ApplicationConfig
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class) // Ponemos nuestro filtro
                .exceptionHandling(exception -> exception
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
                .build();
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