package com.findfix.find_fix_app.resena.controller;

import com.findfix.find_fix_app.exception.exceptions.ResenaNotFoundException;
import com.findfix.find_fix_app.exception.exceptions.SpecialistRequestNotFoundException;
import com.findfix.find_fix_app.exception.exceptions.TrabajoAppNotFoundException;
import com.findfix.find_fix_app.exception.exceptions.UserNotFoundException;
import com.findfix.find_fix_app.resena.dto.CrearResenaDTO;
import com.findfix.find_fix_app.resena.dto.MostrarResenaDTO;
import com.findfix.find_fix_app.resena.model.Resena;
import com.findfix.find_fix_app.resena.service.ResenaService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/resenas")
@AllArgsConstructor
public class ResenaController {

    @Autowired
    private ResenaService resenaService;

    @PostMapping
    public ResponseEntity<Resena> crearResena(@Valid @RequestBody CrearResenaDTO dto) throws TrabajoAppNotFoundException {
        Resena nueva = resenaService.crearResena(dto);
        return ResponseEntity.ok(nueva);
    }

    @GetMapping
    public ResponseEntity<List<MostrarResenaDTO>> obtenerTodas() {
        return ResponseEntity.ok(resenaService.buscarTodos());
    }

    @GetMapping("/trabajo/{id}")
    public ResponseEntity<Resena> buscarPorTrabajoId(@PathVariable Long id) throws ResenaNotFoundException {
        return ResponseEntity.ok(resenaService.buscarPorTrabajoId(id).get());
    }

    @GetMapping("/titulo")
    public ResponseEntity<Resena> buscarPorTitulo(@RequestParam String titulo) throws ResenaNotFoundException, TrabajoAppNotFoundException {
        return ResponseEntity.ok(resenaService.buscarPorTrabajoTitulo(titulo).get());
    }

    @GetMapping("/mis-trabajos")
    public ResponseEntity<List<CrearResenaDTO>> verResenasDeMisTrabajos()
            throws UserNotFoundException, SpecialistRequestNotFoundException {
        return ResponseEntity.ok(resenaService.ResenasDeMisTrabajos());
    }

    @GetMapping("/mis-resenas")
    public ResponseEntity<List<CrearResenaDTO>> verResenasHechasPorMi()
            throws UserNotFoundException {
        return ResponseEntity.ok(resenaService.ResenasHechasPorMi());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> borrarResena(@PathVariable Long id) throws ResenaNotFoundException {
        resenaService.borrarResena(id);
        return ResponseEntity.ok("Reseña eliminada con éxito.");
    }
}