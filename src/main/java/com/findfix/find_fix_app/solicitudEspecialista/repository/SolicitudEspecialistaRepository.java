package com.findfix.find_fix_app.solicitudEspecialista.repository;

import com.findfix.find_fix_app.enums.EstadosSolicitudes;
import com.findfix.find_fix_app.solicitudEspecialista.model.SolicitudEspecialista;
import com.findfix.find_fix_app.usuario.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface SolicitudEspecialistaRepository extends JpaRepository<SolicitudEspecialista, Long>, JpaSpecificationExecutor<SolicitudEspecialista> {
    List<SolicitudEspecialista> findByUsuarioEmail(String email);
}
