package com.findfix.find_fix_app.usuario.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class UsuarioLoginDTO {
    @Email
    @NotBlank(message = "El email es obligatorio.")
    private String email;

    @NotBlank(message = "La contraseña es obligatoria.")
    private String password;
}
