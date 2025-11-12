package com.findfix.find_fix_app.resena.dto;

import com.findfix.find_fix_app.resena.model.Resena;
import lombok.Data;

@Data
public class MostrarResenaEspecialistaDTO {
    private Long resenaId;
    private Double puntuacion;
    private String comentario;
    private String nombreCliente;

    public MostrarResenaEspecialistaDTO(Resena resena) {
        this.resenaId = resena.getResenaId();
        this.puntuacion = resena.getPuntuacion();
        this.comentario = resena.getComentario();
        this.nombreCliente = resena.getTrabajoApp().getUsuario() != null ? resena.getTrabajoApp().getUsuario().getNombre() : "Cliente desvinculado";
    }
}
