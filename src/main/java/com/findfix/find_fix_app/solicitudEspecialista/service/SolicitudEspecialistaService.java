package com.findfix.find_fix_app.solicitudEspecialista.service;

import com.findfix.find_fix_app.utils.exception.exceptions.RolNotFoundException;
import com.findfix.find_fix_app.utils.exception.exceptions.SolicitudEspecialistaException;
import com.findfix.find_fix_app.utils.exception.exceptions.SolicitudEspecialistaNotFoundException;
import com.findfix.find_fix_app.utils.exception.exceptions.UserNotFoundException;
import com.findfix.find_fix_app.solicitudEspecialista.dto.ActualizarSolicitudEspecialistaDTO;
import com.findfix.find_fix_app.solicitudEspecialista.dto.BuscarSolicitudEspecialistaDTO;
import com.findfix.find_fix_app.solicitudEspecialista.dto.MandarSolicitudEspecialistaDTO;
import com.findfix.find_fix_app.solicitudEspecialista.dto.FichaCompletaSolicitudEspecialistaDTO;
import com.findfix.find_fix_app.solicitudEspecialista.model.SolicitudEspecialista;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface SolicitudEspecialistaService {

    SolicitudEspecialista mandarSolicitud (MandarSolicitudEspecialistaDTO dto) throws UserNotFoundException, SolicitudEspecialistaException;
    List<SolicitudEspecialista> obtenerSolicitudesEspecialista() throws SolicitudEspecialistaNotFoundException;
    SolicitudEspecialista actualizarSolicitudEspecialistaAdmin(ActualizarSolicitudEspecialistaDTO dto, Long id) throws SolicitudEspecialistaNotFoundException, UserNotFoundException, RolNotFoundException, SolicitudEspecialistaException;
    void eliminarPorId(Long id) throws SolicitudEspecialistaNotFoundException, SolicitudEspecialistaException;
    List<SolicitudEspecialista> obtenerMisSolicitudesEspecialista() throws SolicitudEspecialistaException, SolicitudEspecialistaNotFoundException, UserNotFoundException;
    List<FichaCompletaSolicitudEspecialistaDTO> filtrarSolicitudes(BuscarSolicitudEspecialistaDTO filtro) throws SolicitudEspecialistaException;

    }
