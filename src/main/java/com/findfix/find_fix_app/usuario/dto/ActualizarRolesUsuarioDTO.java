package com.findfix.find_fix_app.usuario.dto;

import java.util.Set;

public record ActualizarRolesUsuarioDTO (
        Set<String> rolesAgregar,
        Set<String> rolesEliminar
){
    public boolean tieneRolesAgregar() {return rolesAgregar != null && !rolesAgregar.isEmpty();}
    public boolean tieneRolesEliminar() {return rolesEliminar != null && !rolesEliminar.isEmpty();}
}
