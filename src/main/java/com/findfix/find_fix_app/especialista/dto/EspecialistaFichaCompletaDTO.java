package com.findfix.find_fix_app.especialista.dto;

import com.findfix.find_fix_app.especialista.model.Especialista;
import com.findfix.find_fix_app.oficio.model.Oficio;
import lombok.Data;

import java.util.Set;
import java.util.stream.Collectors;

@Data
public class EspecialistaFichaCompletaDTO {
    private String descripcion;
    private String nombre;
    private String apellido;
    private String telefono;
    private String ciudad;
    private Set<String> oficios;
    private Double calificacionPromedio;


    public EspecialistaFichaCompletaDTO(Especialista especialista) {
            this.nombre = validarYObtenerString(especialista.getUsuario().getNombre());
            this.apellido = validarYObtenerString(especialista.getUsuario().getApellido());
            this.telefono = validarYObtenerString(especialista.getUsuario().getTelefono());

            // Validar ciudad
            this.ciudad = (especialista.getUsuario().getCiudad() != null)
                    ? especialista.getUsuario().getCiudad().getNombreAmigable()
                    : "No especificado";

            this.descripcion = validarYObtenerString(especialista.getDescripcion());

            // Validar oficios
            this.oficios = obtenerOficiosSet(especialista);

            // Calcular calificación
            this.calificacionPromedio = calcularPromedioCalificacion(especialista);
        }

        /// Valida strings y retorna "No especificado" si es null o vacío
        private String validarYObtenerString(String valor) {
            return (valor != null && !valor.trim().isEmpty()) ? valor.trim() : "No especificado";
        }

        /// Obtiene los oficios como Set<String> con validación
        private Set<String> obtenerOficiosSet(Especialista especialista) {
            if (especialista.getOficios() == null || especialista.getOficios().isEmpty()) {
                return Set.of("No especificado");
            }

            return especialista.getOficios().stream()
                    .filter(oficio -> oficio != null && oficio.getNombre() != null)
                    .map(oficio -> validarYObtenerString(oficio.getNombre()))
                    .collect(Collectors.toSet());
        }

        /// Promedio de calificaciones por especialista
        private double calcularPromedioCalificacion(Especialista especialista) {
            if (especialista.getTrabajos() == null) {
                return 0.0;
            }

            double promedio = especialista.getTrabajos().stream()
                    .filter(trabajo -> trabajo != null
                            && trabajo.getResena() != null
                            && trabajo.getResena().getPuntuacion() != null)
                    .mapToDouble(trabajo -> {
                        double puntuacion = trabajo.getResena().getPuntuacion();
                        return Math.max(0, Math.min(5, puntuacion)); // Asegura rango 0-5
                    })
                    .average()
                    .orElse(0.0);

            return Math.max(0, Math.min(5, promedio)); // Doble verificación del rango
        }
}

