package com.findfix.find_fix_app.solicitudEspecialista.controller;

import com.findfix.find_fix_app.solicitudEspecialista.service.SolicitudEspecialistaService;
import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/")
@RequiredArgsConstructor
public class SolicitudEspecialistaController {
    private final SolicitudEspecialistaService solicitudEspecialistaService;

}
