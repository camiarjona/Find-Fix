package com.findfix.find_fix_app.resena.service;

import com.findfix.find_fix_app.utils.auth.service.AuthServiceImpl;
import com.findfix.find_fix_app.especialista.model.Especialista;
import com.findfix.find_fix_app.especialista.service.EspecialistaService;
import com.findfix.find_fix_app.utils.enums.EstadosTrabajos;
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
import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ResenaServiceImpl implements ResenaService {

    private final ResenaRepository repository;
    private final AuthServiceImpl autorizacion;
    private final EspecialistaService especialistaService;
    private final TrabajoAppService trabajoService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Resena crearResena(CrearResenaDTO dto) throws UsuarioNotFoundException, TrabajoAppNotFoundException, TrabajoAppException {
        Usuario usuario = autorizacion.obtenerUsuarioAutenticado();

        TrabajoApp trabajo = trabajoService.buscarPorId(dto.getTrabajoId())
                .orElseThrow(() -> new TrabajoAppNotFoundException("Trabajo no encontrado"));

        boolean esCliente = trabajo.getUsuario().getUsuarioId().equals(usuario.getUsuarioId());

        if (!esCliente) {
            throw new UsuarioNotFoundException("No estás autorizado para dejar una reseña sobre este trabajo.");
        }

        if(trabajo.getEstado() != EstadosTrabajos.FINALIZADO){
            throw new TrabajoAppException("El trabajo debe estar finalizado para agregar una reseña. ");
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
    public Optional<Resena> buscarPorTrabajoTitulo(String titulo) throws TrabajoAppNotFoundException, ResenaNotFoundException, EspecialistaNotFoundException, UsuarioNotFoundException, ResenaException {

        Especialista especialista = especialistaService.obtenerEspecialistaAutenticado();
        TrabajoApp trabajo = trabajoService.buscarPorTitulo(titulo).orElseThrow(() -> new TrabajoAppNotFoundException("Trabajo no encontrado."));

        validarEspecialista(trabajo, especialista);

        if(trabajo.getResena() == null) {
            throw new ResenaNotFoundException("Este trabajo aun no tiene reseña.");
        }

        return Optional.of(trabajo.getResena());
    }

    @Override
    @Transactional(readOnly = true)
    public List<Resena> resenasDeMisTrabajos() throws UsuarioNotFoundException, EspecialistaNotFoundException, ResenaException {
        Especialista especialista = especialistaService.obtenerEspecialistaAutenticado();

        List<Resena> resenas = repository.findAllByTrabajoApp_Especialista(especialista);

        if(resenas.isEmpty()) {
            throw new ResenaException("Aún no ha recibido reseñas de sus trabajos.");
        }

        return resenas;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Resena> resenasHechasPorMi() throws UsuarioNotFoundException, ResenaException {
        Usuario usuario = autorizacion.obtenerUsuarioAutenticado();

        List<Resena> resenas = repository.findAllByTrabajoApp_Usuario(usuario);

        return resenas;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void borrarResena(Long id) throws ResenaNotFoundException, UsuarioNotFoundException, ResenaException {

        Usuario usuario = autorizacion.obtenerUsuarioAutenticado();

        Resena resena = buscarPorId(id)
                .orElseThrow(() -> new ResenaNotFoundException("No se encontró una reseña con el id: " + id));

        validarCliente(resena, usuario);

        TrabajoApp trabajo = resena.getTrabajoApp();
        if (trabajo != null) {
            trabajo.setResena(null);
            trabajoService.guardarTrabajoApp(trabajo); // opcional pero recomendable
        }

        repository.delete(resena);
    }

    @Override
    public void validarCliente(Resena resena, Usuario usuario) throws ResenaException {
        if (!Objects.equals(usuario.getUsuarioId(), resena.getTrabajoApp().getUsuario().getUsuarioId())) {
            throw new ResenaException("La reseña que desea eliminar no le pertenece. Corrobore el id ingresado.");
        }
    }

    @Override
    public void validarEspecialista(TrabajoApp trabajo, Especialista especialista) throws ResenaException {
        if (!Objects.equals(especialista.getEspecialistaId(), trabajo.getEspecialista().getEspecialistaId())) {
            throw new ResenaException("El trabajo que desea buscar no le pertenece. Corrobore el titulo ingresado.");
        }
    }
}