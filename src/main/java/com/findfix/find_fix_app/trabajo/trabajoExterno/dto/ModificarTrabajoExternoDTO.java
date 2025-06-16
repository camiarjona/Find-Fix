package com.findfix.find_fix_app.trabajo.trabajoExterno.dto;

import com.findfix.find_fix_app.enums.EstadosTrabajos;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


public record ModificarTrabajoExternoDTO (
        @Size(max = 50)
        String nombreCliente,
        @Size(max = 250)
        String descripcion,
        @Positive(message = "El presupuesto no puede ser cero ni negativo.")
        Double presupuesto,
        String titulo
){
    public boolean tieneNombreCliente(){return nombreCliente != null;}
    public boolean tieneDescripcion(){return descripcion != null;}
    public boolean tienePresupuesto(){return presupuesto != null;}
    public boolean tieneTitulo(){return titulo != null;}

}
