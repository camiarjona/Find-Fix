package com.findfix.find_fix_app.usuario.dto;

public record ActualizarUsuarioDTO (
    String nombre,
    String apellido,
    String telefono,
    String ciudad
){
    public boolean tieneNombre() { return nombre != null; }
    public boolean tieneApellido() { return apellido != null; }
    public boolean tieneTelefono() { return telefono != null; }
    public boolean tieneCiudad() { return ciudad != null; }
}
