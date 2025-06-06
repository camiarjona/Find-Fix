package com.findfix.find_fix_app.trabajo.service;

import com.findfix.find_fix_app.trabajo.trabajoApp.repository.TrabajoAppRepository;
import com.findfix.find_fix_app.trabajo.trabajoExterno.repository.TrabajoExternoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TrabajoServiceImpl implements TrabajoService{

    private final TrabajoAppRepository trabajoAppRepository;
    private final TrabajoExternoRepository trabajoExternoRepository;

}
