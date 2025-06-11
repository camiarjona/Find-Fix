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
    public Oficio crearOficio(Oficio oficio) {
        return oficioRepository.save(oficio);
    }

    @Override
    public List<Oficio> buscarTodos() {
        return oficioRepository.findAll();
    }

    @Override
    public Optional<Oficio> buscarPorId(Long id) {
        return oficioRepository.findById(id);
    }

    @Override
    public Oficio modificarOficio(Long id, String nuevo) throws OficioNotFoundException {
        Optional<Oficio> existente = Optional.ofNullable(buscarPorId(id).orElseThrow(() -> new OficioNotFoundException("\n Oficio no encontrado. ")));
        if(existente.isPresent()){
            existente.get().setNombre(nuevo);
            return oficioRepository.save(existente.get());
        }
        return null;
    }

    @Override
    public void borrarOficioPorId(Long id) {
        oficioRepository.deleteById(id);
    }

    @Override
    public Oficio filtrarPorNombre(String nombreBuscado) throws OficioNotFoundException {
        Optional<Oficio> encontrado = oficioRepository.findByNombre(nombreBuscado);
        if(!encontrado.isPresent())
        {
            throw new OficioNotFoundException("El oficio que desea buscar no esta registrado en el sistema :(");
        }else
        {
            return encontrado.get();
        }
    }
}
