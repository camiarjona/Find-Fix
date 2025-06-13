package com.findfix.find_fix_app.trabajo.trabajoApp.controller;

import com.findfix.find_fix_app.especialista.dto.ActualizarEspecialistaDTO;
import com.findfix.find_fix_app.especialista.dto.EspecialistaRespuestaDTO;
import com.findfix.find_fix_app.especialista.model.Especialista;
import com.findfix.find_fix_app.exception.exceptions.*;
import com.findfix.find_fix_app.trabajo.trabajoApp.dto.ActualizarTrabajoAppDTO;
import com.findfix.find_fix_app.trabajo.trabajoApp.dto.VisualizarTrabajoAppClienteDTO;
import com.findfix.find_fix_app.trabajo.trabajoApp.dto.VisualizarTrabajoAppEspecialistaDTO;
import com.findfix.find_fix_app.trabajo.trabajoApp.model.TrabajoApp;
import com.findfix.find_fix_app.trabajo.trabajoApp.service.TrabajoAppService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.graphql.GraphQlProperties;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/trabajosApp")
@RequiredArgsConstructor
@Validated
public class TrabajoAppController {
    private final TrabajoAppService trabajoAppService;

    @GetMapping("/misTrabajosC")
    public ResponseEntity<Map<String, Object>> obtenerTrabajosDelCliente() throws UserNotFoundException, TrabajoAppException {
        List<TrabajoApp> trabajos = trabajoAppService.obtenerTrabajosClientes();

        List<VisualizarTrabajoAppClienteDTO> dtos = trabajos.stream()
                .map(trabajo -> new VisualizarTrabajoAppClienteDTO(
                        trabajo.getEspecialista().getUsuario().getNombre(),
                        trabajo.getDescripcion(),
                        trabajo.getEstado(),
                        trabajo.getPresupuesto(),
                        trabajo.getFechaInicio(),
                        trabajo.getFechaFin()
                )).toList();
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Lista de trabajos del cliente encontrada ☑️");
        response.put("data", dtos);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/misTrabajosE")
    public ResponseEntity<Map<String, Object>> obtenerTrabajosDelEspecialista() throws UserNotFoundException, TrabajoAppException, SpecialistRequestNotFoundException {
        List<TrabajoApp> trabajos = trabajoAppService.obtenerTrabajosEspecialista();

        List<VisualizarTrabajoAppEspecialistaDTO> dtos = trabajos.stream()
                .map(trabajo -> new VisualizarTrabajoAppEspecialistaDTO(
                        trabajo.getUsuario().getNombre(),
                        trabajo.getTitulo(),
                        trabajo.getDescripcion(),
                        trabajo.getEstado(),
                        trabajo.getPresupuesto(),
                        trabajo.getFechaInicio(),
                        trabajo.getFechaFin()
                )).toList();
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Lista de trabajos del cliente encontrada ☑️");
        response.put("data", dtos);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/misTrabajosE/estado")
    public ResponseEntity<Map<String, Object>> obtenerTrabajosDelEspecialistaPorEstado(@Valid @RequestParam String estado) throws UserNotFoundException, TrabajoAppException, SpecialistRequestNotFoundException {
        String estadoNormalizado = estado.toUpperCase().replace(" ", "_");
        List<TrabajoApp> trabajos = trabajoAppService.obtenerTrabajosEspecialistaEstado(estadoNormalizado);
        List<VisualizarTrabajoAppEspecialistaDTO> dtos = trabajos.stream()
                .map(trabajo -> new VisualizarTrabajoAppEspecialistaDTO(
                        trabajo.getUsuario().getNombre(),
                        trabajo.getTitulo(),
                        trabajo.getDescripcion(),
                        trabajo.getEstado(),
                        trabajo.getPresupuesto(),
                        trabajo.getFechaInicio(),
                        trabajo.getFechaFin()
                )).toList();

        Map<String, Object> response = new HashMap<>();
        response.put("message", "Lista de trabajos del especialista filtrada por estado " + estado + "☑️");
        response.put("data", dtos);

        return ResponseEntity.ok(response);
    }


    @GetMapping("/ficha/{tituloBuscado}")
    public ResponseEntity<String> obtenerFichaDeTrabajoEspecialista(@PathVariable String tituloBuscado) throws TrabajoAppNotFoundException {
        TrabajoApp trabajoApp = trabajoAppService.obtenerFichaDeTrabajo(tituloBuscado);
        String respuesta = String.format(
                """
                📋 **Ficha de Trabajo** 📋
                Cliente: %s
                Título: %s
                Descripción: %s
                Estado: %s
                Presupuesto: $%.2f
                Fechas: %s a %s
                """,
                trabajoApp.getUsuario().getNombre(),
                trabajoApp.getTitulo(),
                trabajoApp.getDescripcion(),
                trabajoApp.getEstado().toString(),
                trabajoApp.getPresupuesto(),
                trabajoApp.getFechaInicio(),
                trabajoApp.getFechaFin()
        );
        return ResponseEntity.ok(respuesta);
    }


    @PatchMapping("/{tituloBuscado}")
    public ResponseEntity<String> actualizarTrabajo(@PathVariable String tituloBuscado, @Valid @RequestBody ActualizarTrabajoAppDTO dto) throws TrabajoAppNotFoundException, TrabajoAppException {
        TrabajoApp trabajoApp = trabajoAppService.actualizarTrabajo(tituloBuscado,dto);
        return ResponseEntity.status(HttpStatus.OK).body("Trabajo modificado con exito ☑️");
    }


    @PatchMapping("/{titulo}/{nuevoEstado}")
    public ResponseEntity<String> actualizarEstadoTrabajo(@PathVariable String titulo, @PathVariable String nuevoEstado) throws TrabajoAppNotFoundException, TrabajoAppException {
        String nuevoEstadoNormalizado = nuevoEstado.toUpperCase().replace(" ", "_");
        trabajoAppService.modificarEstadoTrabajo(titulo,nuevoEstadoNormalizado);
        return ResponseEntity.status(HttpStatus.OK).body("Estado actualizado con exito ☑️");
    }


}
