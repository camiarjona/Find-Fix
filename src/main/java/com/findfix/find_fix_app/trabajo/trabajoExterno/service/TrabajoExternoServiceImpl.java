package com.findfix.find_fix_app.trabajo.trabajoExterno.service;

import com.findfix.find_fix_app.especialista.model.Especialista;
import com.findfix.find_fix_app.enums.EstadosTrabajos;
import com.findfix.find_fix_app.especialista.service.EspecialistaService;
import com.findfix.find_fix_app.exception.exceptions.*;
import com.findfix.find_fix_app.trabajo.trabajoExterno.dto.BuscarTrabajoExternoDTO;
import com.findfix.find_fix_app.trabajo.trabajoExterno.dto.CrearTrabajoExternoDTO;
import com.findfix.find_fix_app.trabajo.trabajoExterno.dto.ModificarTrabajoExternoDTO;
import com.findfix.find_fix_app.trabajo.trabajoExterno.model.TrabajoExterno;
import com.findfix.find_fix_app.trabajo.trabajoExterno.repository.TrabajoExternoRepository;
import com.findfix.find_fix_app.trabajo.trabajoExterno.specifications.TrabajoExternoSpecifications;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class TrabajoExternoServiceImpl implements TrabajoExternoService {

    private final TrabajoExternoRepository trabajoExternoRepository;
    private final EspecialistaService especialistaService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public TrabajoExterno crearTrabajoExterno(CrearTrabajoExternoDTO DTO) throws UserNotFoundException, EspecialistaNotFoundException {
        Especialista especialista = especialistaService.obtenerEspecialistaAutenticado();

        TrabajoExterno trabajo = new TrabajoExterno();
        trabajo.setNombreCliente(DTO.nombreCliente());
        trabajo.setDescripcion(DTO.descripcion());
        trabajo.setPresupuesto(DTO.presupuesto());
        trabajo.setEspecialista(especialista);
        trabajo.setEstado(EstadosTrabajos.CREADO);
        trabajo.setTitulo(DTO.titulo());

        return trabajoExternoRepository.save(trabajo);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TrabajoExterno> obtenerMisTrabajos() throws UserNotFoundException, EspecialistaNotFoundException, TrabajoExternoException {
        Especialista especialista = especialistaService.obtenerEspecialistaAutenticado();
        List<TrabajoExterno> misTrabajos = trabajoExternoRepository.findByEspecialista(especialista);

        if (misTrabajos.isEmpty()) {
            throw new TrabajoExternoException("Todavía no hay trabajos ingresados en su lista.");
        }

        return misTrabajos;
    }

    @Override
    public List<TrabajoExterno> filtrarTrabajosExternos(BuscarTrabajoExternoDTO filtro) throws UserNotFoundException, EspecialistaNotFoundException, TrabajoExternoException {
        Especialista especialista = especialistaService.obtenerEspecialistaAutenticado();

        Specification<TrabajoExterno> spec = (root, query, criteriaBuilder) -> criteriaBuilder.conjunction();

        if (filtro.tieneTitulo()) {
            spec = spec.and(TrabajoExternoSpecifications.tituloEs(filtro.titulo()));
        }
        if (filtro.tieneEstado()) {
            spec = spec.and(TrabajoExternoSpecifications.estadoEs(EstadosTrabajos.desdeString(filtro.estado())));
        }
        if (filtro.tieneId()) {
            spec = spec.and(TrabajoExternoSpecifications.idEs(filtro.id()));
        }
        if (filtro.tieneFecha()) {
            spec = spec.and(TrabajoExternoSpecifications.fechaEntre(filtro.desde(), filtro.hasta()));
        }

        List<TrabajoExterno> trabajosEncontrados = trabajoExternoRepository.findAll(spec)
                .stream().filter(trabajoExterno -> trabajoExterno.getEspecialista().equals(especialista))
                .toList();

        if (trabajosEncontrados.isEmpty()) {
            throw new TrabajoExternoException("\uD83D\uDE13No hay coincidencias con su búsqueda\uD83D\uDE13");
        }

        return trabajosEncontrados;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void modificarTrabajoExterno(String titulo, ModificarTrabajoExternoDTO dto) throws TrabajoExternoNotFoundException, UserNotFoundException, EspecialistaNotFoundException, TrabajoExternoException {
        TrabajoExterno trabajo = trabajoExternoRepository.findByTitulo(titulo)
                .orElseThrow(() -> new TrabajoExternoNotFoundException("Trabajo externo no encontrado."));

        Especialista especialista = especialistaService.obtenerEspecialistaAutenticado();

        //validamos que el trabajo corresponda con el especialista en cuestión
        validarEspecialista(trabajo, especialista);

        if (dto.tieneDescripcion()) {
            trabajo.setDescripcion(dto.descripcion());
        }
        if (dto.tienePresupuesto()) {
            trabajo.setPresupuesto(dto.presupuesto());
        }
        if (dto.tieneNombreCliente()) {
            trabajo.setNombreCliente(dto.nombreCliente());
        }
        if (dto.tieneTitulo()) {
            trabajo.setTitulo(dto.titulo());
        }

        trabajoExternoRepository.save(trabajo);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void actualizarEstado(String titulo, String estadoNuevo) throws UserNotFoundException, EspecialistaNotFoundException, TrabajoAppNotFoundException, TrabajoExternoException {

        TrabajoExterno trabajo = trabajoExternoRepository.findByTitulo(titulo)
                .orElseThrow(() -> new TrabajoAppNotFoundException("Trabajo externo no encontrado."));

        Especialista especialista = especialistaService.obtenerEspecialistaAutenticado();

        //validamos el especialista
        validarEspecialista(trabajo, especialista);

        if (estadoNuevo != null && !estadoNuevo.isEmpty()) {

            //validamos los estados
            validarEstado(trabajo, estadoNuevo);

            EstadosTrabajos estado = EstadosTrabajos.desdeString(estadoNuevo);
            trabajo.setEstado(estado);

            //si el estado nuevo es en proceso, se le asigna fecha de inicio, y si es finalizado se le asigna fecha de fin
            if (estado.equals(EstadosTrabajos.EN_PROCESO)) {
                trabajo.setFechaInicio(LocalDate.now());
            } else if (estado.equals(EstadosTrabajos.FINALIZADO)) {
                trabajo.setFechaFin(LocalDate.now());
            }
        }
        trabajoExternoRepository.save(trabajo);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void borrarTrabajoExternoPorTitulo(String titulo) throws UserNotFoundException, EspecialistaNotFoundException, TrabajoExternoException {
        TrabajoExterno trabajo = trabajoExternoRepository.findByTitulo(titulo)
                .orElseThrow(() -> new EntityNotFoundException("Trabajo externo no encontrado."));

        Especialista especialista = especialistaService.obtenerEspecialistaAutenticado();

        validarEspecialista(trabajo, especialista);

        trabajoExternoRepository.delete(trabajo);
    }

    // metodo para validar que el trabajo corresponda al especialista
    @Override
    public void validarEspecialista(TrabajoExterno trabajoExterno, Especialista especialista) throws TrabajoExternoException {
        if (!Objects.equals(especialista.getEspecialistaId(), trabajoExterno.getEspecialista().getEspecialistaId())) {
            throw new TrabajoExternoException("El trabajo que desea modificar no le pertenece. Corrobore el id ingresado.");
        }
    }

    //metodo para validar el estado del trabajo al momento de modificar
    @Override
    public void validarEstado(TrabajoExterno trabajo, String estado) throws TrabajoExternoException {
        if (trabajo.getEstado().equals(EstadosTrabajos.FINALIZADO)) {
            throw new TrabajoExternoException("El trabajo ya se encuentra finalizado, no es posible modificar su estado.");
        }
        if (trabajo.getEstado().equals(EstadosTrabajos.desdeString(estado))) {
            throw new TrabajoExternoException("El trabajo ya se encuentra en el estado especificado.");
        }
        if (trabajo.getEstado().equals(EstadosTrabajos.CREADO) && !EstadosTrabajos.desdeString(estado).equals(EstadosTrabajos.EN_PROCESO)) {
            throw new TrabajoExternoException("El trabajo debe primero ser puesto en proceso");
        }
        if (trabajo.getEstado().equals(EstadosTrabajos.EN_PROCESO) && EstadosTrabajos.desdeString(estado).equals(EstadosTrabajos.CREADO)) {
            throw new TrabajoExternoException("El trabajo ya se encuentra en proceso, no es posible modificar su estado a creado.");
        }
        if (trabajo.getEstado().equals(EstadosTrabajos.EN_REVISION) && EstadosTrabajos.desdeString(estado).equals(EstadosTrabajos.CREADO)) {
            throw new TrabajoExternoException("El trabajo ya se encuentra en revision, no es posible modificar su estado a creado.");
        }
    }

}