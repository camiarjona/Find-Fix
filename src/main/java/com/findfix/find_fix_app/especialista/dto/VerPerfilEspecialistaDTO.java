package com.findfix.find_fix_app.especialista.dto;

import com.findfix.find_fix_app.especialista.model.Especialista;
import lombok.Data;

@Data
public class VerPerfilEspecialistaDTO {

    private String nombre;
    private String apellido;
    private String email;
    private String ciudad;
    private String telefono;
    private String descripcion;
    private String oficios;
    private Double calificacionPromedio;
    private Long dni;

    public VerPerfilEspecialistaDTO(Especialista especialista) {

        this.nombre = validarYObtenerString(especialista.getUsuario().getNombre());
        this.apellido = validarYObtenerString(especialista.getUsuario().getApellido());
        this.email = validarYObtenerString(especialista.getUsuario().getEmail());

        // Verificar ciudad
        this.ciudad = (especialista.getUsuario().getCiudad() != null)
                ? especialista.getUsuario().getCiudad().getNombreAmigable()
                : "No especificado";

        this.telefono = validarYObtenerString(especialista.getUsuario().getTelefono());
        this.descripcion = validarYObtenerString(especialista.getDescripcion());

        // Verificar oficios
        this.oficios = obtenerOficiosString(especialista);

        // Calcular calificación promedio
        this.calificacionPromedio = calcularPromedioCalificacion(especialista);

        // Verificar DNI
        this.dni = (especialista.getDni() != null)
                ? especialista.getDni()
                : 00000000;
    }

     ///Valida strings y retorna "No especificado" si es null o vacío
    private String validarYObtenerString(String valor) {
        return (valor != null && !valor.trim().isEmpty()) ? valor.trim() : "No especificado";
    }

     ///Obtiene los oficios formateados como string
    private String obtenerOficiosString(Especialista especialista) {
        if (especialista.getOficios() == null || especialista.getOficios().isEmpty()) {
            return "No especificado";
        }

        String oficiosStr = especialista.getOficios().stream()
                .filter(oficio -> oficio != null && oficio.getNombre() != null)
                .map(oficio -> oficio.getNombre().trim())
                .filter(nombre -> !nombre.isEmpty())
                .reduce((a, b) -> a + ", " + b)
                .orElse("No especificado");

        return oficiosStr;
    }

    /// Promedio de calificaciones por especialista
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
