package com.findfix.find_fix_app.trabajo.trabajoApp.service;

import com.findfix.find_fix_app.auth.service.AuthService;
import com.findfix.find_fix_app.exception.exceptions.SpecialistRequestNotFoundException;
import com.findfix.find_fix_app.exception.exceptions.TrabajoAppException;
import com.findfix.find_fix_app.exception.exceptions.TrabajoAppNotFoundException;
import com.findfix.find_fix_app.exception.exceptions.UserNotFoundException;
import com.findfix.find_fix_app.trabajo.trabajoApp.dto.ActualizarTrabajoAppDTO;
import com.findfix.find_fix_app.trabajo.trabajoApp.model.TrabajoApp;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public interface TrabajoAppService {
    void guardarTrabajo(TrabajoApp trabajoApp);
    List<TrabajoApp> obtenerTrabajosClientes() throws UserNotFoundException, TrabajoAppException;
    List<TrabajoApp> obtenerTrabajosEspecialista() throws UserNotFoundException, TrabajoAppException, SpecialistRequestNotFoundException;
    List<TrabajoApp> obtenerTrabajosEspecialistaEstado(String nombreEstado) throws UserNotFoundException, SpecialistRequestNotFoundException, TrabajoAppException;
    /// Optional<TrabajoApp> buscarPorEstado(String nombreEstado);
    Optional<TrabajoApp> buscarPorTitulo(String tituloBuscado);
    TrabajoApp actualizarTrabajo(String titulo, ActualizarTrabajoAppDTO dto) throws TrabajoAppNotFoundException, TrabajoAppException, UserNotFoundException, SpecialistRequestNotFoundException;
    void modificarEstadoTrabajo(String titulo,String nombreEstado) throws TrabajoAppNotFoundException, TrabajoAppException, UserNotFoundException, SpecialistRequestNotFoundException;
    TrabajoApp obtenerFichaDeTrabajoParaEspecialista(String titulo) throws TrabajoAppNotFoundException, UserNotFoundException, TrabajoAppException, SpecialistRequestNotFoundException;


}
