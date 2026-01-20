package com.findfix.find_fix_app.notificacion.service;
import java.util.List;

import com.findfix.find_fix_app.notificacion.model.Notificacion;
import com.findfix.find_fix_app.usuario.model.Usuario;

public interface NotificacionService {

    // Métodos funcionales para el Controller
    List<Notificacion> obtenerMisNotificaciones(Usuario usuario);
    void marcarComoLeida(Long idNotificacion);

    // Método Genérico
    void notificar(Usuario destinatario, String titulo, String mensaje);

    // --- MÉTODOS DE NEGOCIO (Los 14 casos) ---
    void notificarSolicitudCambioContrasena(Usuario usuario);
    void notificarCambioContrasenaExitoso(Usuario usuario);
    void notificarReporteRecibido(Usuario usuario);
    
    void notificarAdminNuevaSolicitudEspecialista(Usuario admin, String nombreSolicitante);
    void notificarAdminNuevoReporte(Usuario admin);
    
    void notificarResolucionSolicitudRol(Usuario usuario, boolean aprobado);
    void notificarConfirmacionSolicitudEnviada(Usuario cliente, String nombreEspecialista);
    void notificarRespuestaSolicitudTrabajo(Usuario cliente, String nombreEspecialista, boolean aceptada);
    void notificarCambioEstadoTrabajo(Usuario cliente, String estado, String nombreEspecialista);
    void notificarConfirmacionResenaRealizada(Usuario cliente);
    void notificarConfirmacionSolicitudEspecialistaEnviada(Usuario usuario);
    
    void notificarNuevaSolicitudTrabajoRecibida(Usuario especialista, String nombreCliente, String servicio);
    void notificarNuevoTrabajoCreado(Usuario especialista, String nombreCliente);
    void notificarConfirmacionTrabajoFinalizado(Usuario especialista);
    void notificarNuevaResenaRecibida(Usuario especialista, String nombreCliente);
}
