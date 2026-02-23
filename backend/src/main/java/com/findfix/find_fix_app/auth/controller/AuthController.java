package com.findfix.find_fix_app.auth.controller;

import com.findfix.find_fix_app.auth.dto.*;
import com.findfix.find_fix_app.auth.service.GoogleAuthService;
import com.findfix.find_fix_app.usuario.dto.VerPerfilUsuarioDTO;
import com.findfix.find_fix_app.usuario.model.Usuario;
import com.findfix.find_fix_app.utils.apiResponse.ApiResponse;
import com.findfix.find_fix_app.auth.CookieUtil;
import com.findfix.find_fix_app.auth.service.AuthService;
import com.findfix.find_fix_app.utils.exception.exceptions.RolException;
import com.findfix.find_fix_app.utils.exception.exceptions.UsuarioException;
import com.findfix.find_fix_app.utils.exception.exceptions.UsuarioNotFoundException;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Validated
public class AuthController {

    private final AuthService authService;
    private final CookieUtil  cookieUtil;
    private final GoogleAuthService googleAuthService;

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponseDTO>> login(@Valid @RequestBody UsuarioLoginDTO loginDTO) throws UsuarioNotFoundException {
        AuthResponseDTO authResponse = authService.login(loginDTO);

        ResponseCookie cookie = cookieUtil.crearCookieRefresh(authResponse.getRefreshToken());
        authResponse.setRefreshToken(null);

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .body(new ApiResponse<>("Login exitoso, bienvenido!", authResponse));
    }

    @PostMapping("/login/google")
    public ResponseEntity<ApiResponse<AuthResponseDTO>> loginGoogle(
            @RequestBody GoogleTokenDTO request,
            HttpServletResponse response // Necesario para la cookie
    ) {
        AuthResponseDTO authResponse = googleAuthService.loginWithGoogle(request.getToken());

        ResponseCookie cookie = cookieUtil.crearCookieRefresh(authResponse.getRefreshToken());
        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());

        authResponse.setRefreshToken(null);

        return ResponseEntity.ok(new ApiResponse<>("Login con Google exitoso", authResponse));
    }

    //metodo para que un usuario se registre en el sistema
    @PostMapping("/registrar")
    public ResponseEntity<ApiResponse<VerPerfilUsuarioDTO>> registrarUsuario(@Valid @RequestBody RegistroDTO registro) throws UsuarioException, RolException {
        Usuario user = authService.registrarNuevoUsuario(registro);

        return ResponseEntity.status(HttpStatus.CREATED).body(new ApiResponse<>(
                "Usuario registrado con éxito✅",
                new VerPerfilUsuarioDTO(user)));
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<ApiResponse<TokenRefreshResponseDTO>> refresh(
            @CookieValue(name = "refreshToken", required = false) String refreshToken) {

        if (refreshToken == null) {
            return ResponseEntity.badRequest().body(new ApiResponse<>("Error: No se encontró la cookie de refresco", null));
        }

        TokenRefreshResponseDTO tokenDto = authService.refrescarToken(refreshToken);

        return ResponseEntity.ok(new ApiResponse<>("Token refrescado correctamente", tokenDto));
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logout() {
        ResponseCookie cookie = cookieUtil.limpiarCookie();
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .body(new ApiResponse<>("Sesión cerrada exitosamente", null));
    }
}
