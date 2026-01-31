package com.findfix.find_fix_app.utils.auth.service;

import com.findfix.find_fix_app.rol.model.Rol;
import com.findfix.find_fix_app.rol.repository.RolRepository;
import com.findfix.find_fix_app.utils.auth.dto.AuthResponseDTO;
import com.findfix.find_fix_app.utils.auth.dto.RegistroDTO;
import com.findfix.find_fix_app.utils.auth.dto.TokenRefreshResponseDTO;
import com.findfix.find_fix_app.utils.auth.dto.UsuarioLoginDTO;
import com.findfix.find_fix_app.utils.auth.model.RefreshToken;
import com.findfix.find_fix_app.utils.enums.CiudadesDisponibles;
import com.findfix.find_fix_app.utils.exception.exceptions.RolException;
import com.findfix.find_fix_app.utils.exception.exceptions.UsuarioException;
import com.findfix.find_fix_app.utils.exception.exceptions.UsuarioNotFoundException;
import com.findfix.find_fix_app.usuario.model.Usuario;
import com.findfix.find_fix_app.usuario.repository.UsuarioRepository;
import com.findfix.find_fix_app.utils.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UsuarioRepository usuarioRepository;
    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder passwordEncoder;
    private final RolRepository rolRepository;

    private final JwtService jwtService;
    private final RefreshTokenService refreshTokenService;

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
            throw new UsuarioException("❗Ya existe un usuario registrado con ese email.");
        }

        Usuario usuario = new Usuario();

        usuario.setEmail(registroDTO.email());
        usuario.setPassword(passwordEncoder.encode(registroDTO.password()));
        usuario.setNombre(registroDTO.nombre());
        usuario.setApellido(registroDTO.apellido());
        usuario.setCiudad(CiudadesDisponibles.NO_ESPECIFICADO);
        usuario.setTelefono("No especificado.");

        Rol rol = rolRepository.findByNombre("CLIENTE")
                .orElseThrow(() -> new RolException("❌Rol no encontrado❌"));

        usuario.getRoles().add(rol);

        return usuarioRepository.save(usuario);
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
