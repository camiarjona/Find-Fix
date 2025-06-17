package com.findfix.find_fix_app.trabajo.trabajoApp.controller;

import com.findfix.find_fix_app.trabajo.trabajoApp.dto.ActualizarTrabajoAppDTO;
import com.findfix.find_fix_app.trabajo.trabajoApp.dto.VisualizarTrabajoAppClienteDTO;
import com.findfix.find_fix_app.trabajo.trabajoApp.dto.VisualizarTrabajoAppEspecialistaDTO;
import com.findfix.find_fix_app.trabajo.trabajoApp.model.TrabajoApp;
import com.findfix.find_fix_app.trabajo.trabajoApp.service.TrabajoAppService;
import com.findfix.find_fix_app.utils.apiResponse.ApiResponse;
import com.findfix.find_fix_app.utils.exception.exceptions.EspecialistaNotFoundException;
import com.findfix.find_fix_app.utils.exception.exceptions.TrabajoAppException;
import com.findfix.find_fix_app.utils.exception.exceptions.TrabajoAppNotFoundException;
import com.findfix.find_fix_app.utils.exception.exceptions.UserNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/trabajo-app")
@RequiredArgsConstructor
@Validated
public class TrabajoAppController {
    private final TrabajoAppService trabajoAppService;

    @GetMapping("/cliente/mis-trabajos")
    public ResponseEntity<Map<String, Object>> obtenerTrabajosDelCliente() throws UserNotFoundException, TrabajoAppException {
        List<TrabajoApp> trabajos = trabajoAppService.obtenerTrabajosClientes();

        List<VisualizarTrabajoAppClienteDTO> dtos = trabajos.stream()
                .map(VisualizarTrabajoAppClienteDTO::new).toList();
        Map<String, Object> response = new HashMap<>();
        response.put("Mensaje", "Lista de trabajos encontrada☑️");
        response.put("Mis trabajos", dtos);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/especialista/mis-trabajos")
    public ResponseEntity<?> obtenerTrabajosDelEspecialista() throws UserNotFoundException, TrabajoAppException, EspecialistaNotFoundException {
        List<TrabajoApp> trabajos = trabajoAppService.obtenerTrabajosEspecialista();

        return ResponseEntity.ok(new ApiResponse<>("Lista de trabajos encontrada☑️",
                trabajos.stream().map(VisualizarTrabajoAppEspecialistaDTO::new).toList()));
    }

    @GetMapping("/especialista/mis-trabajos/estado")
    public ResponseEntity<Map<String, Object>> obtenerTrabajosDelEspecialistaPorEstado(@Valid @RequestParam String estado) throws UserNotFoundException, TrabajoAppException, EspecialistaNotFoundException {
        List<TrabajoApp> trabajos = trabajoAppService.obtenerTrabajosEspecialistaEstado(estado);
        List<VisualizarTrabajoAppEspecialistaDTO> dtos = trabajos.stream()
                .map(VisualizarTrabajoAppEspecialistaDTO::new ).toList();

        Map<String, Object> response = new HashMap<>();
        response.put("message", "Lista de trabajos del especialista filtrada por estado " + estado + "☑️");
        response.put("data", dtos);

        return ResponseEntity.ok(response);
    }


    @GetMapping("/especialista/ficha-trabajo/{tituloBuscado}")
    public ResponseEntity<Map<String,Object>> obtenerFichaDeTrabajoEspecialista(@PathVariable String tituloBuscado) throws TrabajoAppNotFoundException, UserNotFoundException, TrabajoAppException, EspecialistaNotFoundException {
        TrabajoApp trabajoApp = trabajoAppService.obtenerFichaDeTrabajoParaEspecialista(tituloBuscado);
        Map<String,Object> response = new HashMap<>();
        response.put("Mensaje","Ficha de trabajo encontrado:");
        response.put("Ficha",new VisualizarTrabajoAppEspecialistaDTO(trabajoApp));

        return ResponseEntity.ok(response);
    }

    @GetMapping("/cliente/ficha-trabajo/{id}")
    public ResponseEntity<Map<String,Object>> obtenerFichaDeTrabajoCliente(@PathVariable Long id) throws TrabajoAppNotFoundException, UserNotFoundException, TrabajoAppException {
        TrabajoApp trabajoApp = trabajoAppService.obtenerFichaDeTrabajoParaCliente(id);
        Map<String,Object> response = new HashMap<>();
        response.put("Mensaje","Ficha de trabajo encontrado");
        response.put("Ficha",new VisualizarTrabajoAppClienteDTO(trabajoApp));

        return ResponseEntity.ok(response);
    }

    @PatchMapping("/actualizar-datos/{tituloBuscado}")
    public ResponseEntity<Map<String,Object>> actualizarTrabajo(@PathVariable String tituloBuscado, @Valid @RequestBody ActualizarTrabajoAppDTO dto) throws TrabajoAppNotFoundException, TrabajoAppException, UserNotFoundException, EspecialistaNotFoundException {
        TrabajoApp trabajoApp = trabajoAppService.actualizarTrabajo(tituloBuscado,dto);
        Map<String,Object> response = new HashMap<>();
        response.put("Mensaje","Trabajo modificado con exito ☑️");
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PatchMapping("/actualizar-estado/{titulo}/{nuevoEstado}")
    public ResponseEntity<Map<String,Object>> actualizarEstadoTrabajo(@PathVariable String titulo, @PathVariable String nuevoEstado) throws TrabajoAppNotFoundException, TrabajoAppException, UserNotFoundException, EspecialistaNotFoundException {
        trabajoAppService.modificarEstadoTrabajo(titulo,nuevoEstado);
        Map<String,Object> response = new HashMap<>();
        response.put("Mensaje","Estado actualizado con éxito ️️☑️");
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }


}
