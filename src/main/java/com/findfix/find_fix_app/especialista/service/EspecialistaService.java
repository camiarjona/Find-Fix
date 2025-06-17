package com.findfix.find_fix_app.especialista.service;

import com.findfix.find_fix_app.especialista.dto.*;
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
    Especialista actualizarOficioDeEspecialista(ActualizarOficioEspDTO dto) throws EspecialistaNotFoundException, EspecialistaExcepcion, UserNotFoundException;
    Especialista actualizarOficioDeEspecialistaAdmin(String email, ActualizarOficioEspDTO dto) throws EspecialistaNotFoundException, EspecialistaExcepcion;
    Optional<Especialista> buscarPorEmail(String email);
    VerPerfilEspecialistaDTO verPerfilEspecialista() throws UserNotFoundException, EspecialistaNotFoundException;
    List<EspecialistaFichaCompletaDTO> filtrarEspecialistas(BuscarEspecialistaDTO filtro) throws EspecialistaExcepcion;
}
