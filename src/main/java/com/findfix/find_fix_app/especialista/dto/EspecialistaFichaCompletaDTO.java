package com.findfix.find_fix_app.especialista.dto;

import com.findfix.find_fix_app.especialista.model.Especialista;
import com.findfix.find_fix_app.oficio.model.Oficio;
import lombok.Data;

import java.util.Set;

@Data
public class EspecialistaFichaCompletaDTO {
    private String descripcion;
    private String nombre;
    private String apellido;
    private String telefono;
    private String ciudad;
    private Set<String> oficios;

    public EspecialistaFichaCompletaDTO(Especialista especialista) {
        this.descripcion = especialista.getDescripcion();
        this.nombre = especialista.getUsuario().getNombre();
        this.apellido = especialista.getUsuario().getApellido();
        this.telefono = especialista.getUsuario().getTelefono();
        this.ciudad = especialista.getUsuario().getCiudad().getNombreAmigable();
        this.oficios = especialista.getOficios().stream().map(Oficio::getNombre).collect(java.util.stream.Collectors.toSet());
    }


}

