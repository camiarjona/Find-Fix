package com.findfix.find_fix_app.trabajo.trabajoExterno.controller;

import com.findfix.find_fix_app.exception.exceptions.OficioNotFoundException;
import com.findfix.find_fix_app.exception.exceptions.TrabajoExternoNotFoundException;
import com.findfix.find_fix_app.trabajo.trabajoExterno.dto.CrearTrabajoExternoDTO;
import com.findfix.find_fix_app.trabajo.trabajoExterno.dto.ModificarTrabajoExternoDTO;
import com.findfix.find_fix_app.trabajo.trabajoExterno.dto.TrabajoExternoDTO;
import com.findfix.find_fix_app.trabajo.trabajoExterno.model.TrabajoExterno;
import com.findfix.find_fix_app.trabajo.trabajoExterno.service.TrabajoExternoService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/trabajosExternos")
public class TrabajoExternoController {

    @Autowired
    private TrabajoExternoService trabajoExternoService;

    @PostMapping
    public ResponseEntity<Map<String, String>> crearTrabajo(@Valid @RequestBody CrearTrabajoExternoDTO dto) {
        trabajoExternoService.crearTrabajoExterno(dto);
        Map<String, String> response = new HashMap<>();
        response.put("mensaje: ", "Trabajo externo creado con éxito✅.");
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<TrabajoExternoDTO>> obtenerTodos() {
        return ResponseEntity.ok(trabajoExternoService.buscarTodos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<TrabajoExternoDTO> obtenerPorId(@PathVariable Long id) {
        return trabajoExternoService.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Map<String, String>> modificarTrabajo(@PathVariable Long id, @Valid @RequestBody ModificarTrabajoExternoDTO dto) throws TrabajoExternoNotFoundException, OficioNotFoundException {
        trabajoExternoService.modificarTrabajoExterno(id, dto);
        Map<String, String> response = new HashMap<>();
        response.put("mensaje: ", "Trabajo externo actualizado con éxito☑\uFE0F.");
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> eliminarTrabajo(@PathVariable Long id) {
        trabajoExternoService.borrarTrabajoExternoPorId(id);
        Map<String, String> response = new HashMap<>();
        response.put("mensaje: ", "Trabajo externo eliminado con éxito☑\uFE0F.");
        return ResponseEntity.ok(response);
    }
}