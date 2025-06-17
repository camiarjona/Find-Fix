package com.findfix.find_fix_app.trabajo.trabajoExterno.controller;

import com.findfix.find_fix_app.trabajo.trabajoExterno.dto.BuscarTrabajoExternoDTO;
import com.findfix.find_fix_app.trabajo.trabajoExterno.dto.CrearTrabajoExternoDTO;
import com.findfix.find_fix_app.trabajo.trabajoExterno.dto.ModificarTrabajoExternoDTO;
import com.findfix.find_fix_app.trabajo.trabajoExterno.dto.TrabajoExternoDTO;
import com.findfix.find_fix_app.trabajo.trabajoExterno.model.TrabajoExterno;
import com.findfix.find_fix_app.trabajo.trabajoExterno.service.TrabajoExternoService;
import com.findfix.find_fix_app.utils.exception.exceptions.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/trabajos-externos")
@RequiredArgsConstructor
@Validated
public class TrabajoExternoController {

    private final TrabajoExternoService trabajoExternoService;

    @PostMapping("/agregar")
    @PreAuthorize("hasRole('ESPECIALISTA')")
    public ResponseEntity<Map<String, Object>> crearTrabajo(@Valid @RequestBody CrearTrabajoExternoDTO dto) throws UserNotFoundException, EspecialistaNotFoundException {
        TrabajoExterno trabajo = trabajoExternoService.crearTrabajoExterno(dto);
        Map<String, Object> response = new HashMap<>();
        response.put("Mensaje: ", "Trabajo externo creado con éxito✅.");
        response.put("Trabajo externo", new TrabajoExternoDTO(trabajo));
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping("/mis-trabajos")
    @PreAuthorize("hasRole('ESPECIALISTA')")
    public ResponseEntity<?> obtenerTodos() throws UserNotFoundException, TrabajoExternoException, EspecialistaNotFoundException {
        List<TrabajoExterno> misTrabajos = trabajoExternoService.obtenerMisTrabajos();
        Map<String, Object> response = new HashMap<>();
        response.put("Mensaje", "Lista de trabajos encontrada con éxito✅");
        response.put("\uD83D\uDEE0️Mis trabajos\uD83D\uDEE0️", misTrabajos.stream().map(TrabajoExternoDTO::new));
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PatchMapping("/modificar/{titulo}")
    @PreAuthorize("hasRole('ESPECIALISTA')")
    public ResponseEntity<Map<String, String>> modificarTrabajo(@PathVariable String titulo, @Valid @RequestBody ModificarTrabajoExternoDTO dto) throws TrabajoExternoNotFoundException, OficioNotFoundException, UserNotFoundException, TrabajoExternoException, EspecialistaNotFoundException {
        trabajoExternoService.modificarTrabajoExterno(titulo, dto);
        Map<String, String> response = new HashMap<>();
        response.put("Mensaje: ", "Trabajo externo actualizado con éxito☑️");
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/eliminar/{titulo}")
    @PreAuthorize("hasRole('ESPECIALISTA')")
    public ResponseEntity<Map<String, String>> eliminarTrabajo(@PathVariable String titulo) throws UserNotFoundException, TrabajoExternoException, EspecialistaNotFoundException {
        trabajoExternoService.borrarTrabajoExternoPorTitulo(titulo);
        Map<String, String> response = new HashMap<>();
        response.put("Mensaje: ", "Trabajo externo eliminado con éxito✅");
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(response);
    }

    @PatchMapping("/actualizar-estado/{titulo}/{estado}")
    @PreAuthorize("hasRole('ESPECIALISTA')")
    public ResponseEntity<?> actualizarEstado(@PathVariable String titulo, @PathVariable String estado) throws UserNotFoundException, TrabajoAppNotFoundException, TrabajoExternoException, EspecialistaNotFoundException {
        trabajoExternoService.actualizarEstado(titulo, estado);
        Map<String, Object> response = new HashMap<>();
        response.put("Mensaje", "Estado actualizado con éxito☑️");
        response.put("Estado", estado);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping("/filtrar")
    @PreAuthorize("hasRole('ESPECIALISTA')")
    public ResponseEntity<?> filtrarMisTrabajos(@RequestBody BuscarTrabajoExternoDTO filtro) throws UserNotFoundException, EspecialistaNotFoundException, TrabajoExternoException {
        List<TrabajoExterno> trabajos = trabajoExternoService.filtrarTrabajosExternos(filtro);
        Map<String, Object> response = new HashMap<>();
        response.put("Mensaje", "Lista de trabajos encontrada️☑️️");
        response.put("Trabajos", trabajos.stream().map(TrabajoExternoDTO::new));
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}