package com.findfix.find_fix_app.resena.controller;

import com.findfix.find_fix_app.resena.service.ResenaService;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/")
@RequiredArgsConstructor
public class ResenaController {
    private final ResenaService resenaService;
}
