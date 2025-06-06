package com.findfix.find_fix_app.oficio.service;

import com.findfix.find_fix_app.oficio.repository.OficioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OficioServiceImpl implements OficioService {
    private final OficioRepository oficioRepository;
}
