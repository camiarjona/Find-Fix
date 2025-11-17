package com.findfix.find_fix_app.trabajo.service;

import com.findfix.find_fix_app.trabajo.trabajoApp.dto.VisualizarTrabajoAppEspecialistaDTO;
import com.findfix.find_fix_app.trabajo.trabajoApp.model.TrabajoApp;
import com.findfix.find_fix_app.trabajo.trabajoApp.service.TrabajoAppService;
import com.findfix.find_fix_app.trabajo.trabajoExterno.dto.TrabajoExternoDTO;
import com.findfix.find_fix_app.trabajo.trabajoExterno.model.TrabajoExterno;
import com.findfix.find_fix_app.trabajo.trabajoExterno.service.TrabajoExternoService;
import com.findfix.find_fix_app.utils.exception.exceptions.EspecialistaNotFoundException;
import com.findfix.find_fix_app.utils.exception.exceptions.TrabajoAppException;
import com.findfix.find_fix_app.utils.exception.exceptions.TrabajoExternoException;
import com.findfix.find_fix_app.utils.exception.exceptions.UsuarioNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class TrabajoServiceImpl implements TrabajoService{

    private final TrabajoAppService trabajoAppService;
    private final TrabajoExternoService trabajoExternoService;

    @Override
    public Map<String, List<?>> verTodosMisTrabajos() throws UsuarioNotFoundException, EspecialistaNotFoundException, TrabajoAppException, TrabajoExternoException {
        List<TrabajoApp> trabajosApp = trabajoAppService.obtenerTrabajosEspecialista();
        List<TrabajoExterno> trabajosExternos = trabajoExternoService.obtenerMisTrabajos();

        Map<String, List<?>> misTrabajos = new HashMap<>();
        misTrabajos.put("Trabajos de la app", trabajosApp.stream().map(VisualizarTrabajoAppEspecialistaDTO::new).toList());
        misTrabajos.put("Trabajos externos", trabajosExternos.stream().map(TrabajoExternoDTO::new).toList());

        return misTrabajos;
    }
}
