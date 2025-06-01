package com.findfix.find_fix_app.solicitudEspecialista.repository;

import com.findfix.find_fix_app.solicitudEspecialista.model.SolicitudEspecialista;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SolicitudEspecialistaRepository extends JpaRepository<SolicitudEspecialista,Long> {
}
