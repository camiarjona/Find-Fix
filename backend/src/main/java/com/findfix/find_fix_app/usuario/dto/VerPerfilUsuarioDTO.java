package com.findfix.find_fix_app.usuario.dto;

import com.findfix.find_fix_app.rol.model.Rol;
import com.findfix.find_fix_app.usuario.model.Usuario;
import lombok.Data;

import java.util.Set;
import java.util.stream.Collectors;

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

    private Double latitud;
    private Double longitud;

    public VerPerfilUsuarioDTO(Usuario usuario) {

        this.usuarioId = usuario.getUsuarioId();
        this.nombre = usuario.getNombre();
        this.apellido = usuario.getApellido();
        this.email = usuario.getEmail();
        this.ciudad = usuario.getCiudad();
        this.telefono = usuario.getTelefono();
        this.fotoUrl = usuario.getFotoUrl();
        this.activo = usuario.isActivo();
        this.latitud = usuario.getLatitud();
        this.longitud = usuario.getLongitud();

        this.roles = usuario.getRoles().stream()
                .map(Rol::getNombre)
                .collect(Collectors.toSet());
    }
}