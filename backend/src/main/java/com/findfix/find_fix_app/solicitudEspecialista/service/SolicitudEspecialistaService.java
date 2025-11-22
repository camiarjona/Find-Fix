package com.findfix.find_fix_app.solicitudEspecialista.service;

import com.findfix.find_fix_app.solicitudEspecialista.dto.*;
import com.findfix.find_fix_app.utils.exception.exceptions.RolNotFoundException;
import com.findfix.find_fix_app.utils.exception.exceptions.SolicitudEspecialistaException;
import com.findfix.find_fix_app.utils.exception.exceptions.SolicitudEspecialistaNotFoundException;
import com.findfix.find_fix_app.utils.exception.exceptions.UsuarioNotFoundException;
import com.findfix.find_fix_app.solicitudEspecialista.model.SolicitudEspecialista;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface SolicitudEspecialistaService {

    void mandarSolicitud (MandarSolicitudEspecialistaDTO dto) throws UsuarioNotFoundException, SolicitudEspecialistaException;
    List<MostrarSolicitudEspecialistaAdminDTO> obtenerSolicitudesEspecialista() throws SolicitudEspecialistaNotFoundException;
    SolicitudEspecialista actualizarSolicitudEspecialistaAdmin(ActualizarSolicitudEspecialistaDTO dto, Long id) throws SolicitudEspecialistaNotFoundException, UsuarioNotFoundException, RolNotFoundException, SolicitudEspecialistaException;
    void eliminarPorId(Long id) throws SolicitudEspecialistaNotFoundException, SolicitudEspecialistaException;
    List<SolicitudEspecialista> obtenerMisSolicitudesEspecialista() throws SolicitudEspecialistaException, SolicitudEspecialistaNotFoundException, UsuarioNotFoundException;
    List<FichaCompletaSolicitudEspecialistaDTO> filtrarSolicitudes(BuscarSolicitudEspecialistaDTO filtro) throws SolicitudEspecialistaException, UsuarioNotFoundException;
    FichaCompletaSolicitudEspecialistaDTO obtenerFichaPorId(Long id) throws SolicitudEspecialistaNotFoundException, UsuarioNotFoundException, SolicitudEspecialistaException;
    }
