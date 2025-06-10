package com.findfix.find_fix_app.especialista.service;

import com.findfix.find_fix_app.especialista.dto.ActualizarEspecialistaDTO;
import com.findfix.find_fix_app.especialista.dto.ActualizarOficioEspDTO;
import com.findfix.find_fix_app.especialista.model.Especialista;
import com.findfix.find_fix_app.exception.exceptions.EspecialistaExcepcion;
import com.findfix.find_fix_app.exception.exceptions.SpecialistRequestNotFoundException;
import com.findfix.find_fix_app.exception.exceptions.UserNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public interface EspecialistaService {

    Especialista guardar (Long id) throws UserNotFoundException;
    Especialista actualizarEspecialista(String email, ActualizarEspecialistaDTO dto) throws SpecialistRequestNotFoundException;
    List<Especialista> obtenerEspecialistas();
    List<Especialista> obtenerEspecialistasDisponibles();
    void eliminarPorEmail(String email) throws SpecialistRequestNotFoundException; ///ruta solo admitida para el admin
    Optional<Especialista>buscarPorDni(Long dni);
    Optional<Especialista>buscarPorId(Long id);
    Especialista actualizarOficioDeEspecialista(String email, ActualizarOficioEspDTO dto) throws SpecialistRequestNotFoundException, EspecialistaExcepcion;
    List<Especialista> buscarPorOficio(String nombreOficio);
    List<Especialista> buscarPorCiudad(String ciudad);
    Optional<Especialista> buscarPorEmail(String email);


}
