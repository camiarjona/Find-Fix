package com.findfix.find_fix_app.usuario.service;

import com.findfix.find_fix_app.rol.model.Rol;
import com.findfix.find_fix_app.rol.repository.RolRepository;
import com.findfix.find_fix_app.usuario.dto.RegistroDTO;
import com.findfix.find_fix_app.usuario.model.Usuario;
import com.findfix.find_fix_app.usuario.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UsuarioServiceImpl implements UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final RolRepository rolRepository;

    @Override
    public Optional<Usuario> encontrarPorEmail(String email) {
        return usuarioRepository.findByEmail(email);
    }

    @Override
    public List<Usuario> obtenerUsuarios() {
        return usuarioRepository.findAll();
    }

    @Override
    public Optional<Usuario> encontrarPorId(Long id) {
        return usuarioRepository.findById(id);
    }

    //metodo para registrar un usuario nuevo
    @Override
    public Usuario guardar(RegistroDTO registroDTO) {
        Usuario usuario = new Usuario();

        usuario.setEmail(registroDTO.email());
        usuario.setPassword(passwordEncoder.encode(registroDTO.password()));
        usuario.setNombre(registroDTO.nombre());
        usuario.setApellido(registroDTO.apellido());

        //Se registra con rol cliente por default
        Rol rol = rolRepository.findByName("CLIENTE")
                        .orElseThrow(() -> new RuntimeException("Rol no encontrado"));

        usuario.getRoles().add(rol);

        return usuarioRepository.save(usuario);
    }
}
