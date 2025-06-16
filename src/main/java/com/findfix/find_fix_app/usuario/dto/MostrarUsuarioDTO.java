package com.findfix.find_fix_app.usuario.dto;

import com.findfix.find_fix_app.rol.model.Rol;
import com.findfix.find_fix_app.usuario.model.Usuario;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;
import java.util.stream.Collectors;

@Data
@AllArgsConstructor
@NoArgsConstructor
//dto para ver lista de usuarios desde admin
public class MostrarUsuarioDTO {
    String nombre;
    String apellido;
    String email;
    Set<String> rolesUsuario;

    public MostrarUsuarioDTO(Usuario usuario) {
        this.nombre = usuario.getNombre();
        this.apellido = usuario.getApellido();
        this.email = usuario.getEmail();
        this.rolesUsuario = usuario.getRoles().stream().map(Rol::getNombre).collect(Collectors.toSet());
    }

}
