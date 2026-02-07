package com.findfix.find_fix_app.barrio.controller;

import com.findfix.find_fix_app.barrio.dto.BarrioDto;
import com.findfix.find_fix_app.barrio.service.BarrioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/barrios")
@CrossOrigin(origins = "http://localhost:4200")
public class BarrioController {

    @Autowired
    private BarrioService barrioService;

    @GetMapping
    public ResponseEntity<List<BarrioDto>> getBarrios(@RequestParam(defaultValue = "mdp") String ciudad) {
        return ResponseEntity.ok(barrioService.obtenerBarrios(ciudad));
    }
}