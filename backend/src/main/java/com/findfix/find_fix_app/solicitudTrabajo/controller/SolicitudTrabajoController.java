package com.findfix.find_fix_app.solicitudTrabajo.controller;

import com.findfix.find_fix_app.utils.apiResponse.ApiResponse;
import com.findfix.find_fix_app.utils.exception.exceptions.SolicitudTrabajoException;
import com.findfix.find_fix_app.utils.exception.exceptions.SolicitudTrabajoNotFoundException;
import com.findfix.find_fix_app.utils.exception.exceptions.EspecialistaNotFoundException;
import com.findfix.find_fix_app.utils.exception.exceptions.UsuarioNotFoundException;
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

import java.util.List;

@RestController
@RequestMapping("/solicitud-trabajo")
@RequiredArgsConstructor
@Validated
public class SolicitudTrabajoController {
    private final SolicitudTrabajoService solicitudTrabajoService;

    @PostMapping("/registrar")
    @PreAuthorize("hasRole('CLIENTE')")
    public ResponseEntity<ApiResponse<MostrarSolicitudTrabajoDTO>> registrarNuevaSolicitud(@Valid @RequestBody SolicitarTrabajoDTO solicitudTrabajo) throws UsuarioNotFoundException, EspecialistaNotFoundException {
        SolicitudTrabajo solicitud = solicitudTrabajoService.registrarNuevaSolicitud(solicitudTrabajo);
        return ResponseEntity.status(HttpStatus.CREATED).body(new ApiResponse<>(
                "Se ha enviado la solicitud al especialista✅",
                new MostrarSolicitudTrabajoDTO(solicitud)));
    }

    @GetMapping("/enviadas/mis-solicitudes")
    @PreAuthorize("hasRole('CLIENTE')")
    public ResponseEntity<ApiResponse<List<MostrarSolicitudTrabajoDTO>>> visualizarSolicitudesEnviadas() throws UsuarioNotFoundException, SolicitudTrabajoException {
        List<SolicitudTrabajo> solicitudesEnviadas = solicitudTrabajoService.obtenerSolicitudesDelCliente();
        return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse<>(
                "\uD83D\uDCE4Solicitudes enviadas\uD83D\uDCE4",
                solicitudesEnviadas.stream().map(MostrarSolicitudTrabajoDTO::new).toList()));
    }

    @GetMapping("/recibidas/mis-solicitudes")
    @PreAuthorize("hasRole('ESPECIALISTA')")
    public ResponseEntity<ApiResponse<List<MostrarSolicitudDTO>>> visualizarSolicitudesRecibidas() throws SolicitudTrabajoException, EspecialistaNotFoundException, UsuarioNotFoundException {
        List<SolicitudTrabajo> solicitudesRecibidas = solicitudTrabajoService.obtenerSolicitudesDelEspecialista();
        return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse<>(
                "\uD83D\uDCE5Soicitudes recibidas\uD83D\uDCE5",
                solicitudesRecibidas.stream().map(MostrarSolicitudDTO::new).toList()));
    }

    @DeleteMapping("/eliminar/{id}")
    @PreAuthorize("hasRole('CLIENTE')")
    public ResponseEntity<ApiResponse<String>> eliminarSolicitud(@PathVariable Long id) throws UsuarioNotFoundException, SolicitudTrabajoNotFoundException, SolicitudTrabajoException {
        solicitudTrabajoService.eliminarSolicitud(id);
        return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse<>(
                "Solicitud eliminada con éxito✅", "{}"));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('CLIENTE', 'ESPECIALISTA')")
    public ResponseEntity<ApiResponse<MostrarSolicitudTrabajoDTO>> buscarPorId(@PathVariable Long id) throws UsuarioNotFoundException, SolicitudTrabajoNotFoundException {
        SolicitudTrabajo solicitudTrabajo = solicitudTrabajoService.buscarPorId(id)
                .orElseThrow(() -> new SolicitudTrabajoNotFoundException("Solicitud no encontrada"));
        return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse<>(
                "Solicitud",
                new MostrarSolicitudTrabajoDTO(solicitudTrabajo)));
    }

    @GetMapping("/filtrar/recibidas")
    @PreAuthorize("hasRole('ESPECIALISTA')")
    public ResponseEntity<ApiResponse<List<MostrarSolicitudDTO>>> filtrarSolicitudesRecibidas(@RequestBody BuscarSolicitudDTO filtro) throws SolicitudTrabajoException, UsuarioNotFoundException, EspecialistaNotFoundException {
        List<SolicitudTrabajo> solicitudes = solicitudTrabajoService.filtrarSolicitudesRecibidas(filtro);
        return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse<>(
                "Coincidencias⬇️",
                solicitudes.stream().map(MostrarSolicitudDTO::new).toList()));
    }

    @GetMapping("/filtrar/enviadas")
    @PreAuthorize("hasRole('CLIENTE')")
    public ResponseEntity<ApiResponse<List<MostrarSolicitudTrabajoDTO>>> filtrarSolicitudesEnviadas(@RequestBody BuscarSolicitudDTO filtro) throws SolicitudTrabajoException, UsuarioNotFoundException {
        List<SolicitudTrabajo> solicitudes = solicitudTrabajoService.filtrarSolicitudesEnviadas(filtro);
        return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse<>(
                "Coincidencias⬇️",
                solicitudes.stream().map(MostrarSolicitudTrabajoDTO::new).toList()));
    }

    @PatchMapping("/actualizar-estado/{id}")
    @PreAuthorize("hasRole('ESPECIALISTA')")
    public ResponseEntity<ApiResponse<String>> actualizarEstado(@PathVariable Long id, @RequestBody ActualizarEstadoDTO actualizar) throws UsuarioNotFoundException, SolicitudTrabajoNotFoundException, SolicitudTrabajoException, EspecialistaNotFoundException {
        solicitudTrabajoService.actualizarEstadoSolicitud(actualizar, id);
        return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse<>(
                "Solicitud actualizada con éxito☑️",
                actualizar.estado()));
    }

}
