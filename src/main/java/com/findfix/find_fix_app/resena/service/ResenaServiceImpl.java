package com.findfix.find_fix_app.resena.service;

import com.findfix.find_fix_app.auth.service.AuthService;
import com.findfix.find_fix_app.especialista.model.Especialista;
import com.findfix.find_fix_app.especialista.service.EspecialistaService;
import com.findfix.find_fix_app.especialista.service.EspecialistaServiceImpl;
import com.findfix.find_fix_app.exception.exceptions.ResenaNotFoundException;
import com.findfix.find_fix_app.exception.exceptions.SpecialistRequestNotFoundException;
import com.findfix.find_fix_app.exception.exceptions.TrabajoAppNotFoundException;
import com.findfix.find_fix_app.exception.exceptions.UserNotFoundException;
import com.findfix.find_fix_app.resena.dto.CrearResenaDTO;
import com.findfix.find_fix_app.resena.dto.MostrarResenaDTO;
import com.findfix.find_fix_app.resena.model.Resena;
import com.findfix.find_fix_app.resena.repository.ResenaRepository;
import com.findfix.find_fix_app.trabajo.trabajoApp.model.TrabajoApp;
import com.findfix.find_fix_app.trabajo.trabajoApp.service.TrabajoAppService;
import com.findfix.find_fix_app.trabajo.trabajoApp.service.TrabajoAppServiceImpl;
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
    @Transactional
    public Resena crearResena(CrearResenaDTO dto) throws TrabajoAppNotFoundException {
        Optional<TrabajoApp> trabajo = trabajoService.buscarPorId(dto.getTrabajoId());

        if(trabajo.isEmpty()){
            throw new TrabajoAppNotFoundException("\n Trabajo de la app no encontrado. ");
        }

        Resena resena = new Resena();
        resena.setComentario(dto.getComentario());
        resena.setPuntuacion(dto.getPuntuacion());
        resena.setTrabajoApp(trabajo.get());

        return repository.save(resena);
    }

    @Override
    @Transactional(readOnly = true)
    public List<MostrarResenaDTO> buscarTodos() {
        List<Resena> resenas = repository.findAll();

        return resenas.stream().map(resena -> {
            TrabajoApp trabajo = resena.getTrabajoApp();

            return new MostrarResenaDTO(
                    resena.getPuntuacion(),
                    resena.getComentario(),
                    trabajo.getFechaInicio(),
                    trabajo.getFechaFin(),
                    trabajo.getEstado(),
                    trabajo.getDescripcion(),
                    trabajo.getPresupuesto()
            );
        }).toList();
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
        TrabajoApp trabajo = trabajoService.buscarPorTitulo(titulo).orElseThrow(() -> new TrabajoAppNotFoundException("\n nio encotnrado"));

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

        List<Resena> resenas = repository.findAllByTrabajoApp_Cliente(usuario);

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
        Resena resena = repository.findById(id)
                .orElseThrow(() -> new ResenaNotFoundException("No se encontró una reseña para el trabajo con id:  " + id));

        repository.delete(resena);
    }
}