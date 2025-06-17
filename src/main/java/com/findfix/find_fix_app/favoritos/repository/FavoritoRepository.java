package com.findfix.find_fix_app.favoritos.repository;

import com.findfix.find_fix_app.especialista.model.Especialista;
import com.findfix.find_fix_app.favoritos.model.Favorito;
import com.findfix.find_fix_app.usuario.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FavoritoRepository extends JpaRepository<Favorito, Integer> {
    boolean existsByUsuarioAndEspecialista(Usuario usuario, Especialista especialista);
    List<Favorito> findAllByUsuario(Usuario usuario);
    void deleteByUsuarioAndEspecialista(Usuario usuario, Especialista especialista);
}
