package com.findfix.find_fix_app.oficio.service;

import com.findfix.find_fix_app.exception.exceptions.OficioNotFoundException;
import com.findfix.find_fix_app.oficio.model.Oficio;
import com.findfix.find_fix_app.oficio.repository.OficioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class OficioServiceImpl implements OficioService {

    @Autowired
    private OficioRepository oficioRepository;

    @Override
    public Oficio saveOficio(Oficio oficio) {
        return oficioRepository.save(oficio);
    }

    @Override
    public List<Oficio> findAll() {
        return oficioRepository.findAll();
    }

    @Override
    public Optional<Oficio> findById(Long id) {
        return oficioRepository.findById(id);
    }

    @Override
    public Oficio updateOficio(Long id, Oficio nuevo) throws OficioNotFoundException {
        Optional<Oficio> existente = Optional.ofNullable(findById(id).orElseThrow(() -> new OficioNotFoundException("\n Oficio no encontrado. ")));
        if(existente.isPresent()){
            existente.get().setNombre(nuevo.getNombre());
            return oficioRepository.save(existente.get());
        }
        return null;
    }

    @Override
    public void delete(Long id) {
        oficioRepository.deleteById(id);
    }
}
