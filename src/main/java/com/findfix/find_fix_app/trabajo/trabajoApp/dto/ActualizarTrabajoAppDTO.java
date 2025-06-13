package com.findfix.find_fix_app.trabajo.trabajoApp.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Size;

import javax.print.DocFlavor;

public record ActualizarTrabajoAppDTO(
        @Size(max = 30,message = "El titulo no puede contener mas de 30 caracteres")
        String titulo,
        @Size(max = 300,message = "La descripcion no puede contener mas de 300 caracteres")
        String descripcion,
        String estado,
        @DecimalMin(value = "1000.0",message = "El presupuesto debe ser al menos 1000")
        Double presupuesto) {



}
