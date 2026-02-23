package com.findfix.find_fix_app.solicitudEspecialista.repository;

import com.findfix.find_fix_app.usuario.model.Usuario;
import com.findfix.find_fix_app.solicitudEspecialista.model.SolicitudEspecialista;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;
@Repository
public interface SolicitudEspecialistaRepository extends JpaRepository<SolicitudEspecialista, Long>, JpaSpecificationExecutor<SolicitudEspecialista> {
    List<SolicitudEspecialista> findByUsuarioEmail(String email);
    List<SolicitudEspecialista> findByUsuario(Usuario usuario);
    @Query("SELECT s FROM SolicitudEspecialista s ORDER BY " +
           "CASE WHEN str(s.estado) = 'PENDIENTE' THEN 1 ELSE 2 END ASC, " +
           "s.fechaSolicitud DESC")
    Page<SolicitudEspecialista> findAllCustomOrder(Pageable pageable);
}
