package com.findfix.find_fix_app.especialista.service;

import com.findfix.find_fix_app.especialista.dto.ActualizarEspecialistaDTO;
import com.findfix.find_fix_app.especialista.dto.ActualizarOficioEspDTO;
import com.findfix.find_fix_app.especialista.dto.EspecialistaDTO;
import com.findfix.find_fix_app.especialista.model.Especialista;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public interface EspecialistaService {

    Especialista guardar (Especialista especialista);
    Especialista actualizar(Long dni, ActualizarEspecialistaDTO dto);
    List<Especialista> obtenerEspecialistas();
    void eliminar(Long dni); ///ruta solo admitida para el admin
    Optional<Especialista>buscarPorDni(Long dni);
    Optional<Especialista>buscarPorId(Long id);
    Especialista modificarOficioDeEspecialista(Long dni, ActualizarOficioEspDTO dto);

}
