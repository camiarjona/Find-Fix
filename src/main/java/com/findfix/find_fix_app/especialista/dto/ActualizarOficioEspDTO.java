package com.findfix.find_fix_app.especialista.dto;

import com.findfix.find_fix_app.especialista.model.Especialista;
import com.findfix.find_fix_app.oficio.service.OficioService;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;
import org.apache.coyote.Response;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.HashSet;
import java.util.Set;

public record ActualizarOficioEspDTO (
        @NotEmpty(message = "Debe especificar al menos un oficio para agregar o eliminar")
        Set<Long> agregar ,
        Set<Long> eliminar){

    public ActualizarOficioEspDTO {
        if ((agregar == null || agregar.isEmpty()) &&
                (eliminar == null || eliminar.isEmpty())) {
            throw new IllegalArgumentException("Debe especificar oficios para agregar o remover");
        }
    }
}
