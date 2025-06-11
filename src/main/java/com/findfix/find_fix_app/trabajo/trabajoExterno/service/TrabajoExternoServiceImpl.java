package com.findfix.find_fix_app.trabajo.trabajoExterno.service;

import com.findfix.find_fix_app.especialista.model.Especialista;
import com.findfix.find_fix_app.especialista.repository.EspecialistaRepository;
import com.findfix.find_fix_app.enums.EstadosTrabajos;
import com.findfix.find_fix_app.exception.exceptions.TrabajoExternoNotFoundException;
import com.findfix.find_fix_app.trabajo.trabajoExterno.dto.CrearTrabajoExternoDTO;
import com.findfix.find_fix_app.trabajo.trabajoExterno.dto.ModificarTrabajoExternoDTO;
import com.findfix.find_fix_app.trabajo.trabajoExterno.dto.TrabajoExternoDTO;
import com.findfix.find_fix_app.trabajo.trabajoExterno.model.TrabajoExterno;
import com.findfix.find_fix_app.trabajo.trabajoExterno.repository.TrabajoExternoRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TrabajoExternoServiceImpl implements TrabajoExternoService {

    @Autowired
    private TrabajoExternoRepository trabajoExternoRepository;
    @Autowired
    private EspecialistaRepository especialistaRepository;

    @Override
    @Transactional
    public TrabajoExterno crearTrabajoExterno(CrearTrabajoExternoDTO DTO) {
        Especialista especialista = especialistaRepository.findById(DTO.getEspecialistaId())
                .orElseThrow(() -> new EntityNotFoundException("Especialista no encontrado con ID: " + DTO.getEspecialistaId()));

        TrabajoExterno trabajo = new TrabajoExterno();
        trabajo.setNombreCliente(DTO.getNombreCliente());
        trabajo.setDescripcion(DTO.getDescripcion());
        trabajo.setPresupuesto(DTO.getPresupuesto());
        trabajo.setEspecialista(especialista);
        trabajo.setEstado(EstadosTrabajos.CREADO);
        trabajo.setFechaInicio(null);
        trabajo.setFechaFin(null);

        return trabajoExternoRepository.save(trabajo);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TrabajoExternoDTO> buscarTodos() {
        return trabajoExternoRepository.findAll().stream()
                .map(TrabajoExternoDTO::new)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<TrabajoExternoDTO> buscarPorId(Long id) {
        return trabajoExternoRepository.findById(id)
                .map(TrabajoExternoDTO::new);
    }

    @Override
    @Transactional
    public TrabajoExterno modificarTrabajoExterno(Long id, ModificarTrabajoExternoDTO DTO) throws TrabajoExternoNotFoundException {
        TrabajoExterno trabajo = trabajoExternoRepository.findById(id)
                .orElseThrow(() -> new TrabajoExternoNotFoundException("Trabajo externo no encontrado con ID: " + id));

        EstadosTrabajos estadoActual = trabajo.getEstado();
        EstadosTrabajos nuevoEstado = DTO.getEstado();

        if (!estadoActual.equals(nuevoEstado)) {
            trabajo.setEstado(nuevoEstado);

            if (estadoActual == EstadosTrabajos.CREADO && nuevoEstado == EstadosTrabajos.EN_PROCESO) {
                trabajo.setFechaInicio(LocalDate.now());
                trabajo.setFechaFin(null);
            } else if (estadoActual == EstadosTrabajos.EN_PROCESO && nuevoEstado == EstadosTrabajos.FINALIZADO) {
                trabajo.setFechaFin(LocalDate.now());
            }
        }

        return trabajoExternoRepository.save(trabajo);
    }

    @Override
    @Transactional
    public void borrarTrabajoExternoPorId(Long id) {
        TrabajoExterno trabajo = trabajoExternoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Trabajo externo no encontrado con ID: " + id));
        trabajoExternoRepository.delete(trabajo);
    }
}