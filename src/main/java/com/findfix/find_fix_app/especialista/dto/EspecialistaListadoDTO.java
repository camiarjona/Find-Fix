package com.findfix.find_fix_app.especialista.dto;

import com.findfix.find_fix_app.especialista.interfaz.DatosEspecialista;
import com.findfix.find_fix_app.especialista.model.Especialista;
import lombok.Data;
import java.util.Set;

@Data
public class EspecialistaListadoDTO implements DatosEspecialista {
    private String nombre;
    private String apellido;
    private String ciudad;
    private Set<String> oficios;
    private Double calificacionPromedio;
    private String email;

    public EspecialistaListadoDTO(Especialista especialista) {
        this.nombre = validarYObtenerString(especialista.getUsuario() != null ? especialista.getUsuario().getNombre() : "Usuario desvinculado");
        this.apellido = validarYObtenerString(especialista.getUsuario() != null ? especialista.getUsuario().getApellido() : "Usuario desvinculado");
        this.email = validarYObtenerString(especialista.getUsuario() != null ? especialista.getUsuario().getEmail() : "Usuario desvinculado");

        // Manejo de ciudad nula
        this.ciudad = (especialista.getUsuario().getCiudad() != null)
                ? especialista.getUsuario().getCiudad().getNombreAmigable()
                : "No especificado";

        // Manejo de oficios nulos
        this.oficios = obtenerOficiosSet(especialista);

        // Cálculo de calificación
        this.calificacionPromedio = calcularPromedioCalificacion(especialista);
    }

}