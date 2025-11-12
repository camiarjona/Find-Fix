package com.findfix.find_fix_app.utils.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import java.util.Arrays;
import java.util.List;

public enum CiudadesDisponibles {
    MAR_DEL_PLATA("Mar del Plata"),
    BAHIA_BLANCA("Bahia Blanca"),
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
    //metodo para transformar de string a enum
    public static CiudadesDisponibles desdeString(String nombre) {
        return Arrays.stream(CiudadesDisponibles.values())
                .filter(ciudad -> ciudad.nombreAmigable.equalsIgnoreCase(nombre))
                .findFirst().orElseThrow(() -> new IllegalArgumentException("La ciudad ingresada no forma parte del sistema"));
    }

    //metodo para obtener la lista de ciudades disponibles
    public static List<String> ciudadesDisponibles() {
        return Arrays.stream(CiudadesDisponibles.values()).map(CiudadesDisponibles::getNombreAmigable)
                .filter(nombre -> !"No especificado".equalsIgnoreCase(nombre))
                .toList();
    }
}
