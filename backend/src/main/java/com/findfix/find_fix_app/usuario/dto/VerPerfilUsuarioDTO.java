package com.findfix.find_fix_app.usuario.dto;

import com.findfix.find_fix_app.rol.model.Rol;
import com.findfix.find_fix_app.usuario.model.Usuario;
import lombok.Data;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

//dto para que el usuario pueda visualizar su perfil
@Data
public class VerPerfilUsuarioDTO {
    private Long usuarioId;
    private String nombre;
    private String apellido;
    private String email;
    private String ciudad;
    private String telefono;
    private Set<String> roles;
    private boolean activo;
    private String fotoUrl;


    public VerPerfilUsuarioDTO(Usuario usuario) {

        this.usuarioId = usuario.getUsuarioId();
        this.nombre = usuario.getNombre();
        this.apellido = usuario.getApellido();
        this.email = usuario.getEmail();
        this.ciudad = usuario.getCiudad() == null ? "No especificada" : usuario.getCiudad().getNombreAmigable();
        this.telefono = usuario.getTelefono();
        this.fotoUrl = usuario.getFotoUrl();

        this.roles = usuario.getRoles().stream()
                .map(Rol::getNombre)
                .collect(Collectors.toSet());

        this.activo = usuario.isActivo();
    }
}
