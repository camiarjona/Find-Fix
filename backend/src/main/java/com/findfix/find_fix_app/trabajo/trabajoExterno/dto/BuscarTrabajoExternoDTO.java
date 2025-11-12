package com.findfix.find_fix_app.trabajo.trabajoExterno.dto;

import java.time.LocalDate;

public record BuscarTrabajoExternoDTO (
       String titulo,
       String estado,
       Long id,
       LocalDate desde,
       LocalDate hasta
){
    public boolean tieneTitulo(){return titulo != null && !titulo.isEmpty();}
    public boolean tieneEstado() { return estado != null && !estado.isEmpty(); }
    public boolean tieneId(){return id != null && id > 0;}
    public boolean tieneFecha() { return desde != null || hasta != null; }
}
