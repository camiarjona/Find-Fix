package com.findfix.find_fix_app.resena.controller;

import com.findfix.find_fix_app.resena.dto.MostrarResenaDTO;
import com.findfix.find_fix_app.resena.dto.MostrarResenaClienteDTO;
import com.findfix.find_fix_app.resena.dto.MostrarResenaEspecialistaDTO;
import com.findfix.find_fix_app.utils.apiResponse.ApiResponse;
import com.findfix.find_fix_app.utils.exception.exceptions.*;
import com.findfix.find_fix_app.resena.dto.CrearResenaDTO;
import com.findfix.find_fix_app.resena.model.Resena;
import com.findfix.find_fix_app.resena.service.ResenaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/resena")
@RequiredArgsConstructor
public class ResenaController {

    private final ResenaService resenaService;

    @PostMapping("/registrar")
    public ResponseEntity<ApiResponse<MostrarResenaClienteDTO>> crearResena(@Valid @RequestBody CrearResenaDTO dto) throws TrabajoAppNotFoundException, UserNotFoundException {
        Resena nueva = resenaService.crearResena(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(new ApiResponse<>(
                "Reseña creada con éxito",
                new MostrarResenaClienteDTO(nueva)));
    }

    @GetMapping("/buscar/{id}")
    public ResponseEntity<ApiResponse<MostrarResenaClienteDTO>> buscarPorId(@PathVariable Long id) throws ResenaNotFoundException {
        Resena resena = resenaService.buscarPorId(id)
                .orElseThrow(() -> new ResenaNotFoundException("Resena no encontrada"));
        return ResponseEntity.ok(new ApiResponse<>("Reseña", new MostrarResenaClienteDTO(resena)));
    }

    @GetMapping("/trabajo/{titulo}")
    public ResponseEntity<ApiResponse<MostrarResenaDTO>> buscarPorTitulo(@PathVariable String titulo) throws ResenaNotFoundException, TrabajoAppNotFoundException {
        Resena resena = resenaService.buscarPorTrabajoTitulo(titulo)
                .orElseThrow(() -> new ResenaNotFoundException("Reseña no encontrada."));

        return ResponseEntity.ok(new ApiResponse<>("Reseña", new MostrarResenaDTO(resena)));
    }

    @GetMapping("/recibidas")
    @PreAuthorize("hasRole('ESPECIALISTA')")
    public ResponseEntity<ApiResponse<List<MostrarResenaEspecialistaDTO>>> verResenasDeMisTrabajos() throws UserNotFoundException, EspecialistaExcepcion, EspecialistaNotFoundException {
        List<Resena> resenasRecibidas = resenaService.resenasDeMisTrabajos();
        return ResponseEntity.ok(new ApiResponse<>(
                "Reseñas recibidas",
                resenasRecibidas.stream().map(MostrarResenaEspecialistaDTO::new).toList()));
    }

    @GetMapping("/enviadas")
    @PreAuthorize("hasRole('CLIENTE')")
    public ResponseEntity<ApiResponse<List<MostrarResenaClienteDTO>>> verResenasHechasPorMi() throws UserNotFoundException {
        List<Resena> resenasEnviadas = resenaService.resenasHechasPorMi();
        return ResponseEntity.ok(new ApiResponse<>(
                "Reseñas enviadas",
                resenasEnviadas.stream().map(MostrarResenaClienteDTO::new).toList()));
    }

    @DeleteMapping("/eliminar/{id}")
    @PreAuthorize("hasRole('CLIENTE')")
    public ResponseEntity<ApiResponse<String>> borrarResena(@PathVariable Long id) throws ResenaNotFoundException {
        resenaService.borrarResena(id);
        return ResponseEntity.ok(new ApiResponse<>("Reseña eliminada con éxito", "{}"));
    }
}