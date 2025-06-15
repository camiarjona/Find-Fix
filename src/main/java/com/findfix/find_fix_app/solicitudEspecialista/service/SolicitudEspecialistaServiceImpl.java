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
        if(dto.estado().equals("PENDIENTE")) {

            if (dto.estado().equals("ACEPTADO")) {
                solicitudEspecialista.setEstado(EstadosSolicitudes.ACEPTADO);
                solicitudEspecialista.setRespuesta(dto.respuesta());
                usuarioService.agregarRol(solicitudEspecialista.getUsuario(), "ESPECIALISTA");
                especialistaService.guardar(solicitudEspecialista.getUsuario().getUsuarioId()); ///cambiar en especialistaservice el metodo para recibir un usuario directamente

            }

            if (dto.estado().equals("RECHAZADO")) {
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

        actualizarDatosSolicitud(solicitudEspecialista, dto);
        solicitudEspecialista.setFechaResolucion(LocalDate.now());
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

        solicitudEspecialistaRepository.deleteById(id);
    }

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


//    /// Metodo para buscar solicitud por id
//
//    @Override
//    public Optional<SolicitudEspecialista> buscarPorId(Long id) {
//        return solicitudEspecialistaRepository.findById(id);
//    }



//    /// Metodo para buscar solicitudes por Estado
//
//    @Override
//    public List<SolicitudEspecialista> buscarPorEstado(EstadosSolicitudes estado) throws SolicitudEspecialistaException {
//
//        List<SolicitudEspecialista> solicitudesEspecialistas = solicitudEspecialistaRepository.findAllByEstado(estado);
//        if(solicitudesEspecialistas.isEmpty()){
//            throw new SolicitudEspecialistaException("No se encontraron solicitudes en estado: " + estado.name());
//        }
//
//        return solicitudesEspecialistas;
//    }
//
//    /// Metodo para buscar solicitudes por fecha de solicitud
//
//    @Override
//    public List<SolicitudEspecialista> buscarPorFechaSolicitud(String fecha) throws SolicitudEspecialistaException {
//        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
//
//        try {
//            LocalDate localDate = LocalDate.parse(fecha, formatter);
//            return solicitudEspecialistaRepository.findByFechaSolicitud(localDate);
//        } catch (DateTimeParseException e) {
//            throw new SolicitudEspecialistaException("El formato de la fecha es inválido. Debe ser 'dd-MM-yyyy'.");
//        }
//    }
//
//    /// Metodo para buscar solicitudes por intervalo de fechas
//
//    @Override
//    public List<SolicitudEspecialista> buscarPorIntervaloFechas(String fechaInicio, String fechaFin) throws SolicitudEspecialistaException {
//        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
//        List<SolicitudEspecialista> solicitudesEspecialistas;
//
//        try {
//            LocalDate inicio = LocalDate.parse(fechaInicio, formatter);
//            LocalDate fin = LocalDate.parse(fechaFin, formatter);
//
//            if (inicio.isAfter(fin)) {
//                throw new SolicitudEspecialistaException("La fecha de inicio no puede ser posterior a la fecha de fin.");
//            }
//            solicitudesEspecialistas = solicitudEspecialistaRepository.findByFechaSolicitudBetween(inicio, fin);
//
//            if (solicitudesEspecialistas.isEmpty()) {
//                throw new SolicitudEspecialistaException("No se encontraron solicitudes en el intervalo de fechas especificadas.");
//            }
//
//
//            return solicitudesEspecialistas;
//        } catch (DateTimeParseException e) {
//            throw new SolicitudEspecialistaException("El formato de las fechas es inválido. Debe ser 'dd-MM-yyyy'.");
//        }
//    }
//
//    /// Metodo para buscar solicitudes por usuario de parte del admin
//
//    @Override
//    public List<SolicitudEspecialista> buscarPorUsuarioAdmin(String email) throws SolicitudEspecialistaNotFoundException {
//        List<SolicitudEspecialista> solicitudEspecialistas = solicitudEspecialistaRepository.findByUsuarioEmail(email);
//        if(solicitudEspecialistas.isEmpty()){
//            throw new SolicitudEspecialistaNotFoundException("No se encontraron solicitudes para el usuario con email: " + email);
//        }
//        return solicitudEspecialistas;
//    }





}
