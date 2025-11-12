package com.findfix.find_fix_app.solicitudTrabajo.service;

import com.findfix.find_fix_app.especialista.model.Especialista;
import com.findfix.find_fix_app.utils.exception.exceptions.SolicitudTrabajoException;
import com.findfix.find_fix_app.utils.exception.exceptions.SolicitudTrabajoNotFoundException;
import com.findfix.find_fix_app.utils.exception.exceptions.EspecialistaNotFoundException;
import com.findfix.find_fix_app.utils.exception.exceptions.UsuarioNotFoundException;
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
        SolicitudTrabajo registrarNuevaSolicitud(SolicitarTrabajoDTO solicitarTrabajoDTO) throws UsuarioNotFoundException, EspecialistaNotFoundException;
        void actualizarEstadoSolicitud(ActualizarEstadoDTO actualizar, Long idSolicitud) throws SolicitudTrabajoNotFoundException, UsuarioNotFoundException, EspecialistaNotFoundException, SolicitudTrabajoException;
        List<SolicitudTrabajo> obtenerSolicitudesDelCliente() throws UsuarioNotFoundException, SolicitudTrabajoException;
        List<SolicitudTrabajo> obtenerSolicitudesDelEspecialista() throws UsuarioNotFoundException, EspecialistaNotFoundException, SolicitudTrabajoException;
        void eliminarSolicitud(Long idSolicitud) throws SolicitudTrabajoNotFoundException, UsuarioNotFoundException, SolicitudTrabajoException;
        Optional<SolicitudTrabajo> buscarPorId(Long id);
        List<SolicitudTrabajo> filtrarSolicitudesRecibidas(BuscarSolicitudDTO buscarSolicitudDTO) throws SolicitudTrabajoException, UsuarioNotFoundException, EspecialistaNotFoundException;

        List<SolicitudTrabajo> filtrarSolicitudesEnviadas(BuscarSolicitudDTO filtro) throws SolicitudTrabajoException, UsuarioNotFoundException;

        void validarEstado(SolicitudTrabajo solicitudTrabajo) throws SolicitudTrabajoException;
        void validarCliente(SolicitudTrabajo solicitudTrabajo, Usuario usuario) throws SolicitudTrabajoException;
        void validarEspecialista(SolicitudTrabajo solicitudTrabajo, Especialista especialista) throws SolicitudTrabajoException;
        boolean estaPendiente(SolicitudTrabajo solicitudTrabajo);
}
