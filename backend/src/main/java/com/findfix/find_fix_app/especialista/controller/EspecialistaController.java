package com.findfix.find_fix_app.especialista.controller;

import com.findfix.find_fix_app.especialista.dto.*;
import com.findfix.find_fix_app.especialista.model.Especialista;
import com.findfix.find_fix_app.especialista.service.EspecialistaService;
import com.findfix.find_fix_app.utils.apiResponse.ApiResponse;
import com.findfix.find_fix_app.utils.exception.exceptions.EspecialistaExcepcion;
import com.findfix.find_fix_app.utils.exception.exceptions.EspecialistaNotFoundException;
import com.findfix.find_fix_app.utils.exception.exceptions.RolNotFoundException;
import com.findfix.find_fix_app.utils.exception.exceptions.UsuarioNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.stream.Stream;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;


@RestController
@RequestMapping("/especialistas")
@RequiredArgsConstructor
@Validated
public class EspecialistaController {
    private final EspecialistaService especialistaService;


    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<EspecialistaListadoDTO>>> obtenerEspecialistas() throws EspecialistaNotFoundException {
        List<Especialista> especialistas = especialistaService.obtenerEspecialistas();

        return ResponseEntity.ok(new ApiResponse<>("Lista de especialistas encontrada☑️", especialistas.stream().map(EspecialistaListadoDTO::new).toList()));
    }


    @GetMapping("/disponibles")
    @PreAuthorize("hasAnyRole('CLIENTE', 'ESPECIALISTA')")
    public ResponseEntity<ApiResponse<List<EspecialistaListadoDTO>>> obtenerEspecialistasDisponibles() throws EspecialistaNotFoundException, UsuarioNotFoundException {
        List<Especialista> especialistas = especialistaService.obtenerEspecialistasDisponibles();

        return ResponseEntity.ok(new ApiResponse<>("Lista de especialistas disponibles encontrada☑️", especialistas.stream().map(EspecialistaListadoDTO::new).toList()));
    }


    @PatchMapping("/actualizar/{email}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<EspecialistaRespuestaDTO>> actualizarEspecialistaAdmin(@PathVariable String email, @Valid @RequestBody ActualizarEspecialistaDTO dto) throws EspecialistaExcepcion, EspecialistaNotFoundException {
        Especialista especialista = especialistaService.actualizarEspecialistaAdmin(email, dto);

        return ResponseEntity.ok(new ApiResponse<>("Especialista actualizado correctamente☑️", new EspecialistaRespuestaDTO(especialista)));
    }

    @PatchMapping("/actualizar/mis-datos")
    @PreAuthorize("hasRole('ESPECIALISTA')")
    public ResponseEntity<ApiResponse<EspecialistaRespuestaDTO>> actualizarEspecialista(@Valid @RequestBody ActualizarEspecialistaDTO dto) throws EspecialistaExcepcion, EspecialistaNotFoundException, UsuarioNotFoundException {
        Especialista especialista = especialistaService.actualizarEspecialista(dto);

        return ResponseEntity.ok(new ApiResponse<>("Especialista actualizado correctamente☑️", new EspecialistaRespuestaDTO(especialista)));
    }

    @DeleteMapping("/eliminar/{email}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<String>> eliminarPorEmail(@PathVariable String email) throws EspecialistaNotFoundException, RolNotFoundException {
       especialistaService.eliminarPorEmail(email);

        return ResponseEntity.ok(new ApiResponse<>("Especialista eliminado correctamente✅", "[]"));
    }


    @PatchMapping("/actualizar/oficios/{email}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<EspecialistaRespuestaDTO>> actualizarOficiosDeEspecialistaAdmin(@PathVariable String email, @Valid @RequestBody ActualizarOficioEspDTO dto) throws EspecialistaExcepcion, EspecialistaNotFoundException {
      Especialista especialista = especialistaService.actualizarOficioDeEspecialistaAdmin(email, dto);

        return ResponseEntity.ok(new ApiResponse<>("Oficios actualizados correctamente☑️", new EspecialistaRespuestaDTO(especialista)));
    }

    @PatchMapping("/actualizar/mis-oficios")
    @PreAuthorize("hasRole('ESPECIALISTA')")
    public ResponseEntity<ApiResponse<EspecialistaRespuestaDTO>> actualizarOficiosDeEspecialista(@Valid @RequestBody ActualizarOficioEspDTO dto) throws UsuarioNotFoundException, EspecialistaExcepcion, EspecialistaNotFoundException {
       Especialista especialista = especialistaService.actualizarOficioDeEspecialista(dto);

        return ResponseEntity.ok(new ApiResponse<>("Oficios actualizados correctamente☑️", new EspecialistaRespuestaDTO(especialista)));
    }

    @GetMapping("/ver-perfil")
    @PreAuthorize("hasRole('ESPECIALISTA')")
    public ResponseEntity<ApiResponse<VerPerfilEspecialistaDTO>> verPerfilEspecialista() throws UsuarioNotFoundException, EspecialistaNotFoundException {
        VerPerfilEspecialistaDTO especialistaDTO = especialistaService.verPerfilEspecialista();

        return ResponseEntity.ok(new ApiResponse<>("Perfil:", especialistaDTO));
    }

    @PostMapping("/filtrar")
    @PreAuthorize("hasAnyRole('ADMIN', 'CLIENTE', 'ESPECIALISTA')")
    public ResponseEntity<ApiResponse<List<EspecialistaFichaCompletaDTO>>> filtrarEspecialistas(@RequestBody BuscarEspecialistaDTO filtro)
            throws EspecialistaExcepcion, UsuarioNotFoundException {
        List<EspecialistaFichaCompletaDTO> especialistasFiltrados = especialistaService.filtrarEspecialistas(filtro);

        return ResponseEntity.ok(new ApiResponse<>("Coincidencias⬇️", especialistasFiltrados));
    }

    @GetMapping("/detalle")
    @PreAuthorize("hasAnyRole('CLIENTE')")
   public ResponseEntity<ApiResponse<VerPerfilEspecialistaDTO>> verDetalle(@RequestParam String email) throws UsuarioNotFoundException, EspecialistaNotFoundException {
        Especialista especialista = especialistaService.buscarPorEmail(email).orElseThrow (() -> new EspecialistaNotFoundException("Especialista no encontrado"));
       VerPerfilEspecialistaDTO especialistaDTO = new VerPerfilEspecialistaDTO(especialista);
        return ResponseEntity.ok(new ApiResponse<>("Perfil:", especialistaDTO));
    }
    

}
