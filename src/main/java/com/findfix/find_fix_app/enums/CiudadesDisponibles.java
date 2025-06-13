package com.findfix.find_fix_app.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import java.util.Arrays;
import java.util.List;

public enum CiudadesDisponibles {
    MAR_DEL_PLATA("Mar del Plata"),
    BAHIA_BLANCA("Bahía Blanca"),
    NO_ESPECIFICADO("No especificado");

    private final String nombreAmigable;

    CiudadesDisponibles(String nombreAmigable) {
        this.nombreAmigable = nombreAmigable;
    }

    @JsonValue
    public String getNombreAmigable() {
        return nombreAmigable;
    }

    @JsonCreator
    public static CiudadesDisponibles desdeString(String nombre) {
        return Arrays.stream(CiudadesDisponibles.values())
                .filter(ciudad -> ciudad.nombreAmigable.equalsIgnoreCase(nombre))
                .findFirst().orElseThrow(() -> new IllegalArgumentException("La ciudad ingresada no forma parte del sistema"));
    }

    public static List<String> ciudadesDisponibles() {
        return Arrays.stream(CiudadesDisponibles.values()).map(CiudadesDisponibles::getNombreAmigable)
                .filter(nombre -> !"No especificado".equalsIgnoreCase(nombre))
                .toList();
    }
}
