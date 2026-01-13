package com.findfix.find_fix_app.usuario.dto;

import com.findfix.find_fix_app.utils.auth.dto.RegistroDTO;
import lombok.Data;

@Data
public class MostrarRegistroDTO {
    String nombre;
    String apellido;
    String email;

    public MostrarRegistroDTO(RegistroDTO registroDTO) {
        this.nombre = registroDTO.nombre();
        this.apellido = registroDTO.apellido();
        this.email = registroDTO.email();
    }
}
