package com.findfix.find_fix_app.trabajo.trabajoExterno.service;

import com.findfix.find_fix_app.especialista.model.Especialista;
import com.findfix.find_fix_app.trabajo.trabajoExterno.dto.BuscarTrabajoExternoDTO;
import com.findfix.find_fix_app.trabajo.trabajoExterno.dto.CrearTrabajoExternoDTO;
import com.findfix.find_fix_app.trabajo.trabajoExterno.dto.ModificarTrabajoExternoDTO;
import com.findfix.find_fix_app.trabajo.trabajoExterno.model.TrabajoExterno;
import com.findfix.find_fix_app.utils.exception.exceptions.*;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface TrabajoExternoService {
    TrabajoExterno crearTrabajoExterno(CrearTrabajoExternoDTO DTO) throws UsuarioNotFoundException, EspecialistaNotFoundException;
    List<TrabajoExterno> obtenerMisTrabajos() throws UsuarioNotFoundException, EspecialistaNotFoundException, TrabajoExternoException;
    List<TrabajoExterno> filtrarTrabajosExternos(BuscarTrabajoExternoDTO filtro) throws UsuarioNotFoundException, EspecialistaNotFoundException, TrabajoExternoException;
    void modificarTrabajoExterno(String titulo, ModificarTrabajoExternoDTO DTO) throws OficioNotFoundException, TrabajoExternoNotFoundException, UsuarioNotFoundException, EspecialistaNotFoundException, TrabajoExternoException;
    void actualizarEstado(String titulo, String estadoNuevo) throws UsuarioNotFoundException, EspecialistaNotFoundException, TrabajoAppNotFoundException, TrabajoExternoException;
    void borrarTrabajoExternoPorTitulo(String titulo) throws UsuarioNotFoundException, EspecialistaNotFoundException, TrabajoExternoException;
    void validarEspecialista(TrabajoExterno trabajoExterno, Especialista especialista) throws TrabajoExternoException;
    void validarEstado(TrabajoExterno trabajo, String estado) throws TrabajoExternoException;
}
