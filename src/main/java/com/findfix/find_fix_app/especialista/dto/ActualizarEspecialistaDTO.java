package com.findfix.find_fix_app.especialista.dto;

import java.security.PrivateKey;

public record ActualizarEspecialistaDTO(
        String descripcion,
        String nombre,
        String apellido,
        String telefono,
        String ciudad
) {
}
