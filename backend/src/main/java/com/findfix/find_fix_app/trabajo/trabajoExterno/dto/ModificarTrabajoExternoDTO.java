package com.findfix.find_fix_app.trabajo.trabajoExterno.dto;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;


public record ModificarTrabajoExternoDTO (
        @Size(min = 2, max = 20)
        @Pattern(
                regexp = "^[A-Za-zÁÉÍÓÚáéíóúÑñ]+( [A-Za-zÁÉÍÓÚáéíóúÑñ]+)*$",
                message = "El nombre del cliente no debe tener espacios al principio, al final ni múltiples espacios seguidos"
        )
        String nombreCliente,
        @Size(min = 8, max = 250)
        @Pattern(
                regexp = "^[A-Za-zÁÉÍÓÚáéíóúÑñ]+( [A-Za-zÁÉÍÓÚáéíóúÑñ]+)*$",
                message = "La descripcion no debe tener espacios al principio, al final ni múltiples espacios seguidos"
        )
        String descripcion,
        @Positive(message = "El presupuesto no puede ser cero ni negativo.")
        Double presupuesto,
        @Pattern(
                regexp = "^[A-Za-zÁÉÍÓÚáéíóúÑñ0-9]+( [A-Za-zÁÉÍÓÚáéíóúÑñ0-9]+)*$",
                message = "El titulo no debe tener espacios al principio, al final ni múltiples espacios seguidos"
        )
        @Size(min = 2, max = 20)
        String titulo
){
    public boolean tieneNombreCliente(){return nombreCliente != null && !nombreCliente.isEmpty();}
    public boolean tieneDescripcion(){return descripcion != null && !descripcion.isEmpty();}
    public boolean tienePresupuesto(){return presupuesto != null && presupuesto > 0;}
    public boolean tieneTitulo(){return titulo != null && !titulo.isEmpty();}

}
