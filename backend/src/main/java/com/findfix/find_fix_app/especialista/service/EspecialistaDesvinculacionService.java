package com.findfix.find_fix_app.especialista.service;

import com.findfix.find_fix_app.especialista.model.Especialista;
import com.findfix.find_fix_app.favoritos.repository.FavoritoRepository;
import com.findfix.find_fix_app.solicitudTrabajo.model.SolicitudTrabajo;
import com.findfix.find_fix_app.solicitudTrabajo.repository.SolicitudTrabajoRepository;
import com.findfix.find_fix_app.trabajo.trabajoApp.model.TrabajoApp;
import com.findfix.find_fix_app.trabajo.trabajoApp.repository.TrabajoAppRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EspecialistaDesvinculacionService {
    private final TrabajoAppRepository trabajoAppRepository;
    private final SolicitudTrabajoRepository solicitudTrabajoRepository;
    private final FavoritoRepository favoritoRepository;

    @Transactional
    public void desvincularEspecialista(Especialista especialista) {
        // Desvincular trabajos
        List<TrabajoApp> trabajos = trabajoAppRepository.findByEspecialista(especialista);
        trabajos.forEach(trabajo -> trabajo.setEspecialista(null));
        trabajoAppRepository.saveAll(trabajos);

        // Desvincular solicitudes trabajo
        List<SolicitudTrabajo> solicitudesTrabajo = solicitudTrabajoRepository.findByEspecialista(especialista);
        solicitudesTrabajo.forEach(solicitud -> solicitud.setEspecialista(null));
        solicitudTrabajoRepository.saveAll(solicitudesTrabajo);

        //eliminamos favoritos relacionados al especialista
        favoritoRepository.deleteByEspecialista(especialista);

    }
}
