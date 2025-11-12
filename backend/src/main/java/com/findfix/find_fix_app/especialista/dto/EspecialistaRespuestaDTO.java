package com.findfix.find_fix_app.especialista.dto;

import com.findfix.find_fix_app.especialista.model.Especialista;
import lombok.Data;

import java.util.Set;
import java.util.stream.Collectors;

@Data
public class EspecialistaRespuestaDTO {
    private String descripcion;
    private String nombre;
    private String apellido;
    private Long dni;
    private String telefono;
    private String ciudad;
    private Set<String> oficios;

    public EspecialistaRespuestaDTO(Especialista especialista) {
        // Validar y asignar descripción
        this.descripcion = (especialista.getDescripcion() != null)
                ? especialista.getDescripcion()
                : "No especificado";

        this.nombre = (especialista.getUsuario().getNombre() != null)
                ? especialista.getUsuario().getNombre()
                : "No especificado";

        this.apellido = (especialista.getUsuario().getApellido() != null)
                ? especialista.getUsuario().getApellido()
                : "No especificado";

        this.dni = (especialista.getDni() != null)
                ? especialista.getDni()
                : 0; // Valor por defecto para DNI

        this.telefono = (especialista.getUsuario().getTelefono() != null)
                ? especialista.getUsuario().getTelefono()
                : "No especificado";

        // Manejo seguro de ciudad
        this.ciudad = (especialista.getUsuario().getCiudad() != null)
                ? especialista.getUsuario().getCiudad().getNombreAmigable()
                : "Ciudad no especificada";

        // Manejo seguro de oficios
        this.oficios = (especialista.getOficios() != null)
                ? especialista.getOficios().stream()
                .filter(oficio -> oficio != null && oficio.getNombre() != null)
                .map(oficio -> oficio.getNombre())
                .collect(Collectors.toSet())
                : Set.of("No especificado");
    }
}
