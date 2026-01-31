package com.findfix.find_fix_app.utils.auth.service;

import com.findfix.find_fix_app.utils.auth.model.RefreshToken;
import org.springframework.transaction.annotation.Transactional;

public interface RefreshTokenService {
    RefreshToken findByToken(String token);

    // crea un nuevo token (login)
    RefreshToken createRefreshToken(Long userId);

    // verifica fecha de vencimiento
    RefreshToken verifyExpiration(RefreshToken token);

    // cierra sesion
    void deleteByToken(String token);

    // elimina todos los tokens correspondientes a un usuario
    void deleteByUsuarioId(Long userId);
}
