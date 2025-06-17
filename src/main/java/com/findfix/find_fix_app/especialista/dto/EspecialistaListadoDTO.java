package com.findfix.find_fix_app.especialista.dto;

import com.findfix.find_fix_app.especialista.model.Especialista;
import com.findfix.find_fix_app.oficio.model.Oficio;
import lombok.Data;

import java.util.Set;
import java.util.stream.Collectors;

@Data
public class EspecialistaListadoDTO{
    private String nombre;
    private String apellido;
    private String ciudad;
    private Set<String> oficios;
    private Double calificacionPromedio;
    private String email;

    public EspecialistaListadoDTO(Especialista especialista) {
        this.nombre = validarYObtenerString(especialista.getUsuario().getNombre());
        this.apellido = validarYObtenerString(especialista.getUsuario().getApellido());
        this.email = validarYObtenerString(especialista.getUsuario().getEmail());

        // Manejo de ciudad nula
        this.ciudad = (especialista.getUsuario().getCiudad() != null)
                ? especialista.getUsuario().getCiudad().getNombreAmigable()
                : "No especificado";

        // Manejo de oficios nulos
        this.oficios = obtenerOficiosSet(especialista);

        // Cálculo de calificación
        this.calificacionPromedio = calcularPromedioCalificacion(especialista);
    }

    // Metodo para validar strings
    private String validarYObtenerString(String valor) {
        return (valor != null && !valor.trim().isEmpty()) ? valor.trim() : "No especificado";
    }

    // Metodo para obtener oficios con validación
    private Set<String> obtenerOficiosSet(Especialista especialista) {
        if (especialista.getOficios() == null || especialista.getOficios().isEmpty()) {
            return Set.of("No especificado");
        }

        return especialista.getOficios().stream()
                .filter(oficio -> oficio != null && oficio.getNombre() != null)
                .map(oficio -> validarYObtenerString(oficio.getNombre()))
                .collect(Collectors.toSet());
    }


    private double calcularPromedioCalificacion(Especialista especialista) {
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
}