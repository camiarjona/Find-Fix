package com.findfix.find_fix_app.oficio.service;

import com.findfix.find_fix_app.exception.exceptions.OficioNotFoundException;
import com.findfix.find_fix_app.oficio.model.Oficio;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public interface OficioService {

    Oficio crearOficio(Oficio oficio);
    List<Oficio> buscarTodos();
    Optional<Oficio> buscarPorId(Long id);
    Oficio modificarOficio(Long id, String nuevo) throws OficioNotFoundException;
    void borrarOficioPorId(Long id);

}
