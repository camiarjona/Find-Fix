package com.findfix.find_fix_app.resena.repository;

import com.findfix.find_fix_app.especialista.model.Especialista;
import com.findfix.find_fix_app.resena.model.Resena;
import com.findfix.find_fix_app.usuario.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ResenaRepository extends JpaRepository<Resena,Long> {

    Optional<Resena> findByTrabajoApp_Titulo(String titulo);

    List<Resena> findAllByTrabajoApp_Especialista(Especialista especialista);

    List<Resena> findAllByTrabajoApp_Usuario(Usuario usuario);
}
