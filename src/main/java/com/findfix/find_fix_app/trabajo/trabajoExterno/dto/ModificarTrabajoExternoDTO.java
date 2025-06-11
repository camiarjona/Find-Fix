package com.findfix.find_fix_app.trabajo.trabajoExterno.dto;

import com.findfix.find_fix_app.enums.EstadosTrabajos;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor

public class ModificarTrabajoExternoDTO {
    @NotNull(message = "El nuevo estado es obligatorio")
    private EstadosTrabajos estado;
}
