package com.findfix.find_fix_app.oficio.service;

import org.springframework.stereotype.Service;

@Service
public interface OficioService {

    Oficio saveOficio(Oficio oficio);
    List<Oficio> findAll();
    Optional<Oficio> findById(Long id);
    Oficio updateOficio(Long id, Oficio nuevo) throws OficioNotFoundException;
    void delete(Long id);

}
