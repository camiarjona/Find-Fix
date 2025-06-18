package com.findfix.find_fix_app.resena.dto;

import com.findfix.find_fix_app.resena.model.Resena;
import lombok.Data;

@Data
public class MostrarResenaClienteDTO {
    private Long resenaId;
    private Double puntuacion;
    private String comentario;
    private String nombreEspecialista;

    public MostrarResenaClienteDTO(Resena resena) {
        this.resenaId = resena.getResenaId();
        this.puntuacion = resena.getPuntuacion();
        this.comentario = resena.getComentario();
        this.nombreEspecialista = resena.getTrabajoApp().getEspecialista() != null ? resena.getTrabajoApp().getEspecialista().getUsuario().getNombre() : "Especialista desvinculado";
    }
}
