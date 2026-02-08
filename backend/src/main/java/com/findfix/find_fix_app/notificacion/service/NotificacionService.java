package com.findfix.find_fix_app.notificacion.service;
import java.util.List;

import com.findfix.find_fix_app.notificacion.dto.NotificacionDTO;
import com.findfix.find_fix_app.notificacion.model.Notificacion;
import com.findfix.find_fix_app.trabajo.trabajoApp.model.TrabajoApp;
import com.findfix.find_fix_app.usuario.model.Usuario;

public interface NotificacionService {

    List<NotificacionDTO> obtenerMisNotificaciones(Usuario usuario, String rolVista);
    void marcarComoLeida(Long idNotificacion);
    void notificarBienvenida(Usuario usuario, String rolDestinatario);

    void notificar(Usuario destinatario, String titulo, String mensaje, String rolDestinatario);

    void notificarSolicitudCambioContrasena(Usuario usuario,String rolDestinatario);
    void notificarCambioContrasenaExitoso(Usuario usuario, String rolDestinatario);
    void notificarReporteRecibido(Usuario usuario, String rolDestinatario);
    
    void notificarAdminNuevaSolicitudEspecialista(Usuario admin, String nombreSolicitante, String rolDestinatario);
    void notificarAdminNuevoReporte(Usuario admin, String rolDestinatario);
    
    void notificarResolucionSolicitudRol(Usuario usuario, boolean aprobado, String rolDestinatario);
    void notificarConfirmacionSolicitudEnviada(Usuario cliente, String nombreEspecialista, String rolDestinatario);
    void notificarRespuestaSolicitudTrabajo(Usuario cliente, String nombreEspecialista, boolean aceptada, String rolDestinatario);
    void notificarCambioEstadoTrabajo(Usuario cliente, String estado, String nombreEspecialista, String rolDestinatario);
    void notificarConfirmacionResenaRealizada(Usuario cliente, String nombreEspecialista, String rolDestinatario);
    void notificarConfirmacionSolicitudEspecialistaEnviada(Usuario usuario, String rolDestinatario);
    
    void notificarNuevaSolicitudTrabajoRecibida(Usuario especialista, String nombreCliente, String servicio, String rolDestinatario);
    void notificarNuevoTrabajoCreado(Usuario especialista, String nombreCliente, String rolDestinatario);
    void notificarConfirmacionTrabajoFinalizado(Usuario especialista, TrabajoApp trabajoApp,String rolDestinatario);
    void notificarConfirmacionTrabajoIniciado(Usuario especialista, TrabajoApp trabajoApp, String rolDestinatario);

    void notificarNuevaResenaRecibida(Usuario especialista, String nombreCliente, String rolDestinatario);
}
