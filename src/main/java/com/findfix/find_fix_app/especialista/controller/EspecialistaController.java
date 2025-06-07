package com.findfix.find_fix_app.especialista.controller;

import com.findfix.find_fix_app.especialista.service.EspecialistaService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/")
@RequiredArgsConstructor
public class EspecialistaController {
    private final EspecialistaService especialistaService;


}
