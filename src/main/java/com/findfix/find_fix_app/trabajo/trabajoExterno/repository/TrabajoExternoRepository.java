package com.findfix.find_fix_app.trabajo.trabajoExterno.repository;

import com.findfix.find_fix_app.especialista.model.Especialista;
import com.findfix.find_fix_app.trabajo.trabajoExterno.model.TrabajoExterno;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TrabajoExternoRepository extends JpaRepository<TrabajoExterno,Long>, JpaSpecificationExecutor<TrabajoExterno> {
    List<TrabajoExterno> findByEspecialista(Especialista especialistaId);
    Optional<TrabajoExterno> findByTitulo(String titulo);
}
