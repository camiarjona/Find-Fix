package com.findfix.find_fix_app.especialista.controller;

import com.findfix.find_fix_app.especialista.dto.ActualizarEspecialistaDTO;
import com.findfix.find_fix_app.especialista.dto.ActualizarOficioEspDTO;
import com.findfix.find_fix_app.especialista.model.Especialista;
import com.findfix.find_fix_app.especialista.service.EspecialistaService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.SecondaryTable;
import jakarta.validation.Valid;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.Response;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.parameters.P;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/especialista")
@RequiredArgsConstructor
public class EspecialistaController {
    private final EspecialistaService especialistaService;


    @PostMapping
    public ResponseEntity<String> guardar(@Valid @RequestBody Especialista especialista){
        try {
            especialistaService.guardar(especialista);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body("Especialista registrado exitosamente!");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(e.getMessage());
        }
    }

    @GetMapping
    public ResponseEntity<List<Especialista>> obtenerEspecialistas(){
        List<Especialista>especialistas = especialistaService.obtenerEspecialistas();
        return ResponseEntity.ok(especialistas);
    }

    @GetMapping("/id/{id}")
    public ResponseEntity<Especialista> buscarPorId(@PathVariable Long id){
        return especialistaService.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/dni/{dni}")
    public ResponseEntity<Especialista> buscarPorDni(@PathVariable Long dni){
        return especialistaService.buscarPorDni(dni)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{dni}")
    public ResponseEntity<?> actualizar(@PathVariable Long dni, @Valid @RequestBody ActualizarEspecialistaDTO dto){
        try {
            Especialista especialista = especialistaService.actualizar(dni, dto);
            return ResponseEntity.status(HttpStatus.OK).body(especialista);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Especialista no encontrado");
        }
    }

    @DeleteMapping("/{dni}")
    public ResponseEntity<String>eliminar(@PathVariable Long dni){
        try{
            especialistaService.eliminar(dni);
            return ResponseEntity.ok("El especialista se eliminó correctamente");
        }catch (IllegalArgumentException e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("El especialista no se encontro");
        }
    }


    @PatchMapping("/{dni}/oficios")
    public ResponseEntity<Especialista> modificarOficiosDeEspecialista(@PathVariable Long dni, @Valid @RequestBody ActualizarOficioEspDTO dto){
        try{
            Especialista especialista = especialistaService.modificarOficioDeEspecialista(dni, dto);
            return  ResponseEntity.ok(especialista);
        }catch (EntityNotFoundException e){
            return ResponseEntity.notFound().build();
        }catch(IllegalArgumentException e){
            return ResponseEntity.badRequest().body(null);
        }
    }



}
