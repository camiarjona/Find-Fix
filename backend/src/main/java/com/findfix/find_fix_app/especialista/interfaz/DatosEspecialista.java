package com.findfix.find_fix_app.especialista.interfaz;

import com.findfix.find_fix_app.especialista.model.Especialista;

import java.util.Set;
import java.util.stream.Collectors;

public interface DatosEspecialista {

    default double calcularPromedioCalificacion(Especialista especialista) {
        double promedio = especialista.getTrabajos().stream()
                .filter(trabajo -> trabajo != null
                        && trabajo.getResena() != null
                        && trabajo.getResena().getPuntuacion() != null)
                .mapToDouble(trabajo -> {
                    // Para que cada puntuación individual esté entre 0-5
                    double puntuacion = trabajo.getResena().getPuntuacion();
                    return puntuacion < 0 ? 0 : puntuacion > 5 ? 5 : puntuacion;
                })
                .average()
                .orElse(0.0);

        // Doble verificación del rango
        return promedio < 0 ? 0 : promedio > 5 ? 5 : promedio;
    }

    ///Obtiene los oficios formateados como string
    default Set<String> obtenerOficiosSet(Especialista especialista) {
        if (especialista.getOficios() == null || especialista.getOficios().isEmpty()) {
            return Set.of("No especificado");
        }

        return especialista.getOficios().stream()
                .filter(oficio -> oficio != null && oficio.getNombre() != null)
                .map(oficio -> validarYObtenerString(oficio.getNombre()))
                .collect(Collectors.toSet());
    }

    ///Valida strings y retorna "No especificado" si es null o vacío
    default String validarYObtenerString(String valor) {
        return (valor != null && !valor.trim().isEmpty()) ? valor.trim() : "No especificado";
    }

    ///Obtiene los oficios formateados como string
    default String obtenerOficiosString(Especialista especialista) {
        if (especialista.getOficios() == null || especialista.getOficios().isEmpty()) {
            return "No especificado";
        }

        return especialista.getOficios().stream()
                .filter(oficio -> oficio != null && oficio.getNombre() != null)
                .map(oficio -> oficio.getNombre().trim())
                .filter(nombre1 -> !nombre1.isEmpty())
                .reduce((a, b) -> a + ", " + b)
                .orElse("No especificado");
    }
}
