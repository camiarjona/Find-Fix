package com.findfix.find_fix_app.trabajo.fotoTrabajo.controller;

import com.findfix.find_fix_app.trabajo.fotoTrabajo.service.FotoTrabajoService;
import com.findfix.find_fix_app.utils.apiResponse.ApiResponse;
import com.findfix.find_fix_app.utils.exception.exceptions.EspecialistaNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/fotos-trabajo")
@RequiredArgsConstructor
public class FotoTrabajoController {

    private final FotoTrabajoService fotoTrabajoService;

    @PostMapping("/subir-galeria")
    public ResponseEntity<ApiResponse> subirFotos(
            @RequestParam("file") List<MultipartFile> files,
            Authentication authentication) throws IOException, EspecialistaNotFoundException {

        // Usamos el nombre del usuario autenticado (el email en tu caso)
        fotoTrabajoService.guardarFotosGaleria(authentication.getName(), files);

        return ResponseEntity.ok(new ApiResponse("Se han subido " + files.size() + " imágenes a tu galería", true));
    }

    @DeleteMapping("/eliminar/{id}")
    public ResponseEntity<ApiResponse> eliminarFoto(
        @PathVariable("id") Long id,
        Authentication authentication) {
    
    fotoTrabajoService.eliminarFotoGaleria(id, authentication.getName());
    
    return ResponseEntity.ok(new ApiResponse("Foto eliminada correctamente", true));
}

}