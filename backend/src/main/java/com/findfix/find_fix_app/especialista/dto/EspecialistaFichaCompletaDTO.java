package com.findfix.find_fix_app.especialista.dto;

import com.findfix.find_fix_app.especialista.interfaz.DatosEspecialista;
import com.findfix.find_fix_app.especialista.model.Especialista;
import lombok.Data;
import java.util.Set;


@Data
public class EspecialistaFichaCompletaDTO implements DatosEspecialista {
    private String descripcion;
    private String nombre;
    private String apellido;
    private String telefono;
    private String ciudad;
    private Set<String> oficios;
    private String email;
    private Double calificacionPromedio;
    private Double latitud;
    private Double longitud;
    private String fotoUrl;


    public EspecialistaFichaCompletaDTO(Especialista especialista) {
            this.nombre = validarYObtenerString(especialista.getUsuario() != null ? especialista.getUsuario().getNombre() : "Usuario desvinculado");
            this.apellido = validarYObtenerString(especialista.getUsuario() != null ? especialista.getUsuario().getApellido() : "Usuario desvinculado");
            this.telefono = validarYObtenerString(especialista.getUsuario() != null ? especialista.getUsuario().getTelefono() : "Usuario desvinculado");
            this.email = validarYObtenerString(especialista.getUsuario() != null ? especialista.getUsuario().getEmail() : "Usuario desvinculado");

            // Validar ciudad
            this.ciudad = (especialista.getUsuario().getCiudad() != null)
                    ? especialista.getUsuario().getCiudad()
                    : "No especificado";

            this.descripcion = validarYObtenerString(especialista.getDescripcion());

            // Validar oficios
            this.oficios = obtenerOficiosSet(especialista);

            // Calcular calificación
            this.calificacionPromedio = calcularPromedioCalificacion(especialista);

            this.latitud = especialista.getUsuario().getLatitud();
            this.longitud = especialista.getUsuario().getLongitud();
            this.fotoUrl = (especialista.getUsuario() != null) ? especialista.getUsuario().getFotoUrl() : null;
        }

}

