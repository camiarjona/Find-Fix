package com.findfix.find_fix_app.usuario.dto;

import com.findfix.find_fix_app.rol.model.Rol;
import lombok.Data;

import java.util.Set;
import java.util.stream.Collectors;

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
