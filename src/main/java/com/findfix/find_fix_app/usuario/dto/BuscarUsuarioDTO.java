package com.findfix.find_fix_app.usuario.dto;

import java.util.List;

public record BuscarUsuarioDTO (
        String email,
        Long id,
        String rol,
        List<String> roles
){
    public boolean tieneEmail() { return email != null && email.isEmpty(); }
    public boolean tieneId() { return id != null; }
    public boolean tieneRol() { return rol != null && rol.isEmpty(); }
    public boolean tieneRoles(){ return roles != null && !roles.isEmpty(); }

}
