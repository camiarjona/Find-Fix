package com.findfix.find_fix_app.trabajo.fotoTrabajo.service;

import com.findfix.find_fix_app.especialista.model.Especialista;
import com.findfix.find_fix_app.especialista.repository.EspecialistaRepository;
import com.findfix.find_fix_app.servicesGenerales.CloudinaryService;
import com.findfix.find_fix_app.trabajo.fotoTrabajo.model.FotoTrabajo;
import com.findfix.find_fix_app.trabajo.fotoTrabajo.repository.FotoTrabajoRepository;
import com.findfix.find_fix_app.utils.exception.exceptions.EspecialistaNotFoundException;

import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class FotoTrabajoServiceImpl implements FotoTrabajoService {

    private final FotoTrabajoRepository fotoTrabajoRepository;
    private final EspecialistaRepository especialistaRepository;

    @Autowired
    private CloudinaryService cloudinaryService;

    @Override
    @Transactional
    public void guardarFotosGaleria(String email, List<MultipartFile> fotos) throws IOException, EspecialistaNotFoundException {

        Especialista especialista = especialistaRepository.findByUsuarioEmail(email)
                .orElseThrow(() -> new EspecialistaNotFoundException("Especialista no encontrado para el email: " + email));

        for (MultipartFile file : fotos) {
            
            Map result = cloudinaryService.subirImagen(file, "findfix/galeria");

            FotoTrabajo foto = new FotoTrabajo();
            foto.setUrl(result.get("secure_url").toString()); 
            foto.setPublicId(result.get("public_id").toString());
            foto.setEspecialista(especialista);

            fotoTrabajoRepository.save(foto);
        }
    }

    @Transactional
    public void eliminarFotoGaleria(Long fotoId, String email) {

    FotoTrabajo foto = fotoTrabajoRepository.findById(fotoId)
            .orElseThrow(() -> new RuntimeException("La foto no existe"));

    if (!foto.getEspecialista().getUsuario().getEmail().equals(email)) {
        throw new RuntimeException("No tienes permiso para eliminar esta foto");
    }

    try {

        cloudinaryService.eliminarImagen(foto.getPublicId());
        
        fotoTrabajoRepository.delete(foto);
    } catch (IOException e) {
        throw new RuntimeException("Error al eliminar la imagen de Cloudinary: " + e.getMessage());
    }
}

}