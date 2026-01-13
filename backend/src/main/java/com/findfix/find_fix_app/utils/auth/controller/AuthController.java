package com.findfix.find_fix_app.utils.auth.controller;

import com.findfix.find_fix_app.usuario.dto.VerPerfilUsuarioDTO;
import com.findfix.find_fix_app.usuario.model.Usuario;
import com.findfix.find_fix_app.utils.apiResponse.ApiResponse;
import com.findfix.find_fix_app.utils.auth.dto.RegistroDTO;
import com.findfix.find_fix_app.utils.auth.dto.UsuarioLoginDTO;
import com.findfix.find_fix_app.utils.auth.service.AuthService;
import com.findfix.find_fix_app.utils.exception.exceptions.RolException;
import com.findfix.find_fix_app.utils.exception.exceptions.UsuarioException;
import com.findfix.find_fix_app.utils.exception.exceptions.UsuarioNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Validated
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<VerPerfilUsuarioDTO>> login(@Valid @RequestBody UsuarioLoginDTO loginDTO, HttpServletRequest request) throws UsuarioNotFoundException {
        Usuario usuario = authService.login(loginDTO);

        HttpSession session = request.getSession(true);
        session.setAttribute("SPRING_SECURITY_CONTEXT", SecurityContextHolder.getContext());

        return ResponseEntity.ok(new ApiResponse<>("Login exitoso, bienvenido!", new VerPerfilUsuarioDTO(usuario)));
    }

    //metodo para que un usuario se registre en el sistema
    @PostMapping("/registrar")
    public ResponseEntity<ApiResponse<VerPerfilUsuarioDTO>> registrarUsuario(@Valid @RequestBody RegistroDTO registro) throws UsuarioException, RolException {
        Usuario user = authService.registrarNuevoUsuario(registro);

        return ResponseEntity.status(HttpStatus.CREATED).body(new ApiResponse<>(
                "Usuario registrado con éxito✅",
                new VerPerfilUsuarioDTO(user)));
    }
}
