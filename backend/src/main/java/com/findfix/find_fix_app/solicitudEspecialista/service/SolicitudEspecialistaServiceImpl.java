package com.findfix.find_fix_app.solicitudEspecialista.service;

import com.findfix.find_fix_app.solicitudEspecialista.dto.*;
import com.findfix.find_fix_app.utils.auth.service.AuthServiceImpl;
import com.findfix.find_fix_app.utils.enums.EstadosSolicitudes;
import com.findfix.find_fix_app.especialista.service.EspecialistaService;
import com.findfix.find_fix_app.notificacion.service.NotificacionService;
import com.findfix.find_fix_app.utils.exception.exceptions.RolNotFoundException;
import com.findfix.find_fix_app.utils.exception.exceptions.SolicitudEspecialistaException;
import com.findfix.find_fix_app.utils.exception.exceptions.SolicitudEspecialistaNotFoundException;
import com.findfix.find_fix_app.utils.exception.exceptions.UsuarioNotFoundException;
import com.findfix.find_fix_app.solicitudEspecialista.Specifications.SolicitudEspecialistaSpecifications;
import com.findfix.find_fix_app.solicitudEspecialista.model.SolicitudEspecialista;
import com.findfix.find_fix_app.solicitudEspecialista.repository.SolicitudEspecialistaRepository;
import com.findfix.find_fix_app.usuario.model.Usuario;
import com.findfix.find_fix_app.usuario.repository.UsuarioRepository;
import com.findfix.find_fix_app.usuario.service.UsuarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@RequiredArgsConstructor
@Service
public class SolicitudEspecialistaServiceImpl implements SolicitudEspecialistaService {
    private final AuthServiceImpl authServiceImpl;
    private final SolicitudEspecialistaRepository solicitudEspecialistaRepository;
    private final EspecialistaService especialistaService;
    private final UsuarioService usuarioService;
    private final NotificacionService notificationService; 
    private final UsuarioRepository usuarioRepository;

    /// Metodo para que el usuario mande una solicitud para ser especialista
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void mandarSolicitud(MandarSolicitudEspecialistaDTO dto) throws UsuarioNotFoundException, SolicitudEspecialistaException {
        SolicitudEspecialista solicitudEspecialista = new SolicitudEspecialista();

        Usuario usuario = authServiceImpl.obtenerUsuarioAutenticado();

        verificarUsuario(usuario);
      
        solicitudEspecialista.setUsuario(usuario);
        solicitudEspecialista.setFechaSolicitud(LocalDate.now());
        solicitudEspecialista.setMotivo(dto.motivo());
        solicitudEspecialista.setEstado(EstadosSolicitudes.PENDIENTE);

        solicitudEspecialistaRepository.save(solicitudEspecialista);
        Usuario admin = usuarioRepository.findByEmail("findfixapp.utn@gmail.com") 
                .orElseThrow(() -> new UsuarioNotFoundException("No se encontró al admin para notificar"));
        notificationService.notificarAdminNuevaSolicitudEspecialista(admin, solicitudEspecialista.getUsuario().getNombre());
        notificationService.notificarConfirmacionSolicitudEspecialistaEnviada(solicitudEspecialista.getUsuario());
    }

    /// Metodo para controlar y verificar la cantidad de solicitudes en un estado especifico de un usuario especifico
    private Long contarSolicitudesPorUsuarioYEstado(Usuario usuario, EstadosSolicitudes estado) {
        return solicitudEspecialistaRepository.findAll().stream()
                .filter(s -> s.getUsuario().equals(usuario) && s.getEstado().equals(estado))
                .count();
    }

    /// Metodo para verificar si un usuario puede hacer una solicitud nueva para ser especialista
    private void verificarUsuario(Usuario usuario) throws SolicitudEspecialistaException {

        if (usuario.getRoles().stream().anyMatch(rol -> rol.getNombre().equals("ESPECIALISTA"))) {
            throw new SolicitudEspecialistaException("⚠️Usted ya se encuentra como especialista en nuestro sistema.");
        }

        Long rechazados = contarSolicitudesPorUsuarioYEstado(usuario, EstadosSolicitudes.RECHAZADO);
        if (rechazados >= 5) {
            throw new SolicitudEspecialistaException("⚠️Usted ya tiene 5 solicitudes rechazadas.");
        }

        Long pendientes = contarSolicitudesPorUsuarioYEstado(usuario, EstadosSolicitudes.PENDIENTE);
        if (pendientes >= 1) {
            throw new SolicitudEspecialistaException("⚠️Usted ya tiene una solicitud pendiente.");
        }
    }

    /// Metodo para mostrar todas las solicitudes de especialistas, para el admin.
    @Override
    @Transactional(readOnly = true)
    public List<MostrarSolicitudEspecialistaAdminDTO> obtenerSolicitudesEspecialista() throws SolicitudEspecialistaNotFoundException {
        List<SolicitudEspecialista> solicitudesEspecialistas = solicitudEspecialistaRepository.findAll();

        if (solicitudesEspecialistas.isEmpty()) {
            throw new SolicitudEspecialistaNotFoundException("⚠️No se encontraron solicitudes al momento.");
        }

        return solicitudesEspecialistas.stream()
                .map(solicitud -> new MostrarSolicitudEspecialistaAdminDTO(
                        solicitud.getSeId(),
                        solicitud.getFechaSolicitud(),
                        solicitud.getEstado().name(),
                        solicitud.getUsuario() != null ? solicitud.getUsuario().getEmail() : "Usuario desvinculado"
                )).toList();
    }


    /// Metodo para mostrar mis solicitudes
    @Override
    @Transactional(readOnly = true)
    public List<SolicitudEspecialista> obtenerMisSolicitudesEspecialista() throws SolicitudEspecialistaException, UsuarioNotFoundException {
        Usuario usuario = authServiceImpl.obtenerUsuarioAutenticado();
        List<SolicitudEspecialista> solicitudEspecialistas = solicitudEspecialistaRepository.findByUsuarioEmail(usuario.getEmail());
        if (solicitudEspecialistas.isEmpty()) {
            throw new SolicitudEspecialistaException("⚠️No se encontraron solicitudes para el usuario con email: " + usuario.getEmail());
        }
        return solicitudEspecialistas;
    }

    /// Metodo para actualizar el estado y la respuesta de una solicitud, por parte del admin
    private void actualizarDatosSolicitud(SolicitudEspecialista solicitudEspecialista, ActualizarSolicitudEspecialistaDTO dto) throws RolNotFoundException, SolicitudEspecialistaException {

        if (!solicitudEspecialista.getEstado().equals(EstadosSolicitudes.PENDIENTE)) {
            throw new SolicitudEspecialistaException("⚠️Solo puede modificar una solicitud en estado pendiente.");

        }

        if(dto.tieneEstado()) {
            EstadosSolicitudes nuevoEstado = EstadosSolicitudes.desdeString(dto.estado());
            solicitudEspecialista.setEstado(nuevoEstado);
            solicitudEspecialista.setRespuesta(dto.respuesta());

            if(nuevoEstado == EstadosSolicitudes.ACEPTADO) {
                String respuesta = dto.respuesta() + " ‼️Atención: para aparecer en las búsquedas de clientes," +
                        " debe completar su perfil de especialista (ciudad, teléfono y al menos un oficio).";

                solicitudEspecialista.setRespuesta(respuesta);
                usuarioService.agregarRol(solicitudEspecialista.getUsuario(), "ESPECIALISTA");
                especialistaService.guardar(solicitudEspecialista.getUsuario());
            }
        }
        solicitudEspecialistaRepository.save(solicitudEspecialista);
    }

    /// Metodo que actualiza llamando al de actualizar los datos
    @Override
    @Transactional(rollbackFor = Exception.class)
    public SolicitudEspecialista actualizarSolicitudEspecialistaAdmin(ActualizarSolicitudEspecialistaDTO dto, Long id) throws SolicitudEspecialistaNotFoundException, RolNotFoundException, SolicitudEspecialistaException {
        SolicitudEspecialista solicitudEspecialista = solicitudEspecialistaRepository.findById(id)
                .orElseThrow(() -> new SolicitudEspecialistaNotFoundException("⚠️Solicitud no encontrada"));

        solicitudEspecialista.setFechaResolucion(LocalDate.now());
        actualizarDatosSolicitud(solicitudEspecialista, dto);
        return solicitudEspecialista;
    }

    /// Metodo para eliminar una solicitud pendiente propia de parte del usuario. Solo si esta pendiente

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void eliminarPorId(Long id) throws SolicitudEspecialistaNotFoundException, SolicitudEspecialistaException {
        String email = authServiceImpl.obtenerEmailUsuarioAutenticado();

        if (solicitudEspecialistaRepository.findById(id).isEmpty()) {
            throw new SolicitudEspecialistaNotFoundException("⚠️Solicitud no encontrada");
        }
        if (!email.equals(solicitudEspecialistaRepository.findById(id).get().getUsuario().getEmail())) {
            throw new SolicitudEspecialistaException("⚠️Solicitud no encontrada para el usuario: " + email);
        }

        if (!solicitudEspecialistaRepository.findById(id).get().getEstado().name().equals("PENDIENTE")) {
            throw new SolicitudEspecialistaException("⚠️Solicitud no se puede eliminar porque ya ha sido aceptada o rechazada.");
        }

        solicitudEspecialistaRepository.deleteById(id);
    }

    /// Metodo para filtrar solicitudes
    @Override
    @Transactional(readOnly = true)
    public List<FichaCompletaSolicitudEspecialistaDTO> filtrarSolicitudes(BuscarSolicitudEspecialistaDTO filtro) throws SolicitudEspecialistaException, UsuarioNotFoundException {

        Usuario usuarioAuth = authServiceImpl.obtenerUsuarioAutenticado();

        boolean esAdmin = usuarioAuth.getRoles().stream()
                .anyMatch(rol -> rol.getNombre().equals("ADMIN"));

        Specification<SolicitudEspecialista> spec = (root, query, cb) -> cb.conjunction();

        if (!esAdmin) {
            spec = spec.and(SolicitudEspecialistaSpecifications.tieneUsuarioEmail(usuarioAuth.getEmail()));
        }


        // Filtro por ID
        if (filtro.tieneId()) {
            spec = spec.and(SolicitudEspecialistaSpecifications.tieneId(filtro.id()));
        }

        // Filtro por Estado
        if (filtro.tieneEstado()) {
            try {
                EstadosSolicitudes estadoEnum = EstadosSolicitudes.valueOf(filtro.estado().toUpperCase());
                spec = spec.and(SolicitudEspecialistaSpecifications.tieneEstado(estadoEnum));
            } catch (IllegalArgumentException e) {
                throw new SolicitudEspecialistaException("⚠️El estado ingresado no es válido.");
            }
        }

        // Filtro por Fecha
        if (filtro.tieneFecha()) {
            spec = spec.and(SolicitudEspecialistaSpecifications.fechaEntre(filtro.fechaDesde(), filtro.fechaHasta()));
        }

        if (esAdmin && filtro.tieneEmail()) {
            spec = spec.and(SolicitudEspecialistaSpecifications.tieneUsuarioEmail(filtro.email()));
        }

        List<SolicitudEspecialista> solicitudesEncontradas = solicitudEspecialistaRepository.findAll(spec);

        if (solicitudesEncontradas.isEmpty()) {
            throw new SolicitudEspecialistaException("⚠️No hay coincidencias con su búsqueda.");
        }

        return solicitudesEncontradas.stream()
                .map(FichaCompletaSolicitudEspecialistaDTO::new)
                .toList();
    }



    @Override
    @Transactional(readOnly = true)
    public FichaCompletaSolicitudEspecialistaDTO obtenerFichaPorId(Long id)
            throws SolicitudEspecialistaNotFoundException, UsuarioNotFoundException, SolicitudEspecialistaException {

        SolicitudEspecialista solicitud = solicitudEspecialistaRepository.findById(id)
                .orElseThrow(() -> new SolicitudEspecialistaNotFoundException("No se encontró una solicitud con el ID: " + id));

        Usuario usuarioAutenticado = authServiceImpl.obtenerUsuarioAutenticado();

        boolean esAdmin = usuarioAutenticado.getRoles().stream()
                .anyMatch(rol -> rol.getNombre().equals("ADMIN"));

        boolean esPropietario = solicitud.getUsuario().equals(usuarioAutenticado);

        if (!esAdmin && !esPropietario) {
            throw new SolicitudEspecialistaException("No tiene permisos para ver esta solicitud.");
        }

        return new FichaCompletaSolicitudEspecialistaDTO(solicitud);
    }
}
