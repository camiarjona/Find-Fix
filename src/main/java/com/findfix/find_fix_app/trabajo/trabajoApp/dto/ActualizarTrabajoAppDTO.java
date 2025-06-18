package com.findfix.find_fix_app.trabajo.trabajoApp.dto;

import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record ActualizarTrabajoAppDTO(
        @Size(max = 30,message = "El título no puede contener mas de 30 caracteres")
        String titulo,
        @Size(max = 300,message = "La descripción no puede contener mas de 300 caracteres")
        String descripcion,
        @Positive
        Double presupuesto
) {
        public boolean tieneTitulo(){return  titulo != null;}
        public boolean tieneDescripcion(){return  descripcion != null;}
        public boolean tienePresupuesto(){return  presupuesto != null;}
}
