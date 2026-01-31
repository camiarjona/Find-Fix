package com.findfix.find_fix_app.utils.auth.service;

import com.findfix.find_fix_app.rol.model.Rol;
import com.findfix.find_fix_app.rol.repository.RolRepository;
import com.findfix.find_fix_app.utils.auth.dto.RegistroDTO;
import com.findfix.find_fix_app.utils.auth.dto.UsuarioLoginDTO;
import com.findfix.find_fix_app.utils.exception.exceptions.RolException;
import com.findfix.find_fix_app.utils.exception.exceptions.UsuarioException;
import com.findfix.find_fix_app.utils.exception.exceptions.UsuarioNotFoundException;
import com.findfix.find_fix_app.usuario.model.Usuario;
import com.findfix.find_fix_app.usuario.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService{

    private final UsuarioRepository usuarioRepository;
    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder passwordEncoder;
    private final RolRepository rolRepository;

    @Override
    public String obtenerEmailUsuarioAutenticado() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication.getName();
    }

    @Override
    public Usuario obtenerUsuarioAutenticado() throws UsuarioNotFoundException {
        String email = obtenerEmailUsuarioAutenticado();
        return usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new UsuarioNotFoundException("Usuario no encontrado"));
    }

    // LOGIN: Autentica y devuelve el Usuario
    @Override
    public Usuario login(UsuarioLoginDTO loginDTO) throws UsuarioNotFoundException {
        Authentication authenticationRequest = new UsernamePasswordAuthenticationToken(loginDTO.getEmail(), loginDTO.getPassword());
        Authentication authenticationResponse = authenticationManager.authenticate(authenticationRequest);

        SecurityContextHolder.getContext().setAuthentication(authenticationResponse);

        return obtenerUsuarioAutenticado();
    }

    // metodo para registrar un nuevo user
    @Transactional(rollbackFor = Exception.class)
    @Override
    public Usuario registrarNuevoUsuario(RegistroDTO registroDTO) throws RolException, UsuarioException {

        if (usuarioRepository.findByEmail(registroDTO.email()).isPresent()) {
            throw new UsuarioException("❗Ya existe un usuario registrado con ese email.");
        }

        Usuario usuario = new Usuario();

        usuario.setEmail(registroDTO.email());
        usuario.setPassword(passwordEncoder.encode(registroDTO.password()));
        usuario.setNombre(registroDTO.nombre());
        usuario.setApellido(registroDTO.apellido());

        if (registroDTO.ciudad() != null) {
            usuario.setCiudad(registroDTO.ciudad());
        } else {
            usuario.setCiudad("No especificado");
        }

        if (registroDTO.latitud() != null && registroDTO.longitud() != null) {
            usuario.setLatitud(registroDTO.latitud());
            usuario.setLongitud(registroDTO.longitud());
        }

        usuario.setTelefono("No especificado.");

        Rol rol = rolRepository.findByNombre("CLIENTE")
                .orElseThrow(() -> new RolException("❌Rol no encontrado❌"));

        usuario.getRoles().add(rol);

        return usuarioRepository.save(usuario);
    }

}
