package com.findfix.find_fix_app.auth.service; // Asegurate de que el package coincida

import com.findfix.find_fix_app.auth.model.PasswordResetToken;
import com.findfix.find_fix_app.auth.repository.PasswordTokenRepository;
import com.findfix.find_fix_app.usuario.model.Usuario;
import com.findfix.find_fix_app.usuario.repository.UsuarioRepository;
import com.findfix.find_fix_app.notificacion.service.NotificacionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PasswordResetServiceImpl implements PasswordResetService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordTokenRepository passwordTokenRepository;
    private final NotificacionService notificacionService;
    private final PasswordEncoder passwordEncoder;

    @Transactional(rollbackFor = Exception.class)
    @Override
    public String solicitarRecuperacionPassword(String email) {
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "No existe un usuario registrado con ese email."));

        passwordTokenRepository.deleteByUsuario(usuario);

        String tokenGenerado = UUID.randomUUID().toString();
        PasswordResetToken resetToken = new PasswordResetToken(tokenGenerado, usuario);
        passwordTokenRepository.save(resetToken);

        notificacionService.notificarRecuperacionPassword(usuario, tokenGenerado);

        return "Si el correo existe en nuestro sistema, te enviamos las instrucciones para recuperar tu contraseña.";
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public String restablecerPassword(String token, String nuevaPassword) {
        PasswordResetToken resetToken = passwordTokenRepository.findByToken(token)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "El enlace de recuperación es inválido o no existe."));

        if (resetToken.getExpiryDate().isBefore(LocalDateTime.now())) {
            passwordTokenRepository.delete(resetToken);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El enlace ha expirado. Por favor, solicitá uno nuevo.");
        }

        Usuario usuario = resetToken.getUsuario();
        usuario.setPassword(passwordEncoder.encode(nuevaPassword));
        usuarioRepository.save(usuario);

        passwordTokenRepository.delete(resetToken);

        return "Tu contraseña ha sido actualizada exitosamente. Ya podés iniciar sesión con tu nueva clave.";
    }
}