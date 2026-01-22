package com.findfix.find_fix_app.notificacion.service;
import java.util.List;

import com.findfix.find_fix_app.notificacion.dto.NotificacionDTO;
import com.findfix.find_fix_app.notificacion.model.Notificacion;
import com.findfix.find_fix_app.trabajo.trabajoApp.model.TrabajoApp;
import com.findfix.find_fix_app.usuario.model.Usuario;

public interface NotificacionService {

    List<NotificacionDTO> obtenerMisNotificaciones(Usuario usuario);
    void marcarComoLeida(Long idNotificacion);


    void notificar(Usuario destinatario, String titulo, String mensaje);

    void notificarSolicitudCambioContrasena(Usuario usuario);
    void notificarCambioContrasenaExitoso(Usuario usuario);
    void notificarReporteRecibido(Usuario usuario);
    
    void notificarAdminNuevaSolicitudEspecialista(Usuario admin, String nombreSolicitante);
    void notificarAdminNuevoReporte(Usuario admin);
    
    void notificarResolucionSolicitudRol(Usuario usuario, boolean aprobado);
    void notificarConfirmacionSolicitudEnviada(Usuario cliente, String nombreEspecialista);
    void notificarRespuestaSolicitudTrabajo(Usuario cliente, String nombreEspecialista, boolean aceptada);
    void notificarCambioEstadoTrabajo(Usuario cliente, String estado, String nombreEspecialista);
    void notificarConfirmacionResenaRealizada(Usuario cliente, String nombreEspecialista);
    void notificarConfirmacionSolicitudEspecialistaEnviada(Usuario usuario);
    
    void notificarNuevaSolicitudTrabajoRecibida(Usuario especialista, String nombreCliente, String servicio);
    void notificarNuevoTrabajoCreado(Usuario especialista, String nombreCliente);
    void notificarConfirmacionTrabajoFinalizado(Usuario especialista, TrabajoApp trabajoApp);
    void notificarConfirmacionTrabajoIniciado(Usuario especialista, TrabajoApp trabajoApp);

    void notificarNuevaResenaRecibida(Usuario especialista, String nombreCliente);
}
