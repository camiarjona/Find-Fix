package com.findfix.find_fix_app.solicitudTrabajo.dto;

import com.findfix.find_fix_app.solicitudTrabajo.model.SolicitudTrabajo;
import lombok.Data;

import java.time.LocalDate;

@Data
public class MostrarSolicitudDTO {
    Long id;
    LocalDate fechaSolicitud; // CAMBIO: Renombrado de 'fechaCreacion' a 'fechaSolicitud' para coincidir con el HTML
    String estado;
    String cliente;           // CAMBIO: Campo unificado que espera el frontend
    String titulo;            // CAMBIO: Agregado porque el HTML lo usa
    String descripcion;

    public MostrarSolicitudDTO(SolicitudTrabajo solicitudTrabajo) {
        this.id = solicitudTrabajo.getSolicitudTrabajoId();
        this.fechaSolicitud = solicitudTrabajo.getFechaCreacion(); // Asignamos la fecha de creación aquí
        this.estado = solicitudTrabajo.getEstado().toString();

        // Lógica para armar el nombre completo del cliente
        String nombre = solicitudTrabajo.getUsuario() != null ? solicitudTrabajo.getUsuario().getNombre() : "Usuario";
        String apellido = solicitudTrabajo.getUsuario() != null ? solicitudTrabajo.getUsuario().getApellido() : "Desvinculado";
        this.cliente = nombre + " " + apellido;

        // Asignamos un título por defecto o derivado
        this.titulo = "Solicitud de Servicio";

        this.descripcion = solicitudTrabajo.getDescripcion();
    }
}