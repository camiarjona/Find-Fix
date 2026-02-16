package com.findfix.find_fix_app.resena.dto;

import com.findfix.find_fix_app.resena.model.Resena;
import com.findfix.find_fix_app.utils.enums.EstadosTrabajos;
import lombok.Data;

import java.time.LocalDate;

@Data
public class MostrarResenaDTO {

    private Long resenaId;
    private Double puntuacion;
    private String comentario;
    private LocalDate fechaInicio;
    private LocalDate fechaFin;
    private EstadosTrabajos estado;
    private String descripcion;
    private Double presupuesto;
    private String fotoUrlCliente;

    public MostrarResenaDTO(Resena resena) {
        this.resenaId = resena.getResenaId();
        this.puntuacion = resena.getPuntuacion();
        this.comentario = resena.getComentario();
        this.fechaInicio = resena.getTrabajoApp().getFechaInicio();
        this.fechaFin = resena.getTrabajoApp().getFechaFin();
        this.estado = resena.getTrabajoApp().getEstado();
        this.descripcion = resena.getTrabajoApp().getDescripcion();
        this.presupuesto = resena.getTrabajoApp().getPresupuesto();
        this.fotoUrlCliente = resena.getTrabajoApp().getUsuario().getFotoUrl();
    }
}