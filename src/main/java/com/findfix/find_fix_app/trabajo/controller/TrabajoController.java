package com.findfix.find_fix_app.trabajo.controller;

import com.findfix.find_fix_app.trabajo.service.TrabajoService;
import com.findfix.find_fix_app.utils.apiResponse.ApiResponse;
import com.findfix.find_fix_app.utils.exception.exceptions.EspecialistaNotFoundException;
import com.findfix.find_fix_app.utils.exception.exceptions.TrabajoAppException;
import com.findfix.find_fix_app.utils.exception.exceptions.TrabajoExternoException;
import com.findfix.find_fix_app.utils.exception.exceptions.UserNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/trabajos")
@RequiredArgsConstructor
public class TrabajoController {
    private final TrabajoService trabajoService;

    @GetMapping
    @PreAuthorize("hasRole('ESPECIALISTA')")
    public ResponseEntity<ApiResponse<Map<String, List<?>>>> verTodosMisTrabajos() throws UserNotFoundException, TrabajoExternoException, EspecialistaNotFoundException, TrabajoAppException {
        return ResponseEntity.ok(new ApiResponse<>("Trabajos", trabajoService.verTodosMisTrabajos()));
    }
}
