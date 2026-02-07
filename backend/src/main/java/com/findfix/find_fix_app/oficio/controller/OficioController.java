package com.findfix.find_fix_app.oficio.controller;

import com.findfix.find_fix_app.utils.apiResponse.ApiResponse;
import com.findfix.find_fix_app.oficio.model.Oficio;
import com.findfix.find_fix_app.oficio.service.OficioService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/oficios")
@Validated
public class OficioController {

    private final OficioService oficioService;

    @GetMapping
    @PreAuthorize("hasAnyRole('ESPECIALISTA', 'ADMIN')")
    public ResponseEntity<ApiResponse<List<Oficio>>> buscarTodos() {
        List<Oficio> oficios = oficioService.buscarTodos();
        return ResponseEntity.ok(new ApiResponse<>("Lista de oficios encontrada ✅",oficios));
    }

    @GetMapping("/disponibles")
    @PreAuthorize("hasAnyRole('ESPECIALISTA', 'CLIENTE')")
    public ResponseEntity<ApiResponse<List<String>>> oficiosDisponibles() {
        List<Oficio> disponibles = oficioService.buscarTodos();
        return ResponseEntity.ok(new ApiResponse<>("Oficios disponibles",  disponibles.stream().map(Oficio::getNombre).toList()));
    }
}
