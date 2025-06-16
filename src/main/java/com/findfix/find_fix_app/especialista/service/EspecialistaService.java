package com.findfix.find_fix_app.especialista.service;

import com.findfix.find_fix_app.especialista.dto.ActualizarEspecialistaDTO;
import com.findfix.find_fix_app.especialista.dto.ActualizarOficioEspDTO;
import com.findfix.find_fix_app.especialista.model.Especialista;
import com.findfix.find_fix_app.exception.exceptions.EspecialistaExcepcion;
import com.findfix.find_fix_app.exception.exceptions.EspecialistaNotFoundException;
import com.findfix.find_fix_app.exception.exceptions.UserNotFoundException;
import com.findfix.find_fix_app.usuario.model.Usuario;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public interface EspecialistaService {

    Especialista guardar(Usuario usuario);

    Especialista obtenerEspecialistaAutenticado() throws UserNotFoundException, EspecialistaNotFoundException;
    Especialista actualizarEspecialistaAdmin(String email, ActualizarEspecialistaDTO dto) throws EspecialistaNotFoundException, EspecialistaExcepcion;
    Especialista actualizarEspecialista(ActualizarEspecialistaDTO dto) throws EspecialistaNotFoundException, UserNotFoundException, EspecialistaExcepcion;
    List<Especialista> obtenerEspecialistas() throws EspecialistaNotFoundException;
    List<Especialista> obtenerEspecialistasDisponibles() throws EspecialistaNotFoundException;
    void eliminarPorEmail(String email) throws EspecialistaNotFoundException; ///ruta solo admitida para el admin
    Optional<Especialista>buscarPorDni(Long dni);
    Optional<Especialista>buscarPorId(Long id);
    Especialista actualizarOficioDeEspecialista(ActualizarOficioEspDTO dto) throws EspecialistaNotFoundException, EspecialistaExcepcion, UserNotFoundException;
    Especialista actualizarOficioDeEspecialistaAdmin(String email, ActualizarOficioEspDTO dto) throws EspecialistaNotFoundException, EspecialistaExcepcion;
    List<Especialista> buscarPorOficio(String nombreOficio);
    List<Especialista> buscarPorCiudad(String ciudad);
    Optional<Especialista> buscarPorEmail(String email);

}
