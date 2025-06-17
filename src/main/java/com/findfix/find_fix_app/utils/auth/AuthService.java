package com.findfix.find_fix_app.utils.auth;

import com.findfix.find_fix_app.utils.exception.exceptions.UserNotFoundException;
import com.findfix.find_fix_app.usuario.model.Usuario;
import com.findfix.find_fix_app.usuario.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UsuarioRepository usuarioRepository;

    public String obtenerEmailUsuarioAutenticado() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication.getName();
    }

    public Usuario obtenerUsuarioAutenticado() throws UserNotFoundException {
        String email = obtenerEmailUsuarioAutenticado();
        return usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("Usuario no encontrado"));
    }
}
