package com.findfix.find_fix_app.rol.controller;

import com.findfix.find_fix_app.exception.exceptions.RolException;
import com.findfix.find_fix_app.exception.exceptions.RolNotFoundException;
import com.findfix.find_fix_app.rol.model.Rol;
import com.findfix.find_fix_app.rol.service.RolService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/roles")
@RequiredArgsConstructor
@Validated
public class RolController {

    private final RolService rolService;


    @PostMapping
    public ResponseEntity<String> crearRol(@Valid @RequestBody Rol rol) throws RolException {
        rolService.guardarRol(rol);
        return ResponseEntity.status(HttpStatus.CREATED).body("Rol creado con exito." + "\n" + "ID: " + rol.getRolId() + "\n" + "Nombre: " + rol.getNombre());
    }

    @GetMapping
    public ResponseEntity<List<Rol>> listarRoles() throws RolException {
        return ResponseEntity.ok(rolService.mostrarRoles());
    }

    @DeleteMapping("/{nombre}")
    public ResponseEntity<String> eliminarRol(@PathVariable String nombre) throws RolNotFoundException {
        rolService.eliminarRol(nombre);
        return ResponseEntity.status(HttpStatus.OK).body("Rol eliminado con exito");

    }

    @PutMapping("/{id}")   /// http://localhost:8080/roles/3?nuevoNombre=nuevoRol
    public ResponseEntity<String> modificarRol(@PathVariable Long id, @RequestParam String nuevoNombre) throws RolNotFoundException, RolException {
        rolService.modificarRol(nuevoNombre, id);
        return ResponseEntity.status(HttpStatus.OK).body("Rol modificado con exito");
    }


}
