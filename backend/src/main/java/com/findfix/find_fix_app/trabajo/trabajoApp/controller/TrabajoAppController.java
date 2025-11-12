package com.findfix.find_fix_app.trabajo.trabajoApp.controller;

import com.findfix.find_fix_app.trabajo.trabajoApp.dto.ActualizarTrabajoAppDTO;
import com.findfix.find_fix_app.trabajo.trabajoApp.dto.BuscarTrabajoAppDTO;
import com.findfix.find_fix_app.trabajo.trabajoApp.dto.VisualizarTrabajoAppClienteDTO;
import com.findfix.find_fix_app.trabajo.trabajoApp.dto.VisualizarTrabajoAppEspecialistaDTO;
import com.findfix.find_fix_app.trabajo.trabajoApp.model.TrabajoApp;
import com.findfix.find_fix_app.trabajo.trabajoApp.service.TrabajoAppService;
import com.findfix.find_fix_app.trabajo.trabajoExterno.dto.BuscarTrabajoExternoDTO;
import com.findfix.find_fix_app.utils.apiResponse.ApiResponse;
import com.findfix.find_fix_app.utils.exception.exceptions.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/trabajos-app")
@RequiredArgsConstructor
@Validated
public class TrabajoAppController {
    private final TrabajoAppService trabajoAppService;

    @GetMapping("/cliente/mis-trabajos")
    @PreAuthorize("hasRole('CLIENTE')")
    public ResponseEntity<ApiResponse<List<VisualizarTrabajoAppClienteDTO>>> obtenerTrabajosDelCliente() throws UsuarioNotFoundException, TrabajoAppException {
        List<TrabajoApp> trabajos = trabajoAppService.obtenerTrabajosClientes();
        List<VisualizarTrabajoAppClienteDTO> dtos = trabajos.stream()
                .map(VisualizarTrabajoAppClienteDTO::new).toList();
        return ResponseEntity.ok(new ApiResponse<>("Lista de trabajos encontrada ☑️", dtos));
    }

    @GetMapping("/especialista/mis-trabajos")
    @PreAuthorize("hasRole('ESPECIALISTA')")
    public ResponseEntity<ApiResponse<List<VisualizarTrabajoAppEspecialistaDTO>>> obtenerTrabajosDelEspecialista() throws UsuarioNotFoundException, TrabajoAppException, EspecialistaNotFoundException {
        List<TrabajoApp> trabajos = trabajoAppService.obtenerTrabajosEspecialista();
        return ResponseEntity.ok(new ApiResponse<>("Lista de trabajos encontrada☑️",
                trabajos.stream().map(VisualizarTrabajoAppEspecialistaDTO::new).toList()));
    }

    @GetMapping("/especialista/ficha-trabajo/{tituloBuscado}")
    @PreAuthorize("hasRole('ESPECIALISTA')")
    public ResponseEntity<ApiResponse<VisualizarTrabajoAppEspecialistaDTO>> obtenerFichaDeTrabajoEspecialista(@PathVariable String tituloBuscado) throws TrabajoAppNotFoundException, UsuarioNotFoundException, TrabajoAppException, EspecialistaNotFoundException {
        TrabajoApp trabajoApp = trabajoAppService.obtenerFichaDeTrabajoParaEspecialista(tituloBuscado);
        return ResponseEntity.ok(new ApiResponse<>("Ficha de trabajo encontrado ☑️", new VisualizarTrabajoAppEspecialistaDTO(trabajoApp)));
    }

    @GetMapping("/cliente/ficha-trabajo/{id}")
    @PreAuthorize("hasRole('CLIENTE')")
    public ResponseEntity<ApiResponse<VisualizarTrabajoAppClienteDTO>> obtenerFichaDeTrabajoCliente(@PathVariable Long id) throws TrabajoAppNotFoundException, UsuarioNotFoundException, TrabajoAppException {
        TrabajoApp trabajoApp = trabajoAppService.obtenerFichaDeTrabajoParaCliente(id);
        return ResponseEntity.ok(new ApiResponse<>("Ficha de trabajo encontrado ☑️", new VisualizarTrabajoAppClienteDTO(trabajoApp)));
    }

    @PatchMapping("/actualizar-datos/{tituloBuscado}")
    @PreAuthorize("hasRole('ESPECIALISTA')")
    public ResponseEntity<ApiResponse<String>> actualizarTrabajo(@PathVariable String tituloBuscado, @Valid @RequestBody ActualizarTrabajoAppDTO dto) throws TrabajoAppNotFoundException, TrabajoAppException, UsuarioNotFoundException, EspecialistaNotFoundException {
        TrabajoApp trabajoApp = trabajoAppService.actualizarTrabajo(tituloBuscado, dto);
        return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse<>("Trabajo modificado con exito ☑️", "Busque la ficha del trabajo para visualizar los cambios"));
    }

    @PatchMapping("/actualizar-estado/{titulo}/{nuevoEstado}")
    @PreAuthorize("hasRole('ESPECIALISTA')")
    public ResponseEntity<ApiResponse<String>> actualizarEstadoTrabajo(@PathVariable String titulo, @PathVariable String nuevoEstado) throws TrabajoAppNotFoundException, TrabajoAppException, UsuarioNotFoundException, EspecialistaNotFoundException {
        trabajoAppService.modificarEstadoTrabajo(titulo, nuevoEstado);
        return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse<>("Estado del trabajo modificado con exito ☑️", "Busque la ficha del trabajo para visualizar los cambios"));
    }

    @GetMapping("/especialista/filtrar")
    @PreAuthorize("hasRole('ESPECIALISTA')")
    public ResponseEntity<ApiResponse<List<VisualizarTrabajoAppEspecialistaDTO>>> filtrarMisTrabajos(@RequestBody BuscarTrabajoAppDTO filtro) throws UsuarioNotFoundException, EspecialistaNotFoundException, TrabajoAppException {
        List<TrabajoApp> trabajos = trabajoAppService.filtrarTrabajosApp(filtro);
        return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse<>(
                "Coincidencias⬇️",
                trabajos.stream().map(VisualizarTrabajoAppEspecialistaDTO::new).toList()));
    }

    @GetMapping("/cliente/filtrar/{estado}")
    @PreAuthorize("hasRole('CLIENTE')")
    public ResponseEntity<ApiResponse<List<VisualizarTrabajoAppClienteDTO>>> filtrarPorEstado(@PathVariable String estado) throws UsuarioNotFoundException, TrabajoAppException {
        List<TrabajoApp> trabajos = trabajoAppService.filtrarPorEstadoCliente(estado);
        return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse<>("Coincidencias⬇️",
                trabajos.stream().map(VisualizarTrabajoAppClienteDTO::new).toList()));
    }

}
