package com.findfix.find_fix_app.oficio.service;

import com.findfix.find_fix_app.utils.exception.exceptions.OficioNotFoundException;
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
    public void modificarOficio(Long id, Oficio nuevo) throws OficioNotFoundException {
        Optional<Oficio> existente = Optional.ofNullable(buscarPorId(id).orElseThrow(() -> new OficioNotFoundException("\n Oficio no encontrado. ")));
            existente.get().setNombre(nuevo.getNombre());
            oficioRepository.save(existente.get());
    }

    @Override
    public void borrarOficioPorId(Long id) {
        oficioRepository.deleteById(id);
    }

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
