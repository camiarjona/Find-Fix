package com.findfix.find_fix_app.usuario.service;

import com.findfix.find_fix_app.favoritos.repository.FavoritoRepository;
import com.findfix.find_fix_app.solicitudEspecialista.model.SolicitudEspecialista;
import com.findfix.find_fix_app.solicitudEspecialista.repository.SolicitudEspecialistaRepository;
import com.findfix.find_fix_app.solicitudTrabajo.model.SolicitudTrabajo;
import com.findfix.find_fix_app.solicitudTrabajo.repository.SolicitudTrabajoRepository;
import com.findfix.find_fix_app.trabajo.trabajoApp.model.TrabajoApp;
import com.findfix.find_fix_app.trabajo.trabajoApp.repository.TrabajoAppRepository;
import com.findfix.find_fix_app.usuario.model.Usuario;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UsuarioDesvinculacionService {

    private final TrabajoAppRepository trabajoAppRepository;
    private final SolicitudEspecialistaRepository solicitudEspecialistaRepository;
    private final SolicitudTrabajoRepository solicitudTrabajoRepository;
    private final FavoritoRepository favoritoRepository;

    @Transactional(rollbackFor = Exception.class)
    public void desvincularUsuario(Usuario usuario) {
        // Desvincular trabajos
        List<TrabajoApp> trabajos = trabajoAppRepository.findByUsuario(usuario);
        trabajos.forEach(trabajo -> trabajo.setUsuario(null));
        trabajoAppRepository.saveAll(trabajos);

        // Desvincular solicitudes especialista
        List<SolicitudEspecialista> solicitudesEspecialista = solicitudEspecialistaRepository.findByUsuario(usuario);
        solicitudesEspecialista.forEach(solicitud -> solicitud.setUsuario(null));
        solicitudEspecialistaRepository.saveAll(solicitudesEspecialista);

        // Desvincular solicitudes trabajo
        List<SolicitudTrabajo> solicitudesTrabajo = solicitudTrabajoRepository.findByUsuario(usuario);
        solicitudesTrabajo.forEach(solicitud -> solicitud.setUsuario(null));
        solicitudTrabajoRepository.saveAll(solicitudesTrabajo);

        //eliminamos los favoritos del usuario
        favoritoRepository.deleteByUsuario(usuario);
    }
}
