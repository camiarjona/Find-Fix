package com.findfix.find_fix_app.trabajoExterno.controller;

import com.findfix.find_fix_app.trabajoExterno.service.TrabajoExternoService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping
@RequiredArgsConstructor
public class TrabajoExternoController {
    private final TrabajoExternoService trabajoExternoService;
}
