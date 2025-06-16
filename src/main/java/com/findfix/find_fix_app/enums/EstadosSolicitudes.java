package com.findfix.find_fix_app.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import java.util.Arrays;

public enum EstadosSolicitudes {
    PENDIENTE("Pendiente"),
    ACEPTADO("Aceptado"),
    RECHAZADO("Rechazado");

    private final String nombreAmigable;

    EstadosSolicitudes(String nombreAmigable) {
        this.nombreAmigable = nombreAmigable;
    }

    @JsonValue
    public String getNombreAmigable() {
        return nombreAmigable;
    }

    @JsonCreator
    //metodo para transformar de string a enum
    public static EstadosSolicitudes desdeString(String nombre) {
        return Arrays.stream(values())
                .filter(e -> e.nombreAmigable.equalsIgnoreCase(nombre))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Estado inválido: " + nombre));
    }

}
