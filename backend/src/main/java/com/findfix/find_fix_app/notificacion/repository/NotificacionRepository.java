package com.findfix.find_fix_app.notificacion.repository;

import com.findfix.find_fix_app.notificacion.model.Notificacion;
import com.findfix.find_fix_app.usuario.model.Usuario;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface NotificacionRepository extends JpaRepository<Notificacion, Long> {
    List<Notificacion> findByUsuario_UsuarioIdAndRolDestinatarioOrderByFechaCreacionDesc(Long usuarioId, String rolDestinatario);
    long countByUsuarioAndLeidaFalse(Usuario usuario);
}