package com.findfix.find_fix_app.trabajo.trabajoApp.repository;

import com.findfix.find_fix_app.especialista.model.Especialista;
import com.findfix.find_fix_app.trabajo.trabajoApp.model.TrabajoApp;
import com.findfix.find_fix_app.usuario.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TrabajoAppRepository extends JpaRepository<TrabajoApp,Long>, JpaSpecificationExecutor<TrabajoApp> {
    Optional<TrabajoApp> findByTitulo(String titulo);
    List<TrabajoApp> findByEspecialista(Especialista especialista);
    List<TrabajoApp> findByUsuario(Usuario usuario);


}
