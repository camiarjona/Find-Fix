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
@RequestMapping("/oficios")
@RequiredArgsConstructor
public class OficioController {

    @Autowired
    private OficioService oficioService;

    @GetMapping
    public ResponseEntity<List<Oficio>> findAll() {
        List<Oficio> oficios = oficioService.findAll();
        return ResponseEntity.ok(oficios); // 200 OK
    }

    @GetMapping("/{id}")
    public ResponseEntity<Oficio> findById(@PathVariable Long id) {
        Optional<Oficio> oficio = oficioService.findById(id);
        return oficio.map(ResponseEntity::ok) // 200 OK si existe
                .orElseGet(() -> ResponseEntity.notFound().build()); // 404 si no existe
    }

    @PostMapping
    public ResponseEntity<Oficio> saveOficio(@RequestBody Oficio oficio) {
        Oficio guardado = oficioService.saveOficio(oficio);
        return ResponseEntity.status(HttpStatus.CREATED).body(guardado); // 201 Created
    }

    @PutMapping("/{id}")
    public ResponseEntity<Oficio> updateOficio(@PathVariable Long id, @RequestBody Oficio oficio) {
        try {
            Oficio actualizado = oficioService.updateOficio(id, oficio);
            return ResponseEntity.ok(actualizado); // 200 OK
        } catch (OficioNotFoundException e) {
            return ResponseEntity.notFound().build(); // 404 Not Found
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteOficio(@PathVariable Long id) {
        Optional<Oficio> oficio = oficioService.findById(id);
        if (oficio.isPresent()) {
            oficioService.delete(id);
            return ResponseEntity.noContent().build(); // 204 No Content
        } else {
            return ResponseEntity.notFound().build(); // 404 Not Found
        }
    }

}
