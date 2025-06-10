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
        this.descripcion = especialista.getDescripcion();
        this.nombre = especialista.getUsuario().getNombre();
        this.apellido = especialista.getUsuario().getApellido();
        this.dni = especialista.getDni();
        this.telefono = especialista.getUsuario().getTelefono();
        this.ciudad = especialista.getUsuario().getCiudad();
        this.oficios = especialista.getOficios().stream()
                .map(oficio -> oficio.getNombre())
                .collect(Collectors.toSet());
    }
}
