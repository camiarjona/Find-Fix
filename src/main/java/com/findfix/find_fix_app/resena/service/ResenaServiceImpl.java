package com.findfix.find_fix_app.resena.service;

import com.findfix.find_fix_app.utils.auth.AuthService;
import com.findfix.find_fix_app.especialista.model.Especialista;
import com.findfix.find_fix_app.especialista.service.EspecialistaService;
import com.findfix.find_fix_app.utils.exception.exceptions.*;
import com.findfix.find_fix_app.resena.dto.CrearResenaDTO;
import com.findfix.find_fix_app.resena.model.Resena;
import com.findfix.find_fix_app.resena.repository.ResenaRepository;
import com.findfix.find_fix_app.trabajo.trabajoApp.model.TrabajoApp;
import com.findfix.find_fix_app.trabajo.trabajoApp.service.TrabajoAppService;
import com.findfix.find_fix_app.usuario.model.Usuario;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ResenaServiceImpl implements ResenaService {

    private final ResenaRepository repository;
    private final AuthService autorizacion;
    private final EspecialistaService especialistaService;
    private final TrabajoAppService trabajoService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Resena crearResena(CrearResenaDTO dto) throws UsuarioNotFoundException, TrabajoAppNotFoundException {
        Usuario usuario = autorizacion.obtenerUsuarioAutenticado();

        TrabajoApp trabajo = trabajoService.buscarPorId(dto.getTrabajoId())
                .orElseThrow(() -> new TrabajoAppNotFoundException("Trabajo no encontrado"));

        boolean esCliente = trabajo.getUsuario().getUsuarioId().equals(usuario.getUsuarioId());
        boolean esEspecialista = trabajo.getEspecialista().getEspecialistaId().equals(usuario.getUsuarioId());

        if (!esCliente && !esEspecialista) {
            throw new UsuarioNotFoundException("No estás autorizado para dejar una reseña sobre este trabajo.");
        }

        Resena resena = new Resena();
        resena.setComentario(dto.getComentario());
        resena.setPuntuacion(dto.getPuntuacion());
        resena.setTrabajoApp(trabajo);

        return repository.save(resena);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Resena> buscarPorId(Long id){
        return repository.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Resena> buscarPorTrabajoTitulo(String titulo) throws TrabajoAppNotFoundException, ResenaNotFoundException {
        TrabajoApp trabajo = trabajoService.buscarPorTitulo(titulo).orElseThrow(() -> new TrabajoAppNotFoundException("Trabajo no encontrado."));

        if(trabajo.getResena() == null) {
            throw new ResenaNotFoundException("Este trabajo aun no tiene reseña.");
        }

        return Optional.of(trabajo.getResena());
    }

    @Override
    @Transactional(readOnly = true)
    public List<Resena> resenasDeMisTrabajos() throws UsuarioNotFoundException, EspecialistaNotFoundException {
        Especialista especialista = especialistaService.obtenerEspecialistaAutenticado();
        return repository.findAllByTrabajoApp_Especialista(especialista);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Resena> resenasHechasPorMi() throws UsuarioNotFoundException {
        Usuario usuario = autorizacion.obtenerUsuarioAutenticado();
        return repository.findAllByTrabajoApp_Usuario(usuario);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void borrarResena(Long id) throws ResenaNotFoundException {
        Resena resena = buscarPorId(id)
                .orElseThrow(() -> new ResenaNotFoundException("No se encontró una reseña con el id: " + id));

        repository.delete(resena);
    }
}