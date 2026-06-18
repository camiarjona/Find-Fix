package com.findfix.find_fix_app.trabajo.fotoTrabajo.repository;

import com.findfix.find_fix_app.trabajo.fotoTrabajo.model.FotoTrabajo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FotoTrabajoRepository extends JpaRepository<FotoTrabajo, Long> {

}