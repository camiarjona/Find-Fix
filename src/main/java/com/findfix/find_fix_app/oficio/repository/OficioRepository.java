package com.findfix.find_fix_app.oficio.repository;

import com.findfix.find_fix_app.oficio.model.Oficio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OficioRepository extends JpaRepository<Oficio, Long> {
}
