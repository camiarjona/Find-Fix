package com.findfix.find_fix_app.auth.service;

import com.findfix.find_fix_app.auth.model.TokenConfirmacion;
import com.findfix.find_fix_app.auth.repository.TokenConfirmacionRepository;
import com.findfix.find_fix_app.rol.model.Rol;
import com.findfix.find_fix_app.rol.repository.RolRepository;
import com.findfix.find_fix_app.auth.dto.AuthResponseDTO;
import com.findfix.find_fix_app.auth.dto.RegistroDTO;
import com.findfix.find_fix_app.auth.dto.TokenRefreshResponseDTO;
import com.findfix.find_fix_app.auth.dto.UsuarioLoginDTO;
import com.findfix.find_fix_app.auth.model.RefreshToken;
import com.findfix.find_fix_app.notificacion.service.NotificacionService;
import com.findfix.find_fix_app.utils.exception.exceptions.RolException;
import com.findfix.find_fix_app.utils.exception.exceptions.UsuarioException;
import com.findfix.find_fix_app.utils.exception.exceptions.UsuarioNotFoundException;
import com.findfix.find_fix_app.usuario.model.Usuario;
import com.findfix.find_fix_app.usuario.repository.UsuarioRepository;
import com.findfix.find_fix_app.utils.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UsuarioRepository usuarioRepository;
    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder passwordEncoder;
    private final RolRepository rolRepository;
    private final NotificacionService notificacionService;
    private final JwtService jwtService;
    private final RefreshTokenService refreshTokenService;
    private final TokenConfirmacionRepository tokenConfirmacionRepository;

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
    public AuthResponseDTO login(UsuarioLoginDTO loginDTO) throws UsuarioNotFoundException {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginDTO.getEmail(), loginDTO.getPassword())
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);

        Usuario usuario = usuarioRepository.findByEmail(loginDTO.getEmail())
                .orElseThrow(() -> new UsuarioNotFoundException("Usuario no encontrado"));

        String accessToken = jwtService.generateToken(usuario);
        RefreshToken refreshToken = refreshTokenService.createRefreshToken(usuario.getUsuarioId());

        List<String> rolesNombres = usuario.getRoles().stream()
                .map(Rol::getNombre)
                .toList();

        return AuthResponseDTO.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken.getToken())
                .email(usuario.getEmail())
                .nombre(usuario.getNombre())
                .apellido(usuario.getApellido())
                .activo(usuario.isActivo())
                .id(usuario.getUsuarioId())
                .roles(rolesNombres)
                .build();
    }

    // metodo para registrar un nuevo user
    @Transactional(rollbackFor = Exception.class)
    @Override
    public Usuario registrarNuevoUsuario(RegistroDTO registroDTO) throws RolException, UsuarioException {

        if (usuarioRepository.findByEmail(registroDTO.email()).isPresent()) {
            throw new UsuarioException("Ya existe un usuario registrado con ese email.");
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
        usuario.setActivo(false);

        Rol rol = rolRepository.findByNombre("CLIENTE")
                .orElseThrow(() -> new RolException("Rol no encontrado"));

        usuario.getRoles().add(rol);
        Usuario usuarioGuardado = usuarioRepository.save(usuario);

        String tokenGenerado = java.util.UUID.randomUUID().toString();

        TokenConfirmacion token = new TokenConfirmacion(
                tokenGenerado,
                java.time.LocalDateTime.now(),
                java.time.LocalDateTime.now().plusMinutes(15), //expira en 15min
                usuarioGuardado
        );
        tokenConfirmacionRepository.save(token);

        notificacionService.notificarTokenRegistro(usuarioGuardado, tokenGenerado);

        return usuarioGuardado;
    }

    @Override
    public String confirmarCuenta(String token) {
            TokenConfirmacion tokenConfirmacion = tokenConfirmacionRepository.findByToken(token)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Token inválido o no encontrado."));

            if (tokenConfirmacion.getFechaConfirmacion() != null) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Esta cuenta ya ha sido confirmada anteriormente.");
            }

            if (tokenConfirmacion.getFechaExpiracion().isBefore(LocalDateTime.now())) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El enlace de confirmación ha expirado. Por favor, regístrate nuevamente o solicita un nuevo enlace.");
            }

            tokenConfirmacion.setFechaConfirmacion(LocalDateTime.now());
            tokenConfirmacionRepository.save(tokenConfirmacion);

            Usuario usuario = tokenConfirmacion.getUsuario();
            usuario.setActivo(true);
            usuarioRepository.save(usuario);

            notificacionService.notificarBienvenida(usuario, "CLIENTE");

            return "¡Cuenta confirmada exitosamente! Ya puedes iniciar sesión.";

    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public String reenviarTokenConfirmacion(String email) {
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "No se encontró un usuario con el email " + email + "."));

        if (usuario.isActivo()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Esta cuenta ya está activada. Ya podés iniciar sesión.");
        }

        String tokenGenerado = java.util.UUID.randomUUID().toString();

        TokenConfirmacion nuevoToken = new TokenConfirmacion(
                tokenGenerado,
                java.time.LocalDateTime.now(),
                java.time.LocalDateTime.now().plusMinutes(15),
                usuario
        );

        tokenConfirmacionRepository.save(nuevoToken);

        notificacionService.notificarTokenRegistro(usuario, tokenGenerado);

        return "Nuevo enlace de verificación enviado exitosamente a tu correo.";
    }

    @Override
    public TokenRefreshResponseDTO refrescarToken(String refreshTokenStr) {
        RefreshToken refreshToken = refreshTokenService.findByToken(refreshTokenStr);
        refreshTokenService.verifyExpiration(refreshToken);
        Usuario usuario = refreshToken.getUsuario();
        String nuevoAccessToken = jwtService.generateToken(usuario);

        return new TokenRefreshResponseDTO(nuevoAccessToken);
    }

}
