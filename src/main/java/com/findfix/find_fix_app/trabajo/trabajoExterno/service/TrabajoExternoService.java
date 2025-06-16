package com.findfix.find_fix_app.trabajo.trabajoExterno.service;

import com.findfix.find_fix_app.especialista.model.Especialista;
import com.findfix.find_fix_app.exception.exceptions.*;
import com.findfix.find_fix_app.trabajo.trabajoExterno.dto.BuscarTrabajoExternoDTO;
import com.findfix.find_fix_app.trabajo.trabajoExterno.dto.CrearTrabajoExternoDTO;
import com.findfix.find_fix_app.trabajo.trabajoExterno.dto.ModificarTrabajoExternoDTO;
import com.findfix.find_fix_app.trabajo.trabajoExterno.model.TrabajoExterno;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface TrabajoExternoService {
    TrabajoExterno crearTrabajoExterno(CrearTrabajoExternoDTO DTO) throws UserNotFoundException, EspecialistaNotFoundException;
    List<TrabajoExterno> obtenerMisTrabajos() throws UserNotFoundException, EspecialistaNotFoundException, TrabajoExternoException;
    List<TrabajoExterno> filtrarTrabajosExternos(BuscarTrabajoExternoDTO filtro) throws UserNotFoundException, EspecialistaNotFoundException, TrabajoExternoException;
    void modificarTrabajoExterno(String titulo, ModificarTrabajoExternoDTO DTO) throws OficioNotFoundException, TrabajoExternoNotFoundException, UserNotFoundException, EspecialistaNotFoundException, TrabajoExternoException;
    void actualizarEstado(String titulo, String estadoNuevo) throws UserNotFoundException, EspecialistaNotFoundException, TrabajoAppNotFoundException, TrabajoExternoException;
    void borrarTrabajoExternoPorTitulo(String titulo) throws UserNotFoundException, EspecialistaNotFoundException, TrabajoExternoException;
    void validarEspecialista(TrabajoExterno trabajoExterno, Especialista especialista) throws TrabajoExternoException;
    void validarEstado(TrabajoExterno trabajo, String estado) throws TrabajoExternoException;
}
