package com.findfix.find_fix_app.auth.repository;

import com.findfix.find_fix_app.auth.model.TokenConfirmacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TokenConfirmacionRepository extends JpaRepository<TokenConfirmacion, Long> {

    Optional<TokenConfirmacion> findByToken(String token);
}
