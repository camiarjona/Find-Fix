package com.findfix.find_fix_app.trabajo.trabajoApp.repository;

import com.findfix.find_fix_app.enums.EstadosTrabajos;
import com.findfix.find_fix_app.rol.model.Rol;
import com.findfix.find_fix_app.trabajo.trabajoApp.model.TrabajoApp;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TrabajoAppRepository extends JpaRepository<TrabajoApp,Long> {
    /// Optional<TrabajoApp> findByEstado(EstadosTrabajos estadosTrabajos);
    Optional<TrabajoApp> findByTitulo(String titulo);


}
