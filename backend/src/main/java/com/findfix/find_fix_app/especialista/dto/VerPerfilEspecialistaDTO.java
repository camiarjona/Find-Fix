package com.findfix.find_fix_app.especialista.dto;

import com.findfix.find_fix_app.especialista.interfaz.DatosEspecialista;
import com.findfix.find_fix_app.especialista.model.Especialista;
import com.findfix.find_fix_app.oficio.model.Oficio;
import lombok.Data;

import java.util.Set;

@Data
public class VerPerfilEspecialistaDTO implements DatosEspecialista {

    private String nombre;
    private String apellido;
    private String email;
    private String ciudad;
    private String telefono;
    private String descripcion;
    private Set<Oficio> oficios;
    private Double calificacionPromedio;
    private Long dni;

    public VerPerfilEspecialistaDTO(Especialista especialista) {

        this.nombre = validarYObtenerString(especialista.getUsuario() != null ? especialista.getUsuario().getNombre() : "Usuario desvinculado");
        this.apellido = validarYObtenerString(especialista.getUsuario() != null ? especialista.getUsuario().getApellido() : "Usuario desvinculado");
        this.email = validarYObtenerString(especialista.getUsuario() != null ? especialista.getUsuario().getEmail() : "Usuario desvinculado");

        // Verificar ciudad
        this.ciudad = (especialista.getUsuario().getCiudad() != null)
                ? especialista.getUsuario().getCiudad()
                : "No especificado";

        this.telefono = validarYObtenerString(especialista.getUsuario() != null ? especialista.getUsuario().getTelefono() : "Usuario desvinculado");
        this.descripcion = validarYObtenerString(especialista.getDescripcion());

        // Verificar oficios
        this.oficios = especialista.getOficios();

        // Calcular calificación promedio
        this.calificacionPromedio = calcularPromedioCalificacion(especialista);

        // Verificar DNI
        this.dni = (especialista.getDni() != null)
                ? especialista.getDni()
                : 0;
    }

}
