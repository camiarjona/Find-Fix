package com.findfix.find_fix_app.especialista.dto;

import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record ActualizarEspecialistaDTO(
        @Size(max = 250)
        String descripcion,

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
        String telefono,
        String ciudad,
        Double latitud,
        Double longitud,

        @Digits(integer = 8, fraction = 0, message = "El DNI debe tener 8 dígitos")
        Long dni
) {
    public boolean tieneDescripcion() {
        return descripcion != null && !descripcion.isEmpty();
    }

    public boolean tieneNombre() {
        return nombre != null && !nombre.isEmpty();
    }

    public boolean tieneApellido() {
        return apellido != null && !apellido.isEmpty();
    }

    public boolean tieneTelefono() {
        return telefono != null && !telefono.isEmpty();
    }

    public boolean tieneCiudad() {
        return ciudad != null && !ciudad.isEmpty();
    }

    public boolean tieneDni() {
        return dni != null;
    }

    public boolean tieneCoordenadas() {
        return latitud != null && longitud != null;
    }
}