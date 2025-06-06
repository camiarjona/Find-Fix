package com.findfix.find_fix_app.solicitudTrabajo.controller;

import com.findfix.find_fix_app.solicitudTrabajo.service.SolicitudTrabajoService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/")
@RequiredArgsConstructor
public class SolicitudTrabajoController {
    private final SolicitudTrabajoService solicitudTrabajoService;
}
