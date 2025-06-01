package com.findfix.find_fix_app.trabajoApp.controller;

import com.findfix.find_fix_app.trabajoApp.service.TrabajoAppService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/")
@RequiredArgsConstructor
public class TrabajoAppController {
    private final TrabajoAppService trabajoAppService;
}
