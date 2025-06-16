package com.findfix.find_fix_app.solicitudTrabajo.service;

import com.findfix.find_fix_app.especialista.model.Especialista;
import com.findfix.find_fix_app.exception.exceptions.SolicitudTrabajoException;
import com.findfix.find_fix_app.exception.exceptions.SolicitudTrabajoNotFoundException;
import com.findfix.find_fix_app.exception.exceptions.SpecialistRequestNotFoundException;
import com.findfix.find_fix_app.exception.exceptions.UserNotFoundException;
import com.findfix.find_fix_app.solicitudTrabajo.dto.ActualizarEstadoDTO;
import com.findfix.find_fix_app.solicitudTrabajo.dto.BuscarSolicitudDTO;
import com.findfix.find_fix_app.solicitudTrabajo.dto.SolicitarTrabajoDTO;
import com.findfix.find_fix_app.solicitudTrabajo.model.SolicitudTrabajo;
import com.findfix.find_fix_app.usuario.model.Usuario;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public interface SolicitudTrabajoService {
        SolicitudTrabajo registrarNuevaSolicitud(SolicitarTrabajoDTO solicitarTrabajoDTO) throws UserNotFoundException, SpecialistRequestNotFoundException;
        void actualizarEstadoSolicitud(ActualizarEstadoDTO actualizar, Long idSolicitud) throws SolicitudTrabajoNotFoundException, UserNotFoundException, SpecialistRequestNotFoundException, SolicitudTrabajoException;
        List<SolicitudTrabajo> obtenerSolicitudesDelCliente() throws UserNotFoundException, SolicitudTrabajoException;
        List<SolicitudTrabajo> obtenerSolicitudesDelEspecialista() throws UserNotFoundException, SpecialistRequestNotFoundException, SolicitudTrabajoException;
        void eliminarSolicitud(Long idSolicitud) throws SolicitudTrabajoNotFoundException, UserNotFoundException, SolicitudTrabajoException;
        Optional<SolicitudTrabajo> buscarPorId(Long id);
        List<SolicitudTrabajo> filtrarSolicitudes(BuscarSolicitudDTO buscarSolicitudDTO) throws SolicitudTrabajoException;
        void validarEstado(SolicitudTrabajo solicitudTrabajo) throws SolicitudTrabajoException;
        void validarCliente(SolicitudTrabajo solicitudTrabajo, Usuario usuario) throws SolicitudTrabajoException;
        void validarEspecialista(SolicitudTrabajo solicitudTrabajo, Especialista especialista) throws SolicitudTrabajoException;
        boolean estaPendiente(SolicitudTrabajo solicitudTrabajo);
}
