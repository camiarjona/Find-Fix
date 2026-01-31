package com.findfix.find_fix_app.auth.repository;

import com.findfix.find_fix_app.usuario.model.Usuario;
import com.findfix.find_fix_app.auth.model.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    // Permite obtener el token cuando nos llega la cookie
    Optional<RefreshToken> findByToken(String token);

    // Permite obtener todos los tokens de un usuario
    List<RefreshToken> findByUsuario(Usuario usuario);

    // Borra la sesion actual
    @Modifying
    void deleteByToken(String token);

    // Borra todas las sesiones
    @Modifying
    void deleteByUsuario(Usuario usuario);
}
