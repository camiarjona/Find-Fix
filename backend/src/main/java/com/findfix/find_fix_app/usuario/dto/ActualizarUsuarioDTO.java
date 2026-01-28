package com.findfix.find_fix_app.usuario.dto;

import jakarta.validation.constraints.Pattern;

public record ActualizarUsuarioDTO(
        @Pattern(
                regexp = "^[A-Za-zÁÉÍÓÚáéíóúÑñ]+( [A-Za-zÁÉÍÓÚáéíóúÑñ]+)*$",
                message = "❌ El nombre no debe tener espacios al principio, al final ni múltiples espacios seguidos ❌"
        )
        String nombre,
        @Pattern(
                regexp = "^[A-Za-zÁÉÍÓÚáéíóúÑñ]+( [A-Za-zÁÉÍÓÚáéíóúÑñ]+)*$",
                message = "❌ El apellido no debe tener espacios al principio, al final ni múltiples espacios seguidos ❌"
        )
        String apellido,

        @Pattern(regexp = "^[0-9]{10}$", message = "❌ El teléfono debe contener exactamente 10 dígitos numéricos ❌")
        String telefono,

        String ciudad,

        Double latitud,
        Double longitud
) {
    public boolean tieneNombre() {
        return nombre != null;
    }

    public boolean tieneApellido() {
        return apellido != null;
    }

    public boolean tieneTelefono() {
        return telefono != null;
    }

    public boolean tieneCiudad() {
        return ciudad != null;
    }

    public boolean tieneLatitud() {
        return latitud != null;
    }

    public boolean tieneLongitud() {
        return longitud != null;
    }
}