package com.findfix.find_fix_app.rol.controller;

import com.findfix.find_fix_app.utils.apiResponse.ApiResponse;
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

    ///  endpoint para crear rol nuevo en el sistema
    @PostMapping
    public ResponseEntity<ApiResponse<String>> crearRol(@Valid @RequestBody Rol rol) throws RolException {
        rolService.guardarRol(rol);
        return ResponseEntity.status(HttpStatus.CREATED).body(new ApiResponse<>("Rol creado con exito ☑️","Consulte la lista de roles para visualizar el rol agregado"));
    }
  ///  endpoint que muestra la lista de roles registrados
    @GetMapping
    public ResponseEntity<ApiResponse<List<Rol>>> listarRoles() throws RolException {
        List<Rol>  roles = rolService.mostrarRoles();
        return ResponseEntity.ok(new ApiResponse<>("Lista de roles registrados",roles));
    }
   ///  endpoint para eliminar un rol del sistema a traves de su nombre
    @DeleteMapping("/{nombre}")
    public ResponseEntity<ApiResponse<String>> eliminarRol(@PathVariable String nombre) throws RolNotFoundException {
        nombre = nombre.toUpperCase();
        rolService.eliminarRol(nombre);
        return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse<>("Rol eliminado con exito ☑️","Consulte la lista de roles registrados si desea verificar que fue eliminado"));

    }
     ///  endpoint para modificar rol, se busca por id
    @PatchMapping("/{id}")   /// http://localhost:8080/roles/3?nuevoNombre=nuevoRol
    public ResponseEntity<ApiResponse<String>> modificarRol(@PathVariable Long id, @Valid @RequestParam String nuevoNombre) throws RolNotFoundException, RolException {
        rolService.modificarRol(nuevoNombre, id);
        return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse<>("Rol modificado con exito ☑️","Verifique el cambio buscando el rol que haya modificado"));
    }
   ///  endpoint para obtener un rol filtrandolo por su nombre
    @GetMapping("/{rol}")
    public ResponseEntity<ApiResponse<Rol>> filtrarPorNombre(@PathVariable("rol") String nombre) throws RolNotFoundException {
        nombre = nombre.toUpperCase();
        Rol rol = rolService.filtrarPorNombre(nombre);
        return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse<>("Rol encontrado ☑️",rol));
    }


}
