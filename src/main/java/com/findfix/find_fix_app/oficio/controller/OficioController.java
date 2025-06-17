package com.findfix.find_fix_app.oficio.controller;

import com.findfix.find_fix_app.utils.exception.exceptions.OficioNotFoundException;
import com.findfix.find_fix_app.oficio.model.Oficio;
import com.findfix.find_fix_app.oficio.service.OficioService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/oficios")
@Validated
public class OficioController {

    @Autowired
    private OficioService oficioService;

    @GetMapping
    public ResponseEntity<Map<String,Object>> buscarTodos() {
        Map<String,Object> response = new HashMap<>();
        List<Oficio> oficios = oficioService.buscarTodos();
        response.put("message","Lista de oficios encontrada ✅");
        response.put("data",oficios);
        return ResponseEntity.ok(response); // 200 OK
    }

    @GetMapping("/{id}")
    public ResponseEntity<Map<String,Object>> buscarPorId(@PathVariable Long id) {
        Optional<Oficio> oficio = oficioService.buscarPorId(id);
        Map<String,Object> response = new HashMap<>();
        response.put("message","Oficio encontrado ✅ ");
        response.put("data",oficio.get());
        return ResponseEntity.ok(response);  // 200 OK si existe

    }

    @PostMapping
    public ResponseEntity<Map<String,Object>> crearOficio(@Valid @RequestBody Oficio oficio) {
        Oficio guardado = oficioService.crearOficio(oficio);
        Map<String,Object> response = new HashMap<>();
        response.put("message","Oficio registrado exitosamente ✅");
        return ResponseEntity.status(HttpStatus.CREATED).body(response); // 201 Created
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Map<String,Object>> modificarOficio(@PathVariable Long id, @Valid @RequestBody Oficio nuevo) throws OficioNotFoundException {
            oficioService.modificarOficio(id, nuevo);
            Map<String,Object> response = new HashMap<>();
            response.put("message","Oficio actualizado exitosamente ✅");
            return ResponseEntity.ok(response); // 200 OK

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

    @GetMapping("/nombre/{oficio}")
    public ResponseEntity<Map<String,Object>> filtrarPorNombre(@PathVariable("oficio") String nombre) throws OficioNotFoundException {
        nombre = nombre.toUpperCase();
        Oficio oficio = oficioService.filtrarPorNombre(nombre);
        Map<String,Object> response = new HashMap<>();
        response.put("message","Oficio encontrado ✅");
        response.put("data",oficio);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

}
