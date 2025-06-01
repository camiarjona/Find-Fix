package com.findfix.find_fix_app.solicitudTrabajo.repository;

import com.findfix.find_fix_app.solicitudTrabajo.model.SolicitudTrabajo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SolicitudTrabajoRepository extends JpaRepository<SolicitudTrabajo,Long> {
}
