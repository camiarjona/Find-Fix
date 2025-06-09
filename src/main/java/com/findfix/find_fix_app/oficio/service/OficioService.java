package com.findfix.find_fix_app.oficio.service;

import com.findfix.find_fix_app.exception.exceptions.OficioNotFoundException;
import com.findfix.find_fix_app.oficio.model.Oficio;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public interface OficioService {

    Oficio saveOficio(Oficio oficio);
    List<Oficio> findAll();
    Optional<Oficio> findById(Long id);
    Oficio updateOficio(Long id, String nuevo) throws OficioNotFoundException;
    void delete(Long id);
    Oficio filtrarPorNombre(String nombreBuscado) throws OficioNotFoundException;

}
