package com.findfix.find_fix_app.trabajo.trabajoApp.dto;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record ActualizarTrabajoAppDTO(
        @Size(max = 30,message = "El título no puede contener mas de 30 caracteres")
        @Pattern(
                regexp = "^[A-Za-zÁÉÍÓÚáéíóúÑñ0-9]+( [A-Za-zÁÉÍÓÚáéíóúÑñ0-9]+)*$",
                message = "El titulo no debe tener espacios al principio, al final ni múltiples espacios seguidos"
        )
        String titulo,
        @Size(max = 300,message = "La descripción no puede contener mas de 300 caracteres")
        String descripcion,
        @Positive(message = "El presupuesto no puede ser 0 ni negativo.")
        Double presupuesto
) {
        public boolean tieneTitulo(){return  titulo != null &&  !titulo.isEmpty();}
        public boolean tieneDescripcion(){return  descripcion != null &&  !descripcion.isEmpty();}
        public boolean tienePresupuesto(){return  presupuesto != null;}
}
