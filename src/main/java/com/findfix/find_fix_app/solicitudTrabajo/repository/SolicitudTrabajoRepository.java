package com.findfix.find_fix_app.solicitudTrabajo.repository;

import com.findfix.find_fix_app.especialista.model.Especialista;
import com.findfix.find_fix_app.solicitudTrabajo.model.SolicitudTrabajo;
import com.findfix.find_fix_app.usuario.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SolicitudTrabajoRepository extends JpaRepository<SolicitudTrabajo,Long>, JpaSpecificationExecutor<SolicitudTrabajo> {
    List<SolicitudTrabajo> findByUsuario(Usuario usuario);
    List<SolicitudTrabajo> findByEspecialista(Especialista especialista);

}
