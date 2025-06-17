package com.findfix.find_fix_app.solicitudEspecialista.repository;

import com.findfix.find_fix_app.utils.enums.EstadosSolicitudes;
import com.findfix.find_fix_app.solicitudEspecialista.model.SolicitudEspecialista;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface SolicitudEspecialistaRepository extends JpaRepository<SolicitudEspecialista, Long>, JpaSpecificationExecutor<SolicitudEspecialista> {
    List<SolicitudEspecialista> findByFechaSolicitud(LocalDate fecha);
    List<SolicitudEspecialista> findAllByEstado(EstadosSolicitudes estado);
    List<SolicitudEspecialista> findByUsuarioEmail(String email);
    List<SolicitudEspecialista> findByFechaSolicitudBetween(LocalDate fechaInicio, LocalDate fechaFin);

}
