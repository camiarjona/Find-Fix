package com.findfix.find_fix_app.solicitudEspecialista.controller;

import com.findfix.find_fix_app.utils.apiResponse.ApiResponse;
import com.findfix.find_fix_app.utils.exception.exceptions.RolNotFoundException;
import com.findfix.find_fix_app.utils.exception.exceptions.SolicitudEspecialistaException;
import com.findfix.find_fix_app.utils.exception.exceptions.SolicitudEspecialistaNotFoundException;
import com.findfix.find_fix_app.utils.exception.exceptions.UsuarioNotFoundException;
import com.findfix.find_fix_app.solicitudEspecialista.dto.*;
import com.findfix.find_fix_app.solicitudEspecialista.model.SolicitudEspecialista;
import com.findfix.find_fix_app.solicitudEspecialista.service.SolicitudEspecialistaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;
@RestController
@RequestMapping("/solicitud-especialista")
@RequiredArgsConstructor
@Validated
public class SolicitudEspecialistaController {
    private final SolicitudEspecialistaService solicitudEspecialistaService;

    @PostMapping("/enviar")
    @PreAuthorize("hasRole('CLIENTE')")
    public ResponseEntity<ApiResponse<MandarSolicitudEspecialistaDTO>> mandarSolicitud (@Valid @RequestBody MandarSolicitudEspecialistaDTO solicitudEspecialistaDTO) throws UsuarioNotFoundException, SolicitudEspecialistaException {
        solicitudEspecialistaService.mandarSolicitud(solicitudEspecialistaDTO);

        return ResponseEntity.status(HttpStatus.CREATED).body(new ApiResponse<>("Solicitud generada exitosamente✅", solicitudEspecialistaDTO));
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<MostrarSolicitudEspecialistaAdminDTO>> obtenerSolicitudes(
        @PageableDefault(size = 10) Pageable pageable) 
        throws SolicitudEspecialistaNotFoundException {
    
    return ResponseEntity.ok(solicitudEspecialistaService.obtenerSolicitudesEspecialista(pageable));
}

    @GetMapping("/mis-solicitudes")
    @PreAuthorize("hasRole('CLIENTE')")
    public ResponseEntity<ApiResponse<List<MostrarSolicitudEspecialistaDTO>>> obtenerMisSolicitudes() throws SolicitudEspecialistaException, SolicitudEspecialistaNotFoundException, UsuarioNotFoundException {
        List<SolicitudEspecialista> solicitudesEspecialista = solicitudEspecialistaService.obtenerMisSolicitudesEspecialista();

        return ResponseEntity.ok(new ApiResponse<>(
                "Lista de solicitudes encontrada☑️",
                solicitudesEspecialista.stream().map(MostrarSolicitudEspecialistaDTO::new).toList()));
    }

    @PatchMapping("/actualizar/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<FichaCompletaSolicitudEspecialistaDTO>> actualizarSolicitudEspecialista(@Valid @RequestBody ActualizarSolicitudEspecialistaDTO actualizarSolicitudEspecialistaDTO, @PathVariable Long id) throws UsuarioNotFoundException, SolicitudEspecialistaException, SolicitudEspecialistaNotFoundException, RolNotFoundException {
        SolicitudEspecialista solicitudEspecialista = solicitudEspecialistaService.actualizarSolicitudEspecialistaAdmin(actualizarSolicitudEspecialistaDTO, id);

        return ResponseEntity.ok(new ApiResponse<>("Solicitud actualizada correctamente☑️", new FichaCompletaSolicitudEspecialistaDTO(solicitudEspecialista)));
    }

    @DeleteMapping("/eliminar/{id}")
    @PreAuthorize("hasRole('CLIENTE')")
    public ResponseEntity<ApiResponse<String>> eliminarSolicitudEspecialista(@PathVariable Long id) throws SolicitudEspecialistaException, SolicitudEspecialistaNotFoundException {
        solicitudEspecialistaService.eliminarPorId(id);

        return ResponseEntity.ok(new ApiResponse<>("Solicitud eliminada correctamente✅", "[]"));
    }

    @GetMapping("/ficha/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','CLIENTE')")
    public ResponseEntity<ApiResponse<FichaCompletaSolicitudEspecialistaDTO>> obtenerFichaSolicitudPorId(@PathVariable Long id) throws SolicitudEspecialistaNotFoundException, SolicitudEspecialistaException, UsuarioNotFoundException {

        FichaCompletaSolicitudEspecialistaDTO ficha = solicitudEspecialistaService.obtenerFichaPorId(id);

        return ResponseEntity.ok(new ApiResponse<>("Ficha de solicitud encontrada☑️", ficha));
    }

    @PostMapping("/filtrar")
    @PreAuthorize("hasAnyRole('ADMIN','CLIENTE')")
    public ResponseEntity<ApiResponse<List<FichaCompletaSolicitudEspecialistaDTO>>> filtrarSolicitudes(@RequestBody BuscarSolicitudEspecialistaDTO filtro) throws SolicitudEspecialistaException, UsuarioNotFoundException {
        List<FichaCompletaSolicitudEspecialistaDTO> solicitudesFiltradas = solicitudEspecialistaService.filtrarSolicitudes(filtro);

        return ResponseEntity.ok(new ApiResponse<>("Coincidencias⬇️", solicitudesFiltradas));
    }


}
