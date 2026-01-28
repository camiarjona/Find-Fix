package com.findfix.find_fix_app.utils.initializer;

import com.findfix.find_fix_app.oficio.model.Oficio;
import com.findfix.find_fix_app.oficio.repository.OficioRepository;
import com.findfix.find_fix_app.rol.model.Rol;
import com.findfix.find_fix_app.rol.repository.RolRepository;
import com.findfix.find_fix_app.usuario.model.Usuario;
import com.findfix.find_fix_app.usuario.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private final RolRepository rolRepository;
    private final OficioRepository oficioRepository;
    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;


    @Override
    public void run(String... args) throws Exception {
        precargarRoles();
        precargarOficios();
        precargarAdmin();
    }

    private void precargarRoles() {
        List<String> roles = List.of("CLIENTE", "ESPECIALISTA", "ADMIN");

        for (String nombre : roles) {
            if (!rolRepository.existsByNombre(nombre)) {
                rolRepository.save(new Rol(null, nombre));
                log.info("✅ Rol precargado: {}", nombre);
            }
        }
    }

    private void precargarOficios() {
        List<String> oficios = List.of("ELECTRICISTA", "PLOMERO", "CARPINTERO", "PINTOR", "GASISTA");

        for (String nombre : oficios) {
            if (!oficioRepository.existsByNombreIgnoreCase(nombre)) {
                oficioRepository.save(new Oficio(null, nombre));
                log.info("🛠️ Oficio precargado: {}", nombre);
            }
        }
    }

    private void precargarAdmin() {
        String emailAdmin = "admin@admin.com";

        if (!usuarioRepository.existsByEmail(emailAdmin)) {
            Rol rolAdmin = rolRepository.findByNombre("ADMIN")
                    .orElseThrow(() -> new RuntimeException("Rol ADMIN no encontrado"));

            Usuario admin = new Usuario();
            admin.setEmail(emailAdmin);
            admin.setNombre("Administrador");
            admin.setApellido("Principal");
            admin.setPassword(passwordEncoder.encode("admin123")); // Cambialo si querés
            admin.setRoles(Set.of(rolAdmin));
            admin.setCiudad("No especificado");
            admin.setTelefono("No especificado");

            usuarioRepository.save(admin);
            log.info("✔Usuario ADMIN creado correctamente con email: admin@admin.com y contraseña: admin123");
        } else {
            log.info("ℹ️ Ya existe un usuario administrador con email: admin@admin.com y contraseña: admin123");
        }

    }
}
