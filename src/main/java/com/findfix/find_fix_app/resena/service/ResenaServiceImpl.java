package com.findfix.find_fix_app.resena.service;

import com.findfix.find_fix_app.auth.service.AuthService;
import com.findfix.find_fix_app.especialista.model.Especialista;
import com.findfix.find_fix_app.especialista.service.EspecialistaService;
import com.findfix.find_fix_app.exception.exceptions.ResenaNotFoundException;
import com.findfix.find_fix_app.exception.exceptions.SpecialistRequestNotFoundException;
import com.findfix.find_fix_app.exception.exceptions.TrabajoAppNotFoundException;
import com.findfix.find_fix_app.exception.exceptions.UserNotFoundException;
import com.findfix.find_fix_app.resena.dto.CrearResenaDTO;
import com.findfix.find_fix_app.resena.model.Resena;
import com.findfix.find_fix_app.resena.repository.ResenaRepository;
import com.findfix.find_fix_app.trabajo.trabajoApp.model.TrabajoApp;
import com.findfix.find_fix_app.trabajo.trabajoApp.service.TrabajoAppService;
import com.findfix.find_fix_app.usuario.model.Usuario;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ResenaServiceImpl implements ResenaService {

    @Autowired
    private ResenaRepository repository;

    @Autowired
    private AuthService autorizacion;

    @Autowired
    private EspecialistaService especialistaService;

    @Autowired
    private TrabajoAppService trabajoService;

    @Override
    @Transactional(readOnly = true)
    public Resena crearResena(CrearResenaDTO dto) throws UserNotFoundException, TrabajoAppNotFoundException {
        Usuario usuario = autorizacion.obtenerUsuarioAutenticado();

        TrabajoApp trabajo = trabajoService.buscarPorId(dto.getTrabajoId())
                .orElseThrow(() -> new TrabajoAppNotFoundException("Trabajo no encontrado"));

        boolean esCliente = trabajo.getUsuario().getUsuarioId().equals(usuario.getUsuarioId());
        boolean esEspecialista = trabajo.getEspecialista().getEspecialistaId().equals(usuario.getUsuarioId());

        if (!esCliente && !esEspecialista) {
            throw new UserNotFoundException("No estás autorizado para dejar una reseña sobre este trabajo.");
        }

        Resena resena = new Resena();
        resena.setComentario(dto.getComentario());
        resena.setPuntuacion(dto.getPuntuacion());
        resena.setTrabajoApp(trabajo);

        return repository.save(resena);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Resena> buscarPorTrabajoId(Long id) throws ResenaNotFoundException {
        Optional<Resena> resena = repository.findById(id);

        if (resena.isEmpty()) {
            throw new ResenaNotFoundException("No se encontró una reseña para el trabajo con id: " + id);
        }

        return resena;
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Resena> buscarPorTrabajoTitulo(String titulo) throws ResenaNotFoundException, TrabajoAppNotFoundException {
        TrabajoApp trabajo = trabajoService.buscarPorTitulo(titulo).orElseThrow(() -> new TrabajoAppNotFoundException("\n Trabajo no encontrado. "));

        return Optional.ofNullable(trabajo.getResena());

    }

    @Override
    @Transactional(readOnly = true)
    public List<CrearResenaDTO> ResenasDeMisTrabajos() throws UserNotFoundException, SpecialistRequestNotFoundException {
        Especialista especialista = especialistaService.obtenerEspecialistaAutenticado();

        List<Resena> resenas = repository.findAllByTrabajoApp_Especialista(especialista);

        return resenas.stream()
                .map(resena -> new CrearResenaDTO(
                        resena.getPuntuacion(),
                        resena.getComentario(),
                        resena.getTrabajoApp().getTrabajoAppId()
                ))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<CrearResenaDTO> ResenasHechasPorMi() throws UserNotFoundException {
        Usuario usuario = autorizacion.obtenerUsuarioAutenticado();

        List<Resena> resenas = repository.findAllByTrabajoApp_Usuario(usuario);

        return resenas.stream()
                .map(resena -> new CrearResenaDTO(
                        resena.getPuntuacion(),
                        resena.getComentario(),
                        resena.getTrabajoApp().getTrabajoAppId()
                ))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void borrarResena(Long id) throws ResenaNotFoundException {
        Optional<Resena> resena = buscarPorTrabajoId(id);

        repository.delete(resena.get());
    }
}