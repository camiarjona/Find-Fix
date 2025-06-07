package com.findfix.find_fix_app.especialista.service;

import com.findfix.find_fix_app.especialista.repository.EspecialistaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EspecialistaServiceImpl implements EspecialistaService {

    private final EspecialistaRepository especialistaRepository;


}
