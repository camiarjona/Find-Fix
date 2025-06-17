package com.findfix.find_fix_app.trabajo.trabajoExterno.controller;

import com.findfix.find_fix_app.trabajo.trabajoExterno.dto.BuscarTrabajoExternoDTO;
import com.findfix.find_fix_app.trabajo.trabajoExterno.dto.CrearTrabajoExternoDTO;
import com.findfix.find_fix_app.trabajo.trabajoExterno.dto.ModificarTrabajoExternoDTO;
import com.findfix.find_fix_app.trabajo.trabajoExterno.dto.TrabajoExternoDTO;
import com.findfix.find_fix_app.trabajo.trabajoExterno.model.TrabajoExterno;
import com.findfix.find_fix_app.trabajo.trabajoExterno.service.TrabajoExternoService;
import com.findfix.find_fix_app.utils.apiResponse.ApiResponse;
import com.findfix.find_fix_app.utils.exception.exceptions.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Stream;

@RestController
@RequestMapping("/trabajos-externos")
@RequiredArgsConstructor
@Validated
public class TrabajoExternoController {

    private final TrabajoExternoService trabajoExternoService;

    @PostMapping("/agregar")
    @PreAuthorize("hasRole('ESPECIALISTA')")
    public ResponseEntity<ApiResponse<TrabajoExternoDTO>> crearTrabajo(@Valid @RequestBody CrearTrabajoExternoDTO dto) throws UserNotFoundException, EspecialistaNotFoundException {
        TrabajoExterno trabajo = trabajoExternoService.crearTrabajoExterno(dto);
        return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse<>(
                "Trabajo externo creado con éxito✅",
                new TrabajoExternoDTO(trabajo)));
    }

    @GetMapping("/mis-trabajos")
    @PreAuthorize("hasRole('ESPECIALISTA')")
    public ResponseEntity<ApiResponse<List<TrabajoExternoDTO>>> obtenerTodos() throws UserNotFoundException, TrabajoExternoException, EspecialistaNotFoundException {
        List<TrabajoExterno> misTrabajos = trabajoExternoService.obtenerMisTrabajos();
        return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse<>(
                "\uD83D\uDD27Mis trabajos\uD83D\uDD27",
                misTrabajos.stream().map(TrabajoExternoDTO::new).toList()
        ));
    }

    @PatchMapping("/modificar/{titulo}")
    @PreAuthorize("hasRole('ESPECIALISTA')")
    public ResponseEntity<ApiResponse<String>> modificarTrabajo(@PathVariable String titulo, @Valid @RequestBody ModificarTrabajoExternoDTO dto) throws TrabajoExternoNotFoundException, OficioNotFoundException, UserNotFoundException, TrabajoExternoException, EspecialistaNotFoundException {
        trabajoExternoService.modificarTrabajoExterno(titulo, dto);
        return ResponseEntity.ok(new ApiResponse<>(
                "Trabajo externo actualizado con éxito☑️",
                "Consulte la lista de trabajos para corroborar los cambios."));
    }

    @DeleteMapping("/eliminar/{titulo}")
    @PreAuthorize("hasRole('ESPECIALISTA')")
    public ResponseEntity<ApiResponse<String>> eliminarTrabajo(@PathVariable String titulo) throws UserNotFoundException, TrabajoExternoException, EspecialistaNotFoundException {
        trabajoExternoService.borrarTrabajoExternoPorTitulo(titulo);
        return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse<>(
                "Trabajo externo eliminado con éxito✅", "{}"));
    }

    @PatchMapping("/actualizar-estado/{titulo}/{estado}")
    @PreAuthorize("hasRole('ESPECIALISTA')")
    public ResponseEntity<ApiResponse<String>> actualizarEstado(@PathVariable String titulo, @PathVariable String estado) throws UserNotFoundException, TrabajoAppNotFoundException, TrabajoExternoException, EspecialistaNotFoundException {
        trabajoExternoService.actualizarEstado(titulo, estado);
        return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse<>(
                "Estado actualizado con éxito☑️",
                "Estado: " + estado));
    }

    @GetMapping("/filtrar")
    @PreAuthorize("hasRole('ESPECIALISTA')")
    public ResponseEntity<ApiResponse<List<TrabajoExternoDTO>>> filtrarMisTrabajos(@RequestBody BuscarTrabajoExternoDTO filtro) throws UserNotFoundException, EspecialistaNotFoundException, TrabajoExternoException {
        List<TrabajoExterno> trabajos = trabajoExternoService.filtrarTrabajosExternos(filtro);
        return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse<>(
                "Coincidencias⬇️",
                trabajos.stream().map(TrabajoExternoDTO::new).toList()));
    }
}