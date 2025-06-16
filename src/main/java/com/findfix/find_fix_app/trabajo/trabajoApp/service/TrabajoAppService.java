package com.findfix.find_fix_app.trabajo.trabajoApp.service;

import com.findfix.find_fix_app.especialista.model.Especialista;
import com.findfix.find_fix_app.exception.exceptions.SpecialistRequestNotFoundException;
import com.findfix.find_fix_app.exception.exceptions.TrabajoAppException;
import com.findfix.find_fix_app.exception.exceptions.TrabajoAppNotFoundException;
import com.findfix.find_fix_app.exception.exceptions.UserNotFoundException;
import com.findfix.find_fix_app.solicitudTrabajo.model.SolicitudTrabajo;
import com.findfix.find_fix_app.trabajo.trabajoApp.dto.ActualizarTrabajoAppDTO;
import com.findfix.find_fix_app.trabajo.trabajoApp.model.TrabajoApp;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public interface TrabajoAppService {
    TrabajoApp registrarDesdeSolicitud (SolicitudTrabajo solicitudTrabajo, Especialista especialista);
    List<TrabajoApp> obtenerTrabajosClientes() throws UserNotFoundException, TrabajoAppException;
    List<TrabajoApp> obtenerTrabajosEspecialista() throws UserNotFoundException, TrabajoAppException, SpecialistRequestNotFoundException;
    List<TrabajoApp> obtenerTrabajosEspecialistaEstado(String nombreEstado) throws UserNotFoundException, SpecialistRequestNotFoundException, TrabajoAppException;
    /// Optional<TrabajoApp> buscarPorEstado(String nombreEstado);
    Optional<TrabajoApp> buscarPorTitulo(String tituloBuscado);
    TrabajoApp actualizarTrabajo(String titulo, ActualizarTrabajoAppDTO dto) throws TrabajoAppNotFoundException, TrabajoAppException, UserNotFoundException, SpecialistRequestNotFoundException;
    void modificarEstadoTrabajo(String titulo,String nombreEstado) throws TrabajoAppNotFoundException, TrabajoAppException, UserNotFoundException, SpecialistRequestNotFoundException;
    TrabajoApp obtenerFichaDeTrabajoParaEspecialista(String titulo) throws TrabajoAppNotFoundException, UserNotFoundException, TrabajoAppException, SpecialistRequestNotFoundException;
    TrabajoApp obtenerFichaDeTrabajoParaCliente(Long id) throws UserNotFoundException, TrabajoAppException, TrabajoAppNotFoundException;

}
