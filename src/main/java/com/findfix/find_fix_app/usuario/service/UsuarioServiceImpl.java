package com.findfix.find_fix_app.usuario.service;

import com.findfix.find_fix_app.exception.exceptions.UserNotFoundException;
import com.findfix.find_fix_app.rol.model.Rol;
import com.findfix.find_fix_app.rol.repository.RolRepository;
import com.findfix.find_fix_app.usuario.dto.ActualizarPasswordDTO;
import com.findfix.find_fix_app.usuario.dto.ActualizarUsuarioDTO;
import com.findfix.find_fix_app.usuario.dto.RegistroDTO;
import com.findfix.find_fix_app.usuario.model.Usuario;
import com.findfix.find_fix_app.usuario.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UsuarioServiceImpl implements UsuarioService, UserDetailsService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final RolRepository rolRepository;

    // metodo para buscar un usuario por email
    @Override
    public Optional<Usuario> buscarPorEmail(String email) {
        return usuarioRepository.findByEmail(email);
    }

    // metodo para obtener lista completa de usuarios
    @Override
    public List<Usuario> obtenerUsuarios() {
        return usuarioRepository.findAll();
    }

    // metodo para buscar un usuario por id
    @Override
    public Optional<Usuario> buscarPorId(Long id) {
        return usuarioRepository.findById(id);
    }

    //metodo para registrar un usuario nuevo
    @Override
    public Usuario registrarNuevoUsuario(RegistroDTO registroDTO) {

        if (usuarioRepository.findByEmail(registroDTO.email()).isPresent()) {
            throw new IllegalArgumentException("Ya existe un usuario registrado con ese email.");
        }

        Usuario usuario = new Usuario();

        usuario.setEmail(registroDTO.email());
        usuario.setPassword(passwordEncoder.encode(registroDTO.password()));
        usuario.setNombre(registroDTO.nombre());
        usuario.setApellido(registroDTO.apellido());

        //Se registra con rol cliente por default
        Rol rol = rolRepository.findByName("CLIENTE")
                .orElseThrow(() -> new RuntimeException("Rol no encontrado."));

        usuario.getRoles().add(rol);

        return usuarioRepository.save(usuario);
    }

    // metodo para eliminar un usuario por id
    @Override
    public void eliminar(Long id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("Usuario no encontrado."));

        usuarioRepository.delete(usuario);
    }

    // metodo para actualizar la contraseña de un usuario
    @Override
    public void actualizarPassword(ActualizarPasswordDTO actualizarPasswordDTO) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();

        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("Usuario no encontrado."));

        if (!passwordEncoder.matches(actualizarPasswordDTO.passwordActual(), usuario.getPassword())) {
            throw new IllegalArgumentException("La contraseña actual es incorrecta.");
        }

        usuario.setPassword(passwordEncoder.encode(actualizarPasswordDTO.passwordActual()));
        usuarioRepository.save(usuario);
    }

    // metodo para actualizar atributos de un usuario
    @Override
    public void actualizarUsuario(ActualizarUsuarioDTO actualizarUsuarioDTO) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();

        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("Usuario no encontrado."));

        if (actualizarUsuarioDTO.nombre() != null) {
            usuario.setNombre(actualizarUsuarioDTO.nombre());
        }
        if (actualizarUsuarioDTO.apellido() != null) {
            usuario.setApellido(actualizarUsuarioDTO.apellido());
        }
        if (actualizarUsuarioDTO.telefono() != null) {
            usuario.setTelefono(actualizarUsuarioDTO.telefono());
        }
        if (actualizarUsuarioDTO.ciudad() != null) {
            usuario.setCiudad(actualizarUsuarioDTO.ciudad());
        }

        usuarioRepository.save(usuario);
    }

    // Metodo para buscar un usuario por su email para autenticacion
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<Usuario> userOptional = usuarioRepository.findByEmail(username);
        Usuario usuario = userOptional.orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado."));

        // retorna un user de spring security
        return new org.springframework.security.core.userdetails.User(
                usuario.getEmail(),
                usuario.getPassword(),
                getAuthorities(usuario.getRoles())
        );
    }


    // Metodo que convierte los roles del usuario en autoridades para spring security
    private Collection<? extends GrantedAuthority> getAuthorities(Set<Rol> roles) {
        // Prefijo "ROLE_" es requerido por Spring Security para roles
        return roles.stream()
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role.getNombre()))
                .collect(Collectors.toList());
    }
}
