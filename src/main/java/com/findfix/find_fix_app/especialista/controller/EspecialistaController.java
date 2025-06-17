package com.findfix.find_fix_app.especialista.controller;

import com.findfix.find_fix_app.especialista.dto.*;
import com.findfix.find_fix_app.especialista.model.Especialista;
import com.findfix.find_fix_app.especialista.service.EspecialistaService;
import com.findfix.find_fix_app.utils.exception.exceptions.EspecialistaExcepcion;
import com.findfix.find_fix_app.utils.exception.exceptions.EspecialistaNotFoundException;
import com.findfix.find_fix_app.utils.exception.exceptions.UserNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
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

//    @PostMapping("/agregar/{id}")
//    public ResponseEntity<Map<String, Object>> guardar(@Valid @PathVariable Long id) throws UserNotFoundException {
//        Map<String, Object> response = new HashMap<>();
//
//        especialistaService.guardar();
//        response.put("message", "Especialista registrado exitosamente✅");
//
//        return ResponseEntity.status(HttpStatus.CREATED).body(response);
//    }

    @GetMapping
    public ResponseEntity<Map<String, Object>> obtenerEspecialistas() throws EspecialistaNotFoundException {
        Map<String, Object> response = new HashMap<>();
        List<Especialista> especialistas = especialistaService.obtenerEspecialistas();

        response.put("message", "Lista de especialistas encontrada☑️");
        response.put("data", especialistas.stream().map(EspecialistaFichaCompletaDTO::new).toList());

        return ResponseEntity.ok(response);
    }


    @GetMapping("/disponibles")
    public ResponseEntity<Map<String, Object>> obtenerEspecialistasDisponibles() throws EspecialistaNotFoundException {
        Map<String, Object> response = new HashMap<>();
        List<Especialista> especialistas = especialistaService.obtenerEspecialistasDisponibles();

        response.put("message", "Lista de especialistas disponibles encontrada☑\uFE0F");
        response.put("data", especialistas.stream().map(EspecialistaListadoDTO::new).toList());

        return ResponseEntity.ok(response);
    }

    @GetMapping("/id/{id}")
    public ResponseEntity<Map<String, Object>> buscarPorId(@PathVariable Long id) throws EspecialistaNotFoundException {
        Especialista especialista = especialistaService.buscarPorId(id)
                .orElseThrow(() -> new EspecialistaNotFoundException("No se encontró un especialista con ID: " + id));

        Map<String, Object> response = new HashMap<>();
        response.put("message", "Especialista encontrado ☑☑\uFE0F");
        response.put("data", new EspecialistaFichaCompletaDTO(especialista));
        return ResponseEntity.ok(response);
    }

    @GetMapping("/dni/{dni}")
    public ResponseEntity<Map<String, Object>> buscarPorDni(@PathVariable Long dni) throws EspecialistaNotFoundException {
        Especialista especialista = especialistaService.buscarPorDni(dni)
                .orElseThrow(() -> new EspecialistaNotFoundException("No se encontró un especialista con DNI: " + dni));

        Map<String, Object> response = new HashMap<>();
        response.put("message", "Especialista encontrado ☑☑\uFE0F");
        response.put("data", new EspecialistaFichaCompletaDTO(especialista));
        return ResponseEntity.ok(response);
    }

    @GetMapping("/email/{email}")
    public ResponseEntity<Map<String, Object>> buscarPorEmail(@PathVariable String email) throws EspecialistaNotFoundException {
        Especialista especialista = especialistaService.buscarPorEmail(email)
                .orElseThrow(() -> new EspecialistaNotFoundException("No se encontró un especialista con email: " + email));

        Map<String, Object> response = new HashMap<>();
        response.put("message", "Especialista encontrado ☑\uFE0F");
        response.put("data", new EspecialistaFichaCompletaDTO(especialista));
        return ResponseEntity.ok(response);
    }

    @GetMapping("/oficio/{oficio}")
    public ResponseEntity<Map<String, Object>> buscarPorOficio(@PathVariable String oficio) throws EspecialistaNotFoundException {
        List<Especialista> especialistas = especialistaService.buscarPorOficio(oficio);

        if (especialistas.isEmpty()) {
            throw new EspecialistaNotFoundException("No se encontraron especialistas con el oficio: " + oficio);
        }

        Map<String, Object> response = new HashMap<>();
        response.put("message", "Especialistas encontrados ☑☑\uFE0F");
        response.put("data", especialistas.stream().map(EspecialistaFichaCompletaDTO::new).toList());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/ciudad/{ciudad}")
    public ResponseEntity<Map<String, Object>> buscarPorCiudad(@PathVariable String ciudad) throws EspecialistaNotFoundException {
        List<Especialista> especialistas = especialistaService.buscarPorCiudad(ciudad);

        if (especialistas.isEmpty()) {
            throw new EspecialistaNotFoundException("No se encontraron especialistas de la ciudad: " + ciudad);
        }

        Map<String, Object> response = new HashMap<>();
        response.put("message", "Especialistas de la ciudad encontrados ☑☑\uFE0F");
        response.put("data", especialistas.stream().map(EspecialistaFichaCompletaDTO::new).toList());
        return ResponseEntity.ok(response);
    }


    @PatchMapping("/actualizar/{email}")
    public ResponseEntity<Map<String, Object>> actualizarEspecialistaAdmin(@PathVariable String email, @Valid @RequestBody ActualizarEspecialistaDTO dto) throws EspecialistaExcepcion, EspecialistaNotFoundException {
        Map<String, Object> response = new HashMap<>();

        Especialista especialista = especialistaService.actualizarEspecialistaAdmin(email, dto);
        response.put("message", "Especialista actualizado correctamente☑\uFE0F");
        response.put("data", new EspecialistaRespuestaDTO(especialista));

        return ResponseEntity.ok(response);
    }

    @PatchMapping("/actualizar")
    public ResponseEntity<Map<String, Object>> actualizarEspecialista(@Valid @RequestBody ActualizarEspecialistaDTO dto) throws UserNotFoundException, EspecialistaExcepcion, EspecialistaNotFoundException {
        Map<String, Object> response = new HashMap<>();

        Especialista especialista = especialistaService.actualizarEspecialista(dto);
        response.put("message", "Especialista actualizado correctamente☑\uFE0F");
        response.put("data", new EspecialistaRespuestaDTO(especialista));

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/eliminar/{email}")
    public ResponseEntity<Map<String, Object>> eliminarPorEmail(@PathVariable String email) throws EspecialistaNotFoundException {
        Map<String, Object> response = new HashMap<>();

        especialistaService.eliminarPorEmail(email);
        response.put("message", "Especialista eliminado correctamente✅");

        return ResponseEntity.ok(response);
    }


    @PatchMapping("/{email}/oficios")
    public ResponseEntity<Map<String, Object>> actualizarOficiosDeEspecialistaAdmin(@PathVariable String email, @Valid @RequestBody ActualizarOficioEspDTO dto) throws EspecialistaExcepcion, EspecialistaNotFoundException {
        Map<String, Object> response = new HashMap<>();

        Especialista especialista = especialistaService.actualizarOficioDeEspecialistaAdmin(email, dto);
        response.put("message", "Oficios actualizados correctamente☑\uFE0F");
        response.put("data", new EspecialistaRespuestaDTO(especialista));

        return ResponseEntity.ok(response);
    }

    @PatchMapping("/actualizarOficios")
    public ResponseEntity<Map<String, Object>> actualizarOficiosDeEspecialista(@Valid @RequestBody ActualizarOficioEspDTO dto) throws UserNotFoundException, EspecialistaExcepcion, EspecialistaNotFoundException {
        Map<String, Object> response = new HashMap<>();

        Especialista especialista = especialistaService.actualizarOficioDeEspecialista(dto);
        response.put("message", "Oficios actualizados correctamente☑\uFE0F");
        response.put("data", new EspecialistaRespuestaDTO(especialista));

        return ResponseEntity.ok(response);
    }



}
