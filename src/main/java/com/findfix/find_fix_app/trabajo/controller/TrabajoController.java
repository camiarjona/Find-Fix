package com.findfix.find_fix_app.trabajo.controller;

import com.findfix.find_fix_app.trabajo.service.TrabajoService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/")
@RequiredArgsConstructor
public class TrabajoController {
    private final TrabajoService trabajoService;

}
