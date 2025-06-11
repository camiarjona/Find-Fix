package com.findfix.find_fix_app.oficio.controller;

import com.findfix.find_fix_app.exception.exceptions.OficioNotFoundException;
import com.findfix.find_fix_app.oficio.model.Oficio;
import com.findfix.find_fix_app.oficio.service.OficioService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/oficios")
public class OficioController {

    @Autowired
    private OficioService oficioService;

    @GetMapping
    public ResponseEntity<List<Oficio>> buscarTodos() {
        List<Oficio> oficios = oficioService.buscarTodos();
        return ResponseEntity.ok(oficios); // 200 OK
    }

    @GetMapping("/{id}")
    public ResponseEntity<Oficio> buscarPorId(@PathVariable Long id) {
        Optional<Oficio> oficio = oficioService.buscarPorId(id);
        return oficio.map(ResponseEntity::ok) // 200 OK si existe
                .orElseGet(() -> ResponseEntity.notFound().build()); // 404 si no existe
    }

    @PostMapping
    public ResponseEntity<Oficio> crearOficio(@RequestBody Oficio oficio) {
        Oficio guardado = oficioService.crearOficio(oficio);
        return ResponseEntity.status(HttpStatus.CREATED).body(guardado); // 201 Created
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Oficio> modificarOficio(@PathVariable Long id, @RequestBody String nuevo) {
        try {
            Oficio actualizado = oficioService.modificarOficio(id, nuevo);
            return ResponseEntity.ok(actualizado); // 200 OK
        } catch (OficioNotFoundException e) {
            return ResponseEntity.notFound().build(); // 404 Not Found
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> borrarOficioPorId(@PathVariable Long id) {
        Optional<Oficio> oficio = oficioService.buscarPorId(id);
        if (oficio.isPresent()) {
            oficioService.borrarOficioPorId(id);
            return ResponseEntity.noContent().build(); // 204 No Content
        } else {
            return ResponseEntity.notFound().build(); // 404 Not Found
        }
    }

}
