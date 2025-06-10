package com.findfix.find_fix_app.especialista.dto;

import jakarta.persistence.Column;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Size;

import java.security.PrivateKey;

public record ActualizarEspecialistaDTO(
        @Size(max = 250)
        String descripcion,

        String nombre,
        String apellido,
        String telefono,
        String ciudad,
        @Digits(integer = 8, fraction = 0, message = "El DNI debe tener 8 dígitos")
        Long dni
) {
}
