package com.findfix.find_fix_app.rol.controller;

import com.findfix.find_fix_app.utils.exception.exceptions.RolException;
import com.findfix.find_fix_app.utils.exception.exceptions.RolNotFoundException;
import com.findfix.find_fix_app.rol.model.Rol;
import com.findfix.find_fix_app.rol.service.RolService;
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
@RequestMapping("/roles")
@RequiredArgsConstructor
@Validated
public class RolController {

    private final RolService rolService;


    @PostMapping
    public ResponseEntity<Map<String, Object>> crearRol(@Valid @RequestBody Rol rol) throws RolException {
        rolService.guardarRol(rol);
        Map<String,Object> response = new HashMap<>();
        response.put("message", "Rol creado con exito.");
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<Map<String, Object>> listarRoles() throws RolException {

        Map<String,Object> response = new HashMap<>();
        List<Rol>  roles = rolService.mostrarRoles();
        response.put("message","Lista de roles registrados");
        response.put("data",roles);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{nombre}")
    public ResponseEntity<Map<String,Object>> eliminarRol(@PathVariable String nombre) throws RolNotFoundException {
        nombre = nombre.toUpperCase();
        rolService.eliminarRol(nombre);
        Map<String,Object> response = new HashMap<>();
        response.put("message","Rol eliminado con exito");
        return ResponseEntity.status(HttpStatus.OK).body(response);

    }

    @PatchMapping("/{id}")   /// http://localhost:8080/roles/3?nuevoNombre=nuevoRol
    public ResponseEntity<Map<String,Object>> modificarRol(@PathVariable Long id, @Valid @RequestParam String nuevoNombre) throws RolNotFoundException, RolException {
        rolService.modificarRol(nuevoNombre, id);
        Map<String,Object> response = new HashMap<>();
        response.put("message","Rol modificado con exito");
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping("/{rol}")
    public ResponseEntity<Map<String,Object>> filtrarPorNombre(@PathVariable("rol") String nombre) throws RolNotFoundException {
        nombre = nombre.toUpperCase();
        Rol rol = rolService.filtrarPorNombre(nombre);
        Map<String,Object> response = new HashMap<>();
        response.put("message","Rol encontrado");
        response.put("data",rol);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }


}
