package com.findfix.find_fix_app.usuario.dto;

import java.util.Set;

public record ActualizarRolesUsuarioDTO (
        Set<String> rolesAgregar,
        Set<String> rolesEliminar
){
}
