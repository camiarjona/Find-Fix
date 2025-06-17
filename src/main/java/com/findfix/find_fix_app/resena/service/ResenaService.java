package com.findfix.find_fix_app.resena.service;

import com.findfix.find_fix_app.especialista.model.Especialista;
import com.findfix.find_fix_app.exception.exceptions.ResenaNotFoundException;
import com.findfix.find_fix_app.exception.exceptions.SpecialistRequestNotFoundException;
import com.findfix.find_fix_app.exception.exceptions.TrabajoAppNotFoundException;
import com.findfix.find_fix_app.exception.exceptions.UserNotFoundException;
import com.findfix.find_fix_app.resena.dto.CrearResenaDTO;
import com.findfix.find_fix_app.resena.dto.MostrarResenaDTO;
import com.findfix.find_fix_app.resena.model.Resena;
import com.findfix.find_fix_app.trabajo.trabajoExterno.dto.TrabajoExternoDTO;
import com.findfix.find_fix_app.usuario.model.Usuario;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public interface ResenaService {
    Resena crearResena(CrearResenaDTO DTO) throws TrabajoAppNotFoundException, UserNotFoundException;
    Optional<Resena> buscarPorTrabajoId(Long id) throws ResenaNotFoundException;
    Optional<Resena> buscarPorTrabajoTitulo(String titulo) throws ResenaNotFoundException, TrabajoAppNotFoundException;
    List<CrearResenaDTO> ResenasDeMisTrabajos() throws UserNotFoundException, SpecialistRequestNotFoundException;
    List<CrearResenaDTO> ResenasHechasPorMi() throws UserNotFoundException;
    public void borrarResena(Long id) throws ResenaNotFoundException;

}
