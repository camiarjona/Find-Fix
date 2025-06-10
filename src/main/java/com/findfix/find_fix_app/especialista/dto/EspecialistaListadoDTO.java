package com.findfix.find_fix_app.especialista.dto;

import com.findfix.find_fix_app.especialista.model.Especialista;
import com.findfix.find_fix_app.oficio.model.Oficio;
import lombok.Data;

import java.util.Set;

@Data
public class EspecialistaListadoDTO{
    private String nombre;
    private String apellido;
    private String ciudad;
    private Set<String> oficios;

    public EspecialistaListadoDTO(Especialista especialista) {
        this.nombre = especialista.getUsuario().getNombre();
        this.apellido = especialista.getUsuario().getApellido();
        this.ciudad = especialista.getUsuario().getCiudad();
        this.oficios = especialista.getOficios().stream().map(Oficio::getNombre).collect(java.util.stream.Collectors.toSet());
    }
}
