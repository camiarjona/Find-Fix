package com.findfix.find_fix_app.solicitudTrabajo.service;

import com.findfix.find_fix_app.utils.auth.AuthService;
import com.findfix.find_fix_app.utils.enums.EstadosSolicitudes;
import com.findfix.find_fix_app.especialista.model.Especialista;
import com.findfix.find_fix_app.especialista.service.EspecialistaService;
import com.findfix.find_fix_app.utils.exception.exceptions.SolicitudTrabajoException;
import com.findfix.find_fix_app.utils.exception.exceptions.SolicitudTrabajoNotFoundException;
import com.findfix.find_fix_app.utils.exception.exceptions.EspecialistaNotFoundException;
import com.findfix.find_fix_app.utils.exception.exceptions.UsuarioNotFoundException;
import com.findfix.find_fix_app.solicitudTrabajo.dto.ActualizarEstadoDTO;
import com.findfix.find_fix_app.solicitudTrabajo.dto.BuscarSolicitudDTO;
import com.findfix.find_fix_app.solicitudTrabajo.dto.SolicitarTrabajoDTO;
import com.findfix.find_fix_app.solicitudTrabajo.model.SolicitudTrabajo;
import com.findfix.find_fix_app.solicitudTrabajo.repository.SolicitudTrabajoRepository;
import com.findfix.find_fix_app.solicitudTrabajo.specifications.SolicitudTrabajoSpecifications;
import com.findfix.find_fix_app.trabajo.trabajoApp.service.TrabajoAppService;
import com.findfix.find_fix_app.usuario.model.Usuario;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SolicitudTrabajoServiceImpl implements SolicitudTrabajoService {

    private final SolicitudTrabajoRepository solicitudTrabajoRepository;
    private final EspecialistaService especialistaService;
    private final AuthService authService;
    private final TrabajoAppService trabajoAppService;

    //metodo para registrar una nueva solicitud de trabajo
    @Override
    @Transactional(rollbackFor = Exception.class)
    public SolicitudTrabajo registrarNuevaSolicitud(SolicitarTrabajoDTO solicitarTrabajoDTO) throws UsuarioNotFoundException, EspecialistaNotFoundException {
        SolicitudTrabajo solicitudTrabajo = new SolicitudTrabajo();

        Especialista especialista = especialistaService.buscarPorEmail(solicitarTrabajoDTO.emailEspecialista())
                .orElseThrow(() -> new EspecialistaNotFoundException("Especialista no encontrado."));

        solicitudTrabajo.setDescripcion(solicitarTrabajoDTO.descripcion());
        solicitudTrabajo.setEstado(EstadosSolicitudes.PENDIENTE);
        solicitudTrabajo.setUsuario(authService.obtenerUsuarioAutenticado());
        solicitudTrabajo.setFechaCreacion(LocalDate.now());
        solicitudTrabajo.setEspecialista(especialista);
        return solicitudTrabajoRepository.save(solicitudTrabajo);
    }

    //metodo para actualizar el estado de una solicitud (especialista)
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void actualizarEstadoSolicitud(ActualizarEstadoDTO actualizar, Long idSolicitud) throws SolicitudTrabajoNotFoundException, UsuarioNotFoundException, EspecialistaNotFoundException, SolicitudTrabajoException {

        Especialista especialista = especialistaService.obtenerEspecialistaAutenticado();

        SolicitudTrabajo solicitudTrabajo = solicitudTrabajoRepository.findById(idSolicitud)
                .orElseThrow(() -> new SolicitudTrabajoNotFoundException("Solicitud de trabajo no encontrada"));

        //validamos que la solicitud a actualizar corresponda al especialista
        validarEspecialista(solicitudTrabajo, especialista);

        //validamos el estado de la solicitud ingresada
        validarEstado(solicitudTrabajo);

        if (actualizar.tieneEstado()) {
            EstadosSolicitudes nuevoEstado = EstadosSolicitudes.desdeString(actualizar.estado());
            solicitudTrabajo.setEstado(nuevoEstado);
            solicitudTrabajo.setFechaResolucion(LocalDate.now());
            solicitudTrabajoRepository.save(solicitudTrabajo);

            // si la solicitud es aceptada, pasa a ser un trabajo en la app
            if (nuevoEstado == EstadosSolicitudes.ACEPTADO) {
                trabajoAppService.registrarDesdeSolicitud(solicitudTrabajo, especialista);
            }
        }
    }

    // metodo para obtener las solicitudes enviadas (como cliente)
    @Override
    @Transactional(readOnly = true)
    public List<SolicitudTrabajo> obtenerSolicitudesDelCliente() throws UsuarioNotFoundException, SolicitudTrabajoException {
        Usuario usuario = authService.obtenerUsuarioAutenticado();

        List<SolicitudTrabajo> solicitudesEnviadas = solicitudTrabajoRepository.findByUsuario(usuario);

        if (solicitudesEnviadas.isEmpty()) {
            throw new SolicitudTrabajoException("Usted no ha enviado ninguna solicitud.");
        }

        return solicitudesEnviadas;
    }

    // metodo para obtener las solicitudes recibidas (como especialista)
    @Override
    @Transactional(readOnly = true)
    public List<SolicitudTrabajo> obtenerSolicitudesDelEspecialista() throws UsuarioNotFoundException, EspecialistaNotFoundException, SolicitudTrabajoException {
        Especialista especialista = especialistaService.obtenerEspecialistaAutenticado();

        return solicitudTrabajoRepository.findByEspecialista(especialista);
    }

    // metodo para eliminar una solicitud de trabajo si aún está pendiente (cancelar trabajo desde cliente)
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void eliminarSolicitud(Long idSolicitud) throws SolicitudTrabajoNotFoundException, UsuarioNotFoundException, SolicitudTrabajoException {
        SolicitudTrabajo solicitudTrabajo = solicitudTrabajoRepository.findById(idSolicitud)
                .orElseThrow(() -> new SolicitudTrabajoNotFoundException("Solicitud de trabajo no encontrada"));

        Usuario usuario = authService.obtenerUsuarioAutenticado();

        if (estaPendiente(solicitudTrabajo)) {
            validarCliente(solicitudTrabajo, usuario);
            solicitudTrabajoRepository.delete(solicitudTrabajo);
        }
    }

    // metodo para obtener una solicitud por id
    @Override
    @Transactional(readOnly = true)
    public Optional<SolicitudTrabajo> buscarPorId(Long id) {
        return solicitudTrabajoRepository.findById(id);
    }

    // metodo para filtrar solicitudes según criterios
    @Override
    @Transactional(readOnly = true)
    public List<SolicitudTrabajo> filtrarSolicitudesRecibidas(BuscarSolicitudDTO filtro) throws SolicitudTrabajoException, UsuarioNotFoundException, EspecialistaNotFoundException {

        Especialista especialista = especialistaService.obtenerEspecialistaAutenticado();

        Specification<SolicitudTrabajo> spec = (root, query, criteriaBuilder) -> criteriaBuilder.conjunction();

        if (filtro.tieneFecha()) {
            spec = spec.and(SolicitudTrabajoSpecifications.fechaEntre(filtro.desde(), filtro.hasta()));
        }
        if (filtro.tieneEstado()) {
            spec = spec.and(SolicitudTrabajoSpecifications.estadoEs(EstadosSolicitudes.desdeString(filtro.estado())));
        }

        List<SolicitudTrabajo> solicitudesEncontradas = solicitudTrabajoRepository.findAll(spec).stream()
                .filter(s -> s.getEspecialista().equals(especialista))
                .collect(Collectors.toList());

        if (solicitudesEncontradas.isEmpty()) {
            throw new SolicitudTrabajoException("\uD83D\uDE13No hay coincidencias con su búsqueda\uD83D\uDE13");
        }
        return solicitudesEncontradas;
    }

    @Override
    @Transactional(readOnly = true)
    public List<SolicitudTrabajo> filtrarSolicitudesEnviadas(BuscarSolicitudDTO filtro) throws SolicitudTrabajoException, UsuarioNotFoundException {

        Usuario usuario = authService.obtenerUsuarioAutenticado();

        Specification<SolicitudTrabajo> spec = (root, query, criteriaBuilder) -> criteriaBuilder.conjunction();

        if (filtro.tieneFecha()) {
            spec = spec.and(SolicitudTrabajoSpecifications.fechaEntre(filtro.desde(), filtro.hasta()));
        }
        if (filtro.tieneEstado()) {
            spec = spec.and(SolicitudTrabajoSpecifications.estadoEs(EstadosSolicitudes.desdeString(filtro.estado())));
        }
        if (filtro.tieneEmail()) {
            spec = spec.and(SolicitudTrabajoSpecifications.tieneEmail(filtro.emailEspecialista()));
        }

        List<SolicitudTrabajo> solicitudesEncontradas = solicitudTrabajoRepository.findAll(spec).stream()
                .filter(s -> s.getUsuario().equals(usuario))
                .collect(Collectors.toList());

        if (solicitudesEncontradas.isEmpty()) {
            throw new SolicitudTrabajoException("\uD83D\uDE13No hay coincidencias con su búsqueda\uD83D\uDE13");
        }
        return solicitudesEncontradas;
    }

    // metodo para validar el estado de una solicitud (para actualizar)
    @Override
    public void validarEstado(SolicitudTrabajo solicitudTrabajo) throws SolicitudTrabajoException {
        if (solicitudTrabajo.getEstado().equals(EstadosSolicitudes.ACEPTADO)) {
            throw new SolicitudTrabajoException("Usted ya ha aceptado esta solicitud. No puede modificar su estado.");
        }
        if (solicitudTrabajo.getEstado().equals(EstadosSolicitudes.RECHAZADO)) {
            throw new SolicitudTrabajoException("Usted ya ha rechazado esta solicitud. No puede modificar su estado.");
        }
    }

    // metodo para validar que la solicitud corresponda al especialista
    @Override
    public void validarEspecialista(SolicitudTrabajo solicitudTrabajo, Especialista especialista) throws SolicitudTrabajoException {
        if (!Objects.equals(especialista.getEspecialistaId(), solicitudTrabajo.getEspecialista().getEspecialistaId())) {
            throw new SolicitudTrabajoException("La solicitud que desea modificar no le pertenece. Corrobore el id ingresado.");
        }
    }

    // metodo para validar que la solicitud corresponda al cliente
    @Override
    public void validarCliente(SolicitudTrabajo solicitudTrabajo, Usuario usuario) throws SolicitudTrabajoException {
        if (!Objects.equals(usuario.getUsuarioId(), solicitudTrabajo.getUsuario().getUsuarioId())) {
            throw new SolicitudTrabajoException("La solicitud que desea eliminar no le pertenece. Corrobore el id ingresado.");
        }
    }

    // metodo para verificar si la solicitud sigue pendiente (para eliminar)
    @Override
    public boolean estaPendiente(SolicitudTrabajo solicitudTrabajo) {
        return solicitudTrabajo.getEstado().equals(EstadosSolicitudes.PENDIENTE);
    }
}
