package com.findfix.find_fix_app.solicitudEspecialista.controller;

import com.findfix.find_fix_app.utils.exception.exceptions.RolNotFoundException;
import com.findfix.find_fix_app.utils.exception.exceptions.SolicitudEspecialistaException;
import com.findfix.find_fix_app.utils.exception.exceptions.SolicitudEspecialistaNotFoundException;
import com.findfix.find_fix_app.utils.exception.exceptions.UserNotFoundException;
import com.findfix.find_fix_app.solicitudEspecialista.dto.*;
import com.findfix.find_fix_app.solicitudEspecialista.model.SolicitudEspecialista;
import com.findfix.find_fix_app.solicitudEspecialista.service.SolicitudEspecialistaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/solicitud-especialista")
@RequiredArgsConstructor
public class SolicitudEspecialistaController {
    private final SolicitudEspecialistaService solicitudEspecialistaService;

    @PostMapping("/enviar-solicitud")
    public ResponseEntity<Map<String, Object>> mandarSolicitud (@Valid @RequestBody MandarSolicitudEspecialistaDTO solicitudEspecialistaDTO) throws UserNotFoundException, SolicitudEspecialistaException {
        Map<String, Object> response = new HashMap<>();

        solicitudEspecialistaService.mandarSolicitud(solicitudEspecialistaDTO);
        response.put("Mensaje", "Solicitud generada exitosamente✅");

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<Map<String, Object>> obtenerSolicitudes() throws SolicitudEspecialistaNotFoundException {
        Map<String, Object> response = new HashMap<>();
        List<SolicitudEspecialista> solicitudesEspecialista = solicitudEspecialistaService.obtenerSolicitudesEspecialista();

        response.put("Mensaje", "Lista de solicitudes encontrada☑️");
        response.put("Solicitudes", solicitudesEspecialista.stream()
                .map(solicitud -> new MostrarSolicitudEspecialistaAdminDTO(
                        solicitud.getFechaSolicitud(),
                        solicitud.getEstado().name(),
                        solicitud.getUsuario().getEmail()
                ))
                .toList());

        return ResponseEntity.ok(response);
    }


    @GetMapping("/mis-solicitudes")
    public ResponseEntity<Map<String, Object>> obtenerMisSolicitudes() throws SolicitudEspecialistaException, SolicitudEspecialistaNotFoundException, UserNotFoundException {
        Map<String, Object> response = new HashMap<>();
        List<SolicitudEspecialista> solicitudesEspecialista = solicitudEspecialistaService.obtenerMisSolicitudesEspecialista();

        response.put("Mensaje", "Lista de solicitudes encontrada☑️");
        response.put("Solicitudes", solicitudesEspecialista.stream()
                .map(solicitud -> new MostrarSolicitudEspecialistaDTO(
                        solicitud.getFechaSolicitud(),
                        solicitud.getEstado().name(),
                        solicitud.getUsuario().getEmail(),
                        solicitud.getRespuesta()
                )).toList());

        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Map<String, Object>> actualizarSolicitudEspecialista(@Valid @RequestBody ActualizarSolicitudEspecialistaDTO actualizarSolicitudEspecialistaDTO, @PathVariable Long id) throws UserNotFoundException, SolicitudEspecialistaException, SolicitudEspecialistaNotFoundException, RolNotFoundException {
        Map<String, Object> response = new HashMap<>();


        SolicitudEspecialista solicitudEspecialista = solicitudEspecialistaService.actualizarSolicitudEspecialistaAdmin(actualizarSolicitudEspecialistaDTO, id);
        response.put("Mensaje", "Solicitud actualizada correctamente☑️");
        response.put("Solicitud actualizada", new FichaCompletaSolicitudEspecialistaDTO(solicitudEspecialista));
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> eliminarSolicitudEspecialista(@PathVariable Long id) throws SolicitudEspecialistaException, SolicitudEspecialistaNotFoundException {
        Map<String, Object> response = new HashMap<>();

        solicitudEspecialistaService.eliminarPorId(id);
        response.put("Mensaje", "Solicitud eliminada correctamente✅");

        return ResponseEntity.ok(response);
    }

    @GetMapping("/filtrar")
    public ResponseEntity<Map<String, Object>> filtrarSolicitudes(@RequestBody BuscarSolicitudEspecialistaDTO filtro) throws SolicitudEspecialistaException {
        List<FichaCompletaSolicitudEspecialistaDTO> solicitudesFiltradas = solicitudEspecialistaService.filtrarSolicitudes(filtro);
        Map<String, Object> response = new HashMap<>();
        response.put("Mensaje", "Lista de solicitudes encontrada️☑️️");
        response.put("Solicitudes", solicitudesFiltradas);
        return ResponseEntity.ok(response);
    }


}
