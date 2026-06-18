package com.findfix.find_fix_app.trabajo.fotoTrabajo.service;

import org.springframework.web.multipart.MultipartFile;
import com.findfix.find_fix_app.utils.exception.exceptions.EspecialistaNotFoundException;
import java.io.IOException;
import java.util.List;

public interface FotoTrabajoService {
    void guardarFotosGaleria(String email, List<MultipartFile> fotos) throws IOException, EspecialistaNotFoundException;
    void eliminarFotoGaleria(Long fotoId, String email);
}

