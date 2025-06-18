package com.findfix.find_fix_app.oficio.controller;

import com.findfix.find_fix_app.utils.apiResponse.ApiResponse;
import com.findfix.find_fix_app.utils.exception.exceptions.OficioException;
import com.findfix.find_fix_app.utils.exception.exceptions.OficioNotFoundException;
import com.findfix.find_fix_app.oficio.model.Oficio;
import com.findfix.find_fix_app.oficio.service.OficioService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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

    private final OficioService oficioService;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<Oficio>>> buscarTodos() {
        List<Oficio> oficios = oficioService.buscarTodos();
        return ResponseEntity.ok(new ApiResponse<>("Lista de oficios encontrada ✅",oficios));
    }

    @GetMapping("/disponibles")
    @PreAuthorize("hasRole('ESPECIALISTA')")
    public ResponseEntity<ApiResponse<List<String>>> oficiosDisponibles() {
        List<Oficio> disponibles = oficioService.buscarTodos();
        return ResponseEntity.ok(new ApiResponse<>("Oficios disponibles",  disponibles.stream().map(Oficio::getNombre).toList()));
    }

    @GetMapping("/buscar/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Oficio>> buscarPorId(@PathVariable Long id) {
        Optional<Oficio> oficio = oficioService.buscarPorId(id);
        return ResponseEntity.ok(new ApiResponse<>("Oficio encontrado ✅",oficio.get()));
    }

    @PostMapping("/agregar")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<String>> crearOficio(@Valid @RequestBody Oficio oficio) throws OficioException {
        Oficio guardado = oficioService.crearOficio(oficio);
        return ResponseEntity.status(HttpStatus.CREATED).body(new ApiResponse<>("Oficio registrado exitosamente ✅","Ingrese a la lista de oficios para verificar su registro")); // 201 Created
    }

    @PatchMapping("/actualizar/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<String>> modificarOficio(@PathVariable Long id, @Valid @RequestBody Oficio nuevo) throws OficioNotFoundException {
            oficioService.modificarOficio(id, nuevo);
            return ResponseEntity.ok(new ApiResponse<>("Oficio actualizado exitosamente ✅","Filtre el oficio modificado para  verificar los cambios")); // 200 OK

    }

    @DeleteMapping("/eliminar/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<String>> borrarOficioPorId(@PathVariable Long id) throws OficioNotFoundException {
       oficioService.borrarOficioPorId(id);
       return ResponseEntity.ok(new ApiResponse<>("Oficio eliminado exitosamente ✅","Ingrese a la lista de oficios para verificar la eliminacion del registro"));
    }

    @GetMapping("/nombre/{oficio}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Oficio>> filtrarPorNombre(@PathVariable("oficio") String nombre) throws OficioNotFoundException {
        nombre = nombre.toUpperCase();
        Oficio oficio = oficioService.filtrarPorNombre(nombre);
        return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse<>("Oficio encontrado ✅",oficio));
    }

}
