package com.findfix.find_fix_app.auth.service;

import com.findfix.find_fix_app.auth.model.RefreshToken;

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
