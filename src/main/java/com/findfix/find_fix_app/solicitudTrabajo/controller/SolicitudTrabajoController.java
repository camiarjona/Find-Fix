package com.findfix.find_fix_app.solicitudTrabajo.controller;

import com.findfix.find_fix_app.exception.exceptions.SolicitudTrabajoException;
import com.findfix.find_fix_app.exception.exceptions.SolicitudTrabajoNotFoundException;
import com.findfix.find_fix_app.exception.exceptions.EspecialistaNotFoundException;
import com.findfix.find_fix_app.exception.exceptions.UserNotFoundException;
import com.findfix.find_fix_app.solicitudTrabajo.dto.*;
import com.findfix.find_fix_app.solicitudTrabajo.model.SolicitudTrabajo;
import com.findfix.find_fix_app.solicitudTrabajo.service.SolicitudTrabajoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/solicitud-trabajo")
@RequiredArgsConstructor
@Validated
public class SolicitudTrabajoController {
    private final SolicitudTrabajoService solicitudTrabajoService;

    @PostMapping("/registrar-solicitud")
    @PreAuthorize("hasRole('CLIENTE')")
    public ResponseEntity<Map<String, Object>> registrarNuevaSolicitud (@Valid @RequestBody SolicitarTrabajoDTO solicitudTrabajo) throws UserNotFoundException, EspecialistaNotFoundException {
        SolicitudTrabajo solicitud = solicitudTrabajoService.registrarNuevaSolicitud(solicitudTrabajo);
        Map<String, Object> response = new HashMap<>();
        response.put("Mensaje", "Se ha enviado la solicitud al especialista✅" );
        response.put("Solicitud", new MostrarSolicitudTrabajoDTO(solicitud));
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/mis-solicitudes-enviadas")
    @PreAuthorize("hasRole('CLIENTE')")
    public ResponseEntity<Map<String, Object>> visualizarSolicitudesEnviadas() throws UserNotFoundException, SolicitudTrabajoException {
        List<SolicitudTrabajo> solicitudesEnviadas = solicitudTrabajoService.obtenerSolicitudesDelCliente();
        Map<String, Object> response = new HashMap<>();
        response.put("Mensaje", "Lista de solicitudes encontrada️☑️️");
        response.put("Solicitudes enviadas",  solicitudesEnviadas.stream().map(MostrarSolicitudTrabajoDTO::new));
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping("/mis-solicitudes-recibidas")
    @PreAuthorize("hasRole('ESPECIALISTA')")
    public ResponseEntity<Map<String, Object>> visualizarSolicitudesRecibidas() throws SolicitudTrabajoException, EspecialistaNotFoundException, UserNotFoundException {
        List<SolicitudTrabajo> solicitudesRecibidas =  solicitudTrabajoService.obtenerSolicitudesDelEspecialista();
        Map<String, Object> response = new HashMap<>();
        response.put("Mensaje", "Lista de solicitudes encontrada️☑️️");
        response.put("Solicitudes recibidas", solicitudesRecibidas.stream().map(MostrarSolicitudDTO::new));
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @DeleteMapping("/eliminar-solicitud/{id}")
    @PreAuthorize("hasRole('CLIENTE')")
    public ResponseEntity<Map<String, String>> eliminarSolicitud(@PathVariable Long id) throws UserNotFoundException, SolicitudTrabajoNotFoundException, SolicitudTrabajoException {
        solicitudTrabajoService.eliminarSolicitud(id);
        Map<String, String> response = new HashMap<>();
        response.put("Mensaje", "Solicitud eliminada con éxito✅");
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(response);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('CLIENTE', 'ESPECIALISTA')")
    public ResponseEntity<Map<String, Object>> buscarPorId(@PathVariable Long id) throws UserNotFoundException, SolicitudTrabajoNotFoundException {
        SolicitudTrabajo solicitudTrabajo = solicitudTrabajoService.buscarPorId(id)
                .orElseThrow(() -> new SolicitudTrabajoNotFoundException("Solicitud no encontrada"));
        Map<String, Object> response = new HashMap<>();
        response.put("Solicitud", new MostrarSolicitudTrabajoDTO(solicitudTrabajo));
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping("/filtrar/recibidas")
    @PreAuthorize("hasRole('ESPECIALISTA')")
    public ResponseEntity<Map<String, Object>> filtrarSolicitudesRecibidas(@RequestBody BuscarSolicitudDTO filtro) throws SolicitudTrabajoException, UserNotFoundException, EspecialistaNotFoundException {
        List<SolicitudTrabajo> solicitudes = solicitudTrabajoService.filtrarSolicitudesRecibidas(filtro);
        Map<String, Object> response = new HashMap<>();
        response.put("Mensaje", "Lista de solicitudes encontrada️☑️️");
        response.put("Solicitudes", solicitudes.stream().map(MostrarSolicitudTrabajoDTO::new).toList());
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping("/filtrar/enviadas")
    @PreAuthorize("hasRole('CLIENTE')")
    public ResponseEntity<Map<String, Object>> filtrarSolicitudesEnviadas(@RequestBody BuscarSolicitudDTO filtro) throws SolicitudTrabajoException, UserNotFoundException {
        List<SolicitudTrabajo> solicitudes = solicitudTrabajoService.filtrarSolicitudesEnviadas(filtro);
        Map<String, Object> response = new HashMap<>();
        response.put("Mensaje", "Lista de solicitudes encontrada️☑️️");
        response.put("Solicitudes", solicitudes.stream().map(MostrarSolicitudTrabajoDTO::new).toList());
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PatchMapping("/actualizar-estado/{id}")
    @PreAuthorize("hasRole('ESPECIALISTA')")
    public ResponseEntity<?> actualizarEstado(@PathVariable Long id, @RequestBody ActualizarEstadoDTO actualizar) throws UserNotFoundException, SolicitudTrabajoNotFoundException, SolicitudTrabajoException, EspecialistaNotFoundException {
        solicitudTrabajoService.actualizarEstadoSolicitud(actualizar, id);
        Map<String, Object> response = new HashMap<>();
        response.put("Mensaje", "Solicitud actualizada con éxito☑️");
        response.put("Estado", actualizar.estado());
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

}
