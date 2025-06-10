package com.findfix.find_fix_app.especialista.controller;

import com.findfix.find_fix_app.especialista.dto.*;
import com.findfix.find_fix_app.especialista.model.Especialista;
import com.findfix.find_fix_app.especialista.service.EspecialistaService;
import com.findfix.find_fix_app.exception.exceptions.EspecialistaExcepcion;
import com.findfix.find_fix_app.exception.exceptions.SpecialistRequestNotFoundException;
import com.findfix.find_fix_app.exception.exceptions.UserNotFoundException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/especialista")
@RequiredArgsConstructor
public class EspecialistaController {
    private final EspecialistaService especialistaService;


    @PostMapping("/agregar/{id}")
    public ResponseEntity<Map<String, Object>> guardar(@Valid @PathVariable Long id) {
        Map<String, Object> response = new HashMap<>();
        try {
            especialistaService.guardar(id);
            response.put("message", "Especialista registrado exitosamente");
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (UserNotFoundException e) {
            response.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
        }
    }
    @GetMapping
    public ResponseEntity<Map<String, Object>> obtenerEspecialistas() {
        Map<String, Object> response = new HashMap<>();
        List<Especialista> especialistas = especialistaService.obtenerEspecialistas();
        if (!especialistas.isEmpty()) {
            response.put("message", "Lista de especialistas encontrada");
            response.put("data", especialistas.stream().map(EspecialistaFichaCompletaDTO::new).toList());
            return ResponseEntity.ok(response);
        }
        response.put("message", "No hay especialistas registrados");
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }


    @GetMapping("/disponibles")
    public ResponseEntity<Map<String, Object>> obtenerEspecialistasDisponibles() {
        Map<String, Object> response = new HashMap<>();
        List<Especialista> especialistas = especialistaService.obtenerEspecialistasDisponibles();
        if (!especialistas.isEmpty()) {
            response.put("message", "Lista de especialistas disponibles encontrada");
            response.put("data", especialistas.stream().map(EspecialistaListadoDTO::new).toList());
            return ResponseEntity.ok(response);
        }
        response.put("message", "No hay especialistas disponibles");
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    @GetMapping("/id/{id}")
    public ResponseEntity<Map<String, Object>> buscarPorId(@PathVariable Long id) {
        Map<String, Object> response = new HashMap<>();
        return especialistaService.buscarPorId(id)
                .map(especialista -> {
                    response.put("message", "Especialista encontrado");
                    response.put("data", new EspecialistaFichaCompletaDTO(especialista));
                    return ResponseEntity.ok(response);
                })
                .orElseGet(() -> {
                    response.put("message", "No se encontró un especialista con ID: " + id);
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
                });
    }


    @GetMapping("/dni/{dni}")
    public ResponseEntity<Map<String, Object>> buscarPorDni(@PathVariable Long dni) {
        Map<String, Object> response = new HashMap<>();
        return especialistaService.buscarPorDni(dni)
                .map(especialista -> {
                    response.put("message", "Especialista encontrado");
                    response.put("data", new EspecialistaFichaCompletaDTO(especialista));
                    return ResponseEntity.ok(response);
                })
                .orElseGet(() -> {
                    response.put("message", "No se encontró un especialista con DNI: " + dni);
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
                });
    }

    @GetMapping("/oficio/{oficio}")
    public ResponseEntity<Map<String, Object>> buscarPorOficio(@PathVariable String oficio) {
        Map<String, Object> response = new HashMap<>();
        List<Especialista> especialistas = especialistaService.buscarPorOficio(oficio);
        if (!especialistas.isEmpty()) {
            response.put("message", "Especialistas encontrados");
            response.put("data", especialistas.stream().map(EspecialistaFichaCompletaDTO::new).toList());
            return ResponseEntity.ok(response);
        } else {
            response.put("message", "No se encontraron especialistas con el oficio: " + oficio);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }


    @GetMapping("/ciudad/{ciudad}")
    public ResponseEntity<Map<String, Object>> buscarPorCiudad(@PathVariable String ciudad) {
        Map<String, Object> response = new HashMap<>();
        List<Especialista> especialistas = especialistaService.buscarPorCiudad(ciudad);
        if (!especialistas.isEmpty()) {
            response.put("message", "Especialistas de la ciudad encontrados");
            response.put("data", especialistas.stream().map(EspecialistaFichaCompletaDTO::new).toList());
            return ResponseEntity.ok(response);
        }
        response.put("message", "No se encontraron especialistas de la ciudad: " + ciudad);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }


    @GetMapping("/email/{email}")
    public ResponseEntity<Map<String, Object>> buscarPorEmail(@PathVariable String email) {
        Map<String, Object> response = new HashMap<>();
        return especialistaService.buscarPorEmail(email)
                .map(especialista -> {
                    response.put("message", "Especialista encontrado");
                    response.put("data", new EspecialistaFichaCompletaDTO(especialista));

                    return ResponseEntity.ok(response);
                })
                .orElseGet(() -> {
                    response.put("message", "No se encontró un especialista con email: " + email);
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
                });
    }

    @PutMapping("/actualizar/{email}")
    public ResponseEntity<Map<String, Object>> actualizarEspecialista(@PathVariable String email, @Valid @RequestBody ActualizarEspecialistaDTO dto) {
        Map<String, Object> response = new HashMap<>();
        try {
            Especialista especialista = especialistaService.actualizarEspecialista(email, dto);
            response.put("message", "Especialista actualizado correctamente");
            response.put("data", new EspecialistaRespuestaDTO(especialista));
            return ResponseEntity.ok(response);
        } catch (SpecialistRequestNotFoundException e) {
            response.put("message", "Especialista no encontrado");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }
    @DeleteMapping("/eliminar/{email}")
    public ResponseEntity<Map<String, Object>> eliminarPorEmail(@PathVariable String email) {
        Map<String, Object> response = new HashMap<>();
        try {
            especialistaService.eliminarPorEmail(email);
            response.put("message", "Especialista eliminado correctamente");
            return ResponseEntity.ok(response);
        } catch (SpecialistRequestNotFoundException e) {
            response.put("message", "El especialista no se encontró");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }


    @PatchMapping("/{email}/oficios")
    public ResponseEntity<Map<String, Object>> actualizarOficiosDeEspecialista(@PathVariable String email, @Valid @RequestBody ActualizarOficioEspDTO dto) {
        Map<String, Object> response = new HashMap<>();
        try {
            Especialista especialista = especialistaService.actualizarOficioDeEspecialista(email, dto);
            response.put("message", "Oficios actualizados correctamente");
            response.put("data", new EspecialistaRespuestaDTO(especialista));
            return ResponseEntity.ok(response);
        } catch (SpecialistRequestNotFoundException e) {
            response.put("message", "Especialista no encontrado");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        } catch (EspecialistaExcepcion e) {
            response.put("message", "Datos inválidos para la actualización de oficios");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }



}
