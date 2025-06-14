package com.findfix.find_fix_app.usuario.dto;

import java.util.List;

public record BuscarUsuarioDTO (
        String email,
        Long id,
        String rol,
        List<String> roles
){
}
