package com.findfix.find_fix_app.barrio.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.findfix.find_fix_app.barrio.dto.BarrioDto;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.List;

@Service
public class BarrioService {
    private final ObjectMapper objectMapper = new ObjectMapper();

    public List<BarrioDto> obtenerBarrios(String ciudad) {
        String archivoRuta = "data/barrios_mdp.json";

        try {
            ClassPathResource resource = new ClassPathResource(archivoRuta);
            InputStream inputStream = resource.getInputStream();
            return objectMapper.readValue(inputStream, new TypeReference<List<BarrioDto>>(){});
        } catch (IOException e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }
}
