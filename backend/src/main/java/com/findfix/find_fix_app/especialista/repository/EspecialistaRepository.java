package com.findfix.find_fix_app.especialista.repository;

import com.findfix.find_fix_app.especialista.model.Especialista;
import com.findfix.find_fix_app.usuario.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import java.util.Optional;


@Repository
public interface EspecialistaRepository extends JpaRepository<Especialista, Long> , JpaSpecificationExecutor<Especialista> {
    Optional<Especialista> findByUsuarioEmail(String email);
    boolean existsByDni(Long dni);
    Optional<Especialista> findByUsuario(Usuario usuario);
}
