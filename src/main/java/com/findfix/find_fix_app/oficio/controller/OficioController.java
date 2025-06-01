package com.findfix.find_fix_app.oficio.controller;

import com.findfix.find_fix_app.oficio.service.OficioService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/")
@RequiredArgsConstructor
public class OficioController {

    private final OficioService oficioService;
}
