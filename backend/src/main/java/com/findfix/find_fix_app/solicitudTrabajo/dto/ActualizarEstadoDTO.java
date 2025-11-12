package com.findfix.find_fix_app.solicitudTrabajo.dto;

public record ActualizarEstadoDTO(
        String estado) {

    public boolean tieneEstado() { return estado != null && !estado.isEmpty(); }

}
