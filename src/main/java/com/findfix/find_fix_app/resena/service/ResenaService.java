package com.findfix.find_fix_app.resena.service;

import com.findfix.find_fix_app.utils.exception.exceptions.*;
import com.findfix.find_fix_app.resena.dto.CrearResenaDTO;
import com.findfix.find_fix_app.resena.model.Resena;
import com.findfix.find_fix_app.utils.exception.exceptions.TrabajoAppNotFoundException;
import com.findfix.find_fix_app.utils.exception.exceptions.UsuarioNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public interface ResenaService {
    Resena crearResena(CrearResenaDTO DTO) throws TrabajoAppNotFoundException, UsuarioNotFoundException;
    Optional<Resena> buscarPorId(Long id) throws ResenaNotFoundException;
    Optional<Resena> buscarPorTrabajoTitulo(String titulo) throws ResenaNotFoundException, TrabajoAppNotFoundException;
    List<Resena> resenasDeMisTrabajos() throws UsuarioNotFoundException, EspecialistaExcepcion, EspecialistaNotFoundException;
    List<Resena> resenasHechasPorMi() throws UsuarioNotFoundException;
    public void borrarResena(Long id) throws ResenaNotFoundException;

}
