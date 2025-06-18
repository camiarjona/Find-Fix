package com.findfix.find_fix_app.trabajo.trabajoApp.service;

import com.findfix.find_fix_app.especialista.model.Especialista;
import com.findfix.find_fix_app.trabajo.trabajoExterno.dto.BuscarTrabajoExternoDTO;
import com.findfix.find_fix_app.utils.exception.exceptions.*;
import com.findfix.find_fix_app.solicitudTrabajo.model.SolicitudTrabajo;
import com.findfix.find_fix_app.trabajo.trabajoApp.dto.ActualizarTrabajoAppDTO;
import com.findfix.find_fix_app.trabajo.trabajoApp.model.TrabajoApp;
import jakarta.validation.constraints.NotNull;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

public interface TrabajoAppService {
    void registrarDesdeSolicitud (SolicitudTrabajo solicitudTrabajo, Especialista especialista);
    List<TrabajoApp> obtenerTrabajosClientes() throws UsuarioNotFoundException, TrabajoAppException;
    List<TrabajoApp> obtenerTrabajosEspecialista() throws UsuarioNotFoundException, TrabajoAppException, EspecialistaNotFoundException;
    Optional<TrabajoApp> buscarPorTitulo(String tituloBuscado);
    TrabajoApp actualizarTrabajo(String titulo, ActualizarTrabajoAppDTO dto) throws TrabajoAppNotFoundException, TrabajoAppException, UsuarioNotFoundException, EspecialistaNotFoundException;
    void modificarEstadoTrabajo(String titulo,String nombreEstado) throws TrabajoAppNotFoundException, TrabajoAppException, UsuarioNotFoundException, EspecialistaNotFoundException;
    TrabajoApp obtenerFichaDeTrabajoParaEspecialista(String titulo) throws TrabajoAppNotFoundException, UsuarioNotFoundException, TrabajoAppException, EspecialistaNotFoundException;
    TrabajoApp obtenerFichaDeTrabajoParaCliente(Long id) throws UsuarioNotFoundException, TrabajoAppException, TrabajoAppNotFoundException;
    void validarEspecialista(TrabajoApp trabajoApp, Especialista especialista) throws TrabajoAppException;
    Optional<TrabajoApp> buscarPorId(@NotNull(message = "Debe indicarse el ID del trabajo asociado") Long trabajoId);
    List<TrabajoApp> filtrarTrabajosApp(BuscarTrabajoExternoDTO filtro) throws UsuarioNotFoundException, EspecialistaNotFoundException, TrabajoAppException;

    @Transactional(readOnly = true)
    List<TrabajoApp> filtrarPorEstadoCliente(String estado) throws UsuarioNotFoundException, TrabajoAppException;
}
