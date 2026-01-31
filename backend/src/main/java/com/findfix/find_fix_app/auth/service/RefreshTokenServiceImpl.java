package com.findfix.find_fix_app.auth.service;

import com.findfix.find_fix_app.usuario.model.Usuario;
import com.findfix.find_fix_app.usuario.repository.UsuarioRepository;
import com.findfix.find_fix_app.auth.model.RefreshToken;
import com.findfix.find_fix_app.auth.repository.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RefreshTokenServiceImpl implements RefreshTokenService {

    @Value("${jwt.refresh-expiration-ms}")
    //duracion en milisegundos
    private Long refreshTokenDurationMs;

    private final RefreshTokenRepository refreshTokenRepository;
    private final UsuarioRepository usuarioRepository;

    // busca un token especifico
    @Override
    public RefreshToken findByToken(String token) {
        return refreshTokenRepository.findByToken(token).orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Refresh token no encontrado"));
    }

    // crea un nuevo token (login)
    @Transactional
    @Override
    public RefreshToken createRefreshToken(Long userId) {
        //Validamos el usuario
        Usuario usuario = usuarioRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado"));

        // verificamos la cantidad de tokens que tiene ese user en la bdd
        List<RefreshToken> tokensActivos = refreshTokenRepository.findByUsuario(usuario);

        //si ya tiene 5 sesiones abiertas en simultaneo
        if (tokensActivos.size() >= 5) {
            // buscamos el token que vence primero
            RefreshToken tokenMasViejo = tokensActivos.stream()
                    .min(Comparator.comparing(RefreshToken::getExpiryDate))
                    .orElseThrow();

            //borramos para liberar espacio en la bdd
            refreshTokenRepository.delete(tokenMasViejo);

            // esto obliga a la bdd a ejecutar el borrado inmediatamente antes de continuar
            refreshTokenRepository.flush();
        }
        // creamos el nuevo token

        RefreshToken refreshToken = RefreshToken.builder()
                .usuario(usuario)
                .expiryDate(Instant.now().plusMillis(refreshTokenDurationMs))
                .token(UUID.randomUUID().toString()) // generamos la key
                .build();

        // guardamos en la bdd
        return refreshTokenRepository.save(refreshToken);
    }

    // verifica fecha de vencimiento
    @Override
    public RefreshToken verifyExpiration(RefreshToken token) {
        // Comparamos fechas
        if (token.getExpiryDate().compareTo(Instant.now()) < 0) {
            // Si venció, lo borramos de la BD
            refreshTokenRepository.delete(token);
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Refresh token expirado. Inicie sesión nuevamente.");
        }
        // Si no venció, devolvemos el token intacto
        return token;
    }

    // cierra sesion
    @Transactional
    @Override
    public void deleteByToken(String token) {
        // Borra solo la fila que coincide con este token string
        refreshTokenRepository.deleteByToken(token);
    }

    // elimina todos los tokens correspondientes a un usuario
    @Transactional
    @Override
    public void deleteByUsuarioId(Long userId) {
        Usuario usuario = usuarioRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado"));
        refreshTokenRepository.deleteByUsuario(usuario);
    }
}
