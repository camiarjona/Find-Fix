package com.findfix.find_fix_app.usuario.dto;

public record ActualizarUsuarioDTO (
    String nombre,
    String apellido,
    String telefono,
    String ciudad
){
    @Override
    public String toString() {
        return "ActualizarUsuarioDTO{" +
                "nombre='" + nombre + '\'' +
                ", apellido='" + apellido + '\'' +
                ", telefono='" + telefono + '\'' +
                ", ciudad='" + ciudad + '\'' +
                '}';
    }
}
