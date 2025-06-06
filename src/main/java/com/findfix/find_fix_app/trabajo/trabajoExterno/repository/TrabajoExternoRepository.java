package com.findfix.find_fix_app.trabajo.trabajoExterno.repository;

import com.findfix.find_fix_app.trabajo.trabajoExterno.model.TrabajoExterno;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TrabajoExternoRepository extends JpaRepository<TrabajoExterno,Long> {
}
