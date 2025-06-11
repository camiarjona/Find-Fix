package com.findfix.find_fix_app.trabajo.trabajoExterno.service;

import com.findfix.find_fix_app.exception.exceptions.OficioNotFoundException;
import com.findfix.find_fix_app.exception.exceptions.TrabajoExternoNotFoundException;
import com.findfix.find_fix_app.trabajo.trabajoExterno.dto.CrearTrabajoExternoDTO;
import com.findfix.find_fix_app.trabajo.trabajoExterno.dto.ModificarTrabajoExternoDTO;
import com.findfix.find_fix_app.trabajo.trabajoExterno.dto.TrabajoExternoDTO;
import com.findfix.find_fix_app.trabajo.trabajoExterno.model.TrabajoExterno;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public interface TrabajoExternoService {
    TrabajoExterno crearTrabajoExterno(CrearTrabajoExternoDTO DTO);
    List<TrabajoExternoDTO> buscarTodos();
    Optional<TrabajoExternoDTO> buscarPorId(Long id);
    TrabajoExterno modificarTrabajoExterno(Long id, ModificarTrabajoExternoDTO DTO) throws OficioNotFoundException, TrabajoExternoNotFoundException;
    void borrarTrabajoExternoPorId(Long id);
}
