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
    public ResponseEntity<ApiResponse<List<VisualizarTrabajoAppClienteDTO>>> obtenerTrabajosDelCliente() throws UserNotFoundException, TrabajoAppException {
        List<TrabajoApp> trabajos = trabajoAppService.obtenerTrabajosClientes();
        List<VisualizarTrabajoAppClienteDTO> dtos = trabajos.stream()
                .map(VisualizarTrabajoAppClienteDTO::new).toList();
        return ResponseEntity.ok(new ApiResponse<>("Lista de trabajos encontrada ☑️",dtos));
    }

    @GetMapping("/especialista/mis-trabajos")
    public ResponseEntity<ApiResponse<List<VisualizarTrabajoAppEspecialistaDTO>>> obtenerTrabajosDelEspecialista() throws UserNotFoundException, TrabajoAppException, EspecialistaNotFoundException {
        List<TrabajoApp> trabajos = trabajoAppService.obtenerTrabajosEspecialista();
        return ResponseEntity.ok(new ApiResponse<>("Lista de trabajos encontrada☑️",
                trabajos.stream().map(VisualizarTrabajoAppEspecialistaDTO::new).toList()));
    }

    @GetMapping("/especialista/mis-trabajos/estado")
    public ResponseEntity<ApiResponse<List<VisualizarTrabajoAppEspecialistaDTO>>> obtenerTrabajosDelEspecialistaPorEstado(@Valid @RequestParam String estado) throws UserNotFoundException, TrabajoAppException, EspecialistaNotFoundException {
        List<TrabajoApp> trabajos = trabajoAppService.obtenerTrabajosEspecialistaEstado(estado);
        List<VisualizarTrabajoAppEspecialistaDTO> dtos = trabajos.stream()
                .map(VisualizarTrabajoAppEspecialistaDTO::new ).toList();
        return ResponseEntity.ok(new ApiResponse<>("Lista de trabajos del especialista filtrada por estado " + estado + "☑️",dtos));
    }


    @GetMapping("/especialista/ficha-trabajo/{tituloBuscado}")
    public ResponseEntity<ApiResponse<VisualizarTrabajoAppEspecialistaDTO>> obtenerFichaDeTrabajoEspecialista(@PathVariable String tituloBuscado) throws TrabajoAppNotFoundException, UserNotFoundException, TrabajoAppException, EspecialistaNotFoundException {
        TrabajoApp trabajoApp = trabajoAppService.obtenerFichaDeTrabajoParaEspecialista(tituloBuscado);
        return ResponseEntity.ok(new ApiResponse<>("Ficha de trabajo encontrado ☑️",new VisualizarTrabajoAppEspecialistaDTO(trabajoApp)));
    }

    @GetMapping("/cliente/ficha-trabajo/{id}")
    public ResponseEntity<ApiResponse<VisualizarTrabajoAppClienteDTO>> obtenerFichaDeTrabajoCliente(@PathVariable Long id) throws TrabajoAppNotFoundException, UserNotFoundException, TrabajoAppException {
        TrabajoApp trabajoApp = trabajoAppService.obtenerFichaDeTrabajoParaCliente(id);
        return ResponseEntity.ok(new ApiResponse<>("Ficha de trabajo encontrado ☑️",new VisualizarTrabajoAppClienteDTO(trabajoApp)));
    }

    @PatchMapping("/actualizar-datos/{tituloBuscado}")
    public ResponseEntity<ApiResponse<String>> actualizarTrabajo(@PathVariable String tituloBuscado, @Valid @RequestBody ActualizarTrabajoAppDTO dto) throws TrabajoAppNotFoundException, TrabajoAppException, UserNotFoundException, EspecialistaNotFoundException {
        TrabajoApp trabajoApp = trabajoAppService.actualizarTrabajo(tituloBuscado,dto);
        return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse<>("Trabajo modificado con exito ☑️","Busque la ficha del trabajo para visualizar los cambios"));
    }

    @PatchMapping("/actualizar-estado/{titulo}/{nuevoEstado}")
    public ResponseEntity<ApiResponse<String>> actualizarEstadoTrabajo(@PathVariable String titulo, @PathVariable String nuevoEstado) throws TrabajoAppNotFoundException, TrabajoAppException, UserNotFoundException, EspecialistaNotFoundException {
        trabajoAppService.modificarEstadoTrabajo(titulo,nuevoEstado);
        return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse<>("Estado del trabajo modificado con exito ☑️","Busque la ficha del trabajo para visualizar los cambios"));
    }


}
