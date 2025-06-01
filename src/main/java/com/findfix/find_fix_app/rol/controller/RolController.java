package com.findfix.find_fix_app.rol.controller;

import com.findfix.find_fix_app.rol.service.RolService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/")
@RequiredArgsConstructor
public class RolController {

    private final RolService rolService;

}
