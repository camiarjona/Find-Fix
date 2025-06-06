package com.findfix.find_fix_app.trabajo.trabajoExterno.service;

import com.findfix.find_fix_app.trabajo.trabajoExterno.repository.TrabajoExternoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TrabajoExternoServiceImpl implements TrabajoExternoService {

    private final TrabajoExternoRepository trabajoExternoRepository;
}
