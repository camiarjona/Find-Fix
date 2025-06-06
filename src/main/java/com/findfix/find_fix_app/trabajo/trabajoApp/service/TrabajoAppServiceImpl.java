package com.findfix.find_fix_app.trabajo.trabajoApp.service;

import com.findfix.find_fix_app.trabajo.trabajoApp.repository.TrabajoAppRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TrabajoAppServiceImpl implements TrabajoAppService{

    private final TrabajoAppRepository trabajoAppRepository;

}
