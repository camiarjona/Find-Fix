package com.findfix.find_fix_app.solicitudEspecialista.service;

import com.findfix.find_fix_app.auth.service.AuthService;
import com.findfix.find_fix_app.enums.EstadosSolicitudes;
import com.findfix.find_fix_app.especialista.service.EspecialistaService;
import com.findfix.find_fix_app.exception.exceptions.RolNotFoundException;
import com.findfix.find_fix_app.exception.exceptions.SolicitudEspecialistaException;
import com.findfix.find_fix_app.exception.exceptions.SolicitudEspecialistaNotFoundException;
import com.findfix.find_fix_app.exception.exceptions.UserNotFoundException;
import com.findfix.find_fix_app.solicitudEspecialista.Specifications.SolicitudEspecialistaSpecifications;
import com.findfix.find_fix_app.solicitudEspecialista.dto.ActualizarSolicitudEspecialistaDTO;
import com.findfix.find_fix_app.solicitudEspecialista.dto.BuscarSolicitudEspecialistaDTO;
import com.findfix.find_fix_app.solicitudEspecialista.dto.MandarSolicitudEspecialistaDTO;
import com.findfix.find_fix_app.solicitudEspecialista.dto.FichaCompletaSolicitudEspecialistaDTO;
import com.findfix.find_fix_app.solicitudEspecialista.model.SolicitudEspecialista;
import com.findfix.find_fix_app.solicitudEspecialista.repository.SolicitudEspecialistaRepository;
import com.findfix.find_fix_app.usuario.model.Usuario;
import com.findfix.find_fix_app.usuario.service.UsuarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

@RequiredArgsConstructor
@Service
public class SolicitudEspecialistaServiceImpl implements SolicitudEspecialistaService{
    private final AuthService authService;
    private final SolicitudEspecialistaRepository solicitudEspecialistaRepository;
    private final EspecialistaService especialistaService;
    private final UsuarioService usuarioService;


    /// Metodo para que el usuario mande una solicitud para ser especialista
    @Override
    public SolicitudEspecialista mandarSolicitud (MandarSolicitudEspecialistaDTO dto) throws UserNotFoundException, SolicitudEspecialistaException {
        SolicitudEspecialista solicitudEspecialista = new SolicitudEspecialista();

        Usuario usuario = authService.obtenerUsuarioAutenticado();

        verificarUsuario(usuario);

        solicitudEspecialista.setUsuario(usuario);
        solicitudEspecialista.setFechaSolicitud(LocalDate.now());
        solicitudEspecialista.setMotivo(dto.motivo());
        solicitudEspecialista.setEstado(EstadosSolicitudes.PENDIENTE);

        return solicitudEspecialistaRepository.save(solicitudEspecialista);
    }

    /// Metodo para controlar y verificar la cantidad de solicitudes en un estado especifico de un usuario especifico

    private Long contarSolicitudesPorUsuarioYEstado(Usuario usuario, EstadosSolicitudes estado) {
        return solicitudEspecialistaRepository.findAll().stream()
                .filter(s -> s.getUsuario().equals(usuario) && s.getEstado().equals(estado))
                .count();
    }

    /// Metodo para verificar si un usuario puede hacer una solicitud nueva para ser especialista
    private void verificarUsuario(Usuario usuario) throws UserNotFoundException, SolicitudEspecialistaException {

        if(usuario.getRoles().stream().anyMatch(rol -> rol.getNombre().equals("ESPECIALISTA"))){
            throw new SolicitudEspecialistaException("Usted ya se encuentra como especialista en nuestro sistema.");
        }

        Long rechazados = contarSolicitudesPorUsuarioYEstado(usuario, EstadosSolicitudes.RECHAZADO);
        if (rechazados >= 5) {
            throw new SolicitudEspecialistaException("Usted ya tiene 5 solicitudes rechazadas.");
        }

        Long pendientes = contarSolicitudesPorUsuarioYEstado(usuario, EstadosSolicitudes.PENDIENTE);
        if (pendientes >= 1) {
            throw new SolicitudEspecialistaException("Usted ya tiene una solicitud pendiente.");
        }
    }

    /// Metodo para mostrar todas las solicitudes de especialistas, para el admin.
    @Override
    public List<SolicitudEspecialista> obtenerSolicitudesEspecialista() throws SolicitudEspecialistaNotFoundException {
        List<SolicitudEspecialista> solicitudesEspecialistas = solicitudEspecialistaRepository.findAll();

        if(solicitudesEspecialistas.isEmpty()){
            throw new SolicitudEspecialistaNotFoundException("No se encontraron solicitudes al momento.");
        }

        return solicitudesEspecialistas;
    }


    /// Metodo para mostrar mis solicitudes

    @Override
    public List<SolicitudEspecialista> obtenerMisSolicitudesEspecialista() throws SolicitudEspecialistaException, SolicitudEspecialistaNotFoundException, UserNotFoundException {
        Usuario usuario = authService.obtenerUsuarioAutenticado();
        List<SolicitudEspecialista>solicitudEspecialistas = solicitudEspecialistaRepository.findByUsuarioEmail(usuario.getEmail());
        if (solicitudEspecialistas.isEmpty()) {
            throw new SolicitudEspecialistaException("No se encontraron solicitudes para el usuario con email: " + usuario.getEmail());
        }

        return solicitudEspecialistas;
    }

    /// Metodo para actualizar el estado y la respuesta de una solicitud, por parte del admin
    private void actualizarDatosSolicitud(SolicitudEspecialista solicitudEspecialista, ActualizarSolicitudEspecialistaDTO dto) throws UserNotFoundException, RolNotFoundException, SolicitudEspecialistaException {
        EstadosSolicitudes estadosSolicitudes = EstadosSolicitudes.valueOf(dto.estado().trim().toUpperCase() );
        if(solicitudEspecialista.getEstado().name().equals("PENDIENTE")) {

            if (estadosSolicitudes.name().equals("ACEPTADO")) {
                solicitudEspecialista.setEstado(EstadosSolicitudes.ACEPTADO);
                String respuesta = dto.respuesta() + " Atención: para aparecer en las búsquedas de clientes, debe completar su perfil de especialista (ciudad, teléfono y al menos un oficio).";
                solicitudEspecialista.setRespuesta(respuesta);
                usuarioService.agregarRol(solicitudEspecialista.getUsuario(), "ESPECIALISTA");
                especialistaService.guardar(solicitudEspecialista.getUsuario());

            }

            if (estadosSolicitudes.name().equals("RECHAZADO")) {
                solicitudEspecialista.setEstado(EstadosSolicitudes.RECHAZADO);
                solicitudEspecialista.setRespuesta(dto.respuesta());
            }

            solicitudEspecialistaRepository.save(solicitudEspecialista);
        }else{
            throw new SolicitudEspecialistaException("Solo puede modificar una solicitud en estado pendiente.");
        }
    }

    /// Metodo que actualiza llamando al de actualizar los datos

    @Override
    public SolicitudEspecialista actualizarSolicitudEspecialistaAdmin(ActualizarSolicitudEspecialistaDTO dto, Long id) throws SolicitudEspecialistaNotFoundException, UserNotFoundException, RolNotFoundException, SolicitudEspecialistaException {
        SolicitudEspecialista solicitudEspecialista = solicitudEspecialistaRepository.findById(id)
                .orElseThrow(()-> new SolicitudEspecialistaNotFoundException("Solicitud no encontrada"));

        solicitudEspecialista.setFechaResolucion(LocalDate.now());
        actualizarDatosSolicitud(solicitudEspecialista, dto);
        return solicitudEspecialista;
    }

    /// Metodo para eliminar una solicitud pendiente propia de parte del usuario. Solo si esta pendiente

    @Override
    public void eliminarPorId(Long id) throws SolicitudEspecialistaNotFoundException, SolicitudEspecialistaException {
        String email = authService.obtenerEmailUsuarioAutenticado();

        if(solicitudEspecialistaRepository.findById(id).isEmpty()){
            throw new SolicitudEspecialistaNotFoundException("Solicitud no encontrada");
        }
        if(!email.equals(solicitudEspecialistaRepository.findById(id).get().getUsuario().getEmail())){
            throw new SolicitudEspecialistaException("Solicitud no encontrada para el usuario: " + email);
        }

        if(!solicitudEspecialistaRepository.findById(id).get().getEstado().name().equals("PENDIENTE")){
            throw new SolicitudEspecialistaException("Solicitud no se puede eliminar porque ya ha sido aceptada o rechazada.");
        }

        solicitudEspecialistaRepository.deleteById(id);
    }

    /// Metodo para filtrar solicitudes
    @Override
    public List<FichaCompletaSolicitudEspecialistaDTO> filtrarSolicitudes(BuscarSolicitudEspecialistaDTO filtro) throws SolicitudEspecialistaException {
        Specification<SolicitudEspecialista> spec = (root, query, cb) -> cb.conjunction();

        // Filtro por ID
        if (filtro.id() != null) {
            spec = spec.and(SolicitudEspecialistaSpecifications.tieneId(filtro.id()));
        }

        // Filtro por Estado (String a Enum)
        if (filtro.estado() != null && !filtro.estado().isEmpty()) {
            try {
                EstadosSolicitudes estadoEnum = EstadosSolicitudes.valueOf(filtro.estado().toUpperCase());
                spec = spec.and(SolicitudEspecialistaSpecifications.tieneEstado(estadoEnum));
            } catch (IllegalArgumentException e) {
                throw new SolicitudEspecialistaException("El estado ingresado no es válido.");
            }
        }

        // Filtro por fecha exacta
        if (filtro.fechaSolicitud() != null && !filtro.fechaSolicitud().isEmpty()) {
            try {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
                LocalDate fecha = LocalDate.parse(filtro.fechaSolicitud(), formatter);
                spec = spec.and(SolicitudEspecialistaSpecifications.tieneFechaSolicitud(fecha));
            } catch (DateTimeParseException e) {
                throw new SolicitudEspecialistaException("El formato de la fecha es inválido. Debe ser 'dd-MM-yyyy'.");
            }
        }

        // Filtro por email de usuario
        if (filtro.email() != null && !filtro.email().isEmpty()) {
            spec = spec.and(SolicitudEspecialistaSpecifications.tieneUsuarioEmail(filtro.email()));
        }

        List<SolicitudEspecialista> solicitudesEncontradas = solicitudEspecialistaRepository.findAll(spec);

        if (solicitudesEncontradas.isEmpty()) {
            throw new SolicitudEspecialistaException("No hay coincidencias con su búsqueda.");
        }

        return solicitudesEncontradas.stream()
                .map(FichaCompletaSolicitudEspecialistaDTO::new)
                .toList();
    }

}
