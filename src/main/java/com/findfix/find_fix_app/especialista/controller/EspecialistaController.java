package com.findfix.find_fix_app.especialista.controller;

import com.findfix.find_fix_app.especialista.dto.*;
import com.findfix.find_fix_app.especialista.model.Especialista;
import com.findfix.find_fix_app.especialista.service.EspecialistaService;
import com.findfix.find_fix_app.usuario.dto.VerPerfilUsuarioDTO;
import com.findfix.find_fix_app.utils.exception.exceptions.EspecialistaExcepcion;
import com.findfix.find_fix_app.utils.exception.exceptions.EspecialistaNotFoundException;
import com.findfix.find_fix_app.utils.exception.exceptions.UserNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/especialista")
@RequiredArgsConstructor
public class EspecialistaController {
    private final EspecialistaService especialistaService;


    @GetMapping
    public ResponseEntity<Map<String, Object>> obtenerEspecialistas() throws EspecialistaNotFoundException {
        Map<String, Object> response = new HashMap<>();
        List<Especialista> especialistas = especialistaService.obtenerEspecialistas();

        response.put("Mensaje", "Lista de especialistas encontrada☑️");
        response.put("Especialistas: ", especialistas.stream().map(EspecialistaFichaCompletaDTO::new).toList());

        return ResponseEntity.ok(response);
    }


    @GetMapping("/disponibles")
    public ResponseEntity<Map<String, Object>> obtenerEspecialistasDisponibles() throws EspecialistaNotFoundException {
        Map<String, Object> response = new HashMap<>();
        List<Especialista> especialistas = especialistaService.obtenerEspecialistasDisponibles();

        response.put("Mensaje", "Lista de especialistas disponibles encontrada☑️");
        response.put("Especialistas: ", especialistas.stream().map(EspecialistaListadoDTO::new).toList());

        return ResponseEntity.ok(response);
    }


    @PatchMapping("/{email}")
    public ResponseEntity<Map<String, Object>> actualizarEspecialistaAdmin(@PathVariable String email, @Valid @RequestBody ActualizarEspecialistaDTO dto) throws EspecialistaExcepcion, EspecialistaNotFoundException {
        Map<String, Object> response = new HashMap<>();

        Especialista especialista = especialistaService.actualizarEspecialistaAdmin(email, dto);
        response.put("Mensaje", "Especialista actualizado correctamente☑️");
        response.put("Especialista actualizado: ", new EspecialistaRespuestaDTO(especialista));

        return ResponseEntity.ok(response);
    }

    @PatchMapping
    public ResponseEntity<Map<String, Object>> actualizarEspecialista(@Valid @RequestBody ActualizarEspecialistaDTO dto) throws UserNotFoundException, EspecialistaExcepcion, EspecialistaNotFoundException, UserNotFoundException {
        Map<String, Object> response = new HashMap<>();

        Especialista especialista = especialistaService.actualizarEspecialista(dto);
        response.put("Mensaje", "Especialista actualizado correctamente☑️");
        response.put("Actualizacion: ", new EspecialistaRespuestaDTO(especialista));

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{email}")
    public ResponseEntity<Map<String, Object>> eliminarPorEmail(@PathVariable String email) throws EspecialistaNotFoundException {
        Map<String, Object> response = new HashMap<>();

        especialistaService.eliminarPorEmail(email);
        response.put("Mensaje", "Especialista eliminado correctamente✅");

        return ResponseEntity.ok(response);
    }


    @PatchMapping("/oficios/{email}")
    public ResponseEntity<Map<String, Object>> actualizarOficiosDeEspecialistaAdmin(@PathVariable String email, @Valid @RequestBody ActualizarOficioEspDTO dto) throws EspecialistaExcepcion, EspecialistaNotFoundException {
        Map<String, Object> response = new HashMap<>();

        Especialista especialista = especialistaService.actualizarOficioDeEspecialistaAdmin(email, dto);
        response.put("Mensaje", "Oficios actualizados correctamente☑️");
        response.put("Especialista actualizado: ", new EspecialistaRespuestaDTO(especialista));

        return ResponseEntity.ok(response);
    }

    @PatchMapping("/actualizar-oficios")
    public ResponseEntity<Map<String, Object>> actualizarOficiosDeEspecialista(@Valid @RequestBody ActualizarOficioEspDTO dto) throws UserNotFoundException, EspecialistaExcepcion, EspecialistaNotFoundException {
        Map<String, Object> response = new HashMap<>();

        Especialista especialista = especialistaService.actualizarOficioDeEspecialista(dto);
        response.put("Mensaje", "Oficios actualizados correctamente☑️");
        response.put("Actualizacion", new EspecialistaRespuestaDTO(especialista));

        return ResponseEntity.ok(response);
    }

    @GetMapping("/ver-perfil")
    public ResponseEntity<Map<String, VerPerfilEspecialistaDTO>> verPerfilEspecialista() throws UserNotFoundException, EspecialistaNotFoundException {
        Map<String, VerPerfilEspecialistaDTO> response = new HashMap<>();
        VerPerfilEspecialistaDTO especialistaDTO = especialistaService.verPerfilEspecialista();
        response.put("Perfil:", especialistaDTO);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/filtrar")
    public ResponseEntity<Map<String, Object>> filtrarEspecialistas(@RequestBody BuscarEspecialistaDTO filtro)
            throws EspecialistaExcepcion {

        List<EspecialistaFichaCompletaDTO> especialistasFiltrados = especialistaService.filtrarEspecialistas(filtro);

        Map<String, Object> response = new HashMap<>();
        response.put("Mensaje", "Lista de especialistas encontrada ☑️");
        response.put("Especialista/s: ", especialistasFiltrados);

        return ResponseEntity.ok(response);
    }

}
