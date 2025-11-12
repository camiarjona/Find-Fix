package com.findfix.find_fix_app.solicitudEspecialista.dto;

import java.time.LocalDate;

public record BuscarSolicitudEspecialistaDTO (
        Long id,
        String estado,
        LocalDate fechaDesde,
        LocalDate fechaHasta,
        String email
){
    public boolean tieneId() { return id != null; }
    public boolean tieneEstado() { return estado != null && !estado.isEmpty(); }
    public boolean tieneEmail() { return email != null && !email.isEmpty();}
    public boolean tieneFecha() { return fechaDesde != null || fechaHasta != null; }

}
