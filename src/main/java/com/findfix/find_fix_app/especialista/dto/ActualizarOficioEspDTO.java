package com.findfix.find_fix_app.especialista.dto;
import java.util.Set;

public record ActualizarOficioEspDTO (
        Set<String> agregar ,
        Set<String> eliminar){

    public ActualizarOficioEspDTO {
        if ((agregar == null || agregar.isEmpty()) &&
                (eliminar == null || eliminar.isEmpty())) {
            throw new IllegalArgumentException("Debe especificar oficios para agregar o remover");
        }
    }
}
