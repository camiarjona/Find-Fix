package com.findfix.find_fix_app.oficio.service;

import com.findfix.find_fix_app.utils.exception.exceptions.OficioException;
import com.findfix.find_fix_app.utils.exception.exceptions.OficioNotFoundException;
import com.findfix.find_fix_app.oficio.model.Oficio;
import com.findfix.find_fix_app.oficio.repository.OficioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class OficioServiceImpl implements OficioService {

    private final OficioRepository oficioRepository;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Oficio crearOficio(Oficio oficio) throws OficioException {

    if(oficioRepository.existsByNombreIgnoreCase(oficio.getNombre())){
        throw new OficioException("Ya existe un oficio con ese nombre");
    }
        return oficioRepository.save(oficio);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Oficio> buscarTodos() {
        return oficioRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Oficio> buscarPorId(Long id) {
        return oficioRepository.findById(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void modificarOficio(Long id, Oficio nuevo) throws OficioNotFoundException {
        Optional<Oficio> existente = Optional.ofNullable(buscarPorId(id).orElseThrow(() -> new OficioNotFoundException("\n Oficio no encontrado. ")));
            existente.get().setNombre(nuevo.getNombre());
            oficioRepository.save(existente.get());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void borrarOficioPorId(Long id) throws OficioNotFoundException {
        if(oficioRepository.findById(id).isEmpty())
        {
            throw new OficioNotFoundException("El id ingresado no pertenece a ningun oficio registrado");
        }
        oficioRepository.deleteById(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Oficio filtrarPorNombre(String nombreBuscado) throws OficioNotFoundException {
        Optional<Oficio> encontrado = oficioRepository.findByNombre(nombreBuscado);
        if(encontrado.isEmpty())
        {
            throw new OficioNotFoundException("El oficio que desea buscar no esta registrado en el sistema :(");
        }else
        {
            return encontrado.get();
        }
    }

}
