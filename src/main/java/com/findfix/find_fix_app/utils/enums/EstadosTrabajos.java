package com.findfix.find_fix_app.utils.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import java.util.Arrays;

public enum EstadosTrabajos {
    CREADO("Creado"),
    EN_PROCESO("En proceso"),
    EN_REVISION("En revision"),
    FINALIZADO("Finalizado");

    private final String estadoAmigable;

    EstadosTrabajos(String estado) {
        this.estadoAmigable = estado;
    }

    @JsonValue
    public String getEstadoAmigable() {
        return estadoAmigable;
    }

    @JsonCreator
    public static EstadosTrabajos desdeString(String estadoAmigable) {
        return Arrays.stream(values())
                .filter(e -> e.estadoAmigable.equalsIgnoreCase(estadoAmigable))
                .findFirst().orElseThrow(() -> new IllegalArgumentException("El estado ingresado no forma parte del sistema."));
    }
}
