package com.findfix.find_fix_app.usuario.dto;

import com.findfix.find_fix_app.usuario.model.Usuario;
import lombok.Data;

import java.util.Optional;

//dto para que el usuario pueda visualizar su perfil
@Data
public class VerPerfilUsuarioDTO {
    private String nombre;
    private String apellido;
    private String email;
    private String ciudad;
    private String telefono;


    public VerPerfilUsuarioDTO(Usuario usuario) {
        this.nombre = usuario.getNombre();
        this.apellido = usuario.getApellido();
        this.email = usuario.getEmail();
        this.ciudad = usuario.getCiudad() == null ? "No especificada" : usuario.getCiudad().getNombreAmigable();
        this.telefono = usuario.getTelefono();
    }
}
