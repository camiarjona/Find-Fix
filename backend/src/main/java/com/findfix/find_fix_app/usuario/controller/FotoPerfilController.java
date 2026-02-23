package com.findfix.find_fix_app.usuario.controller;

import com.findfix.find_fix_app.servicesGenerales.CloudinaryService;
import com.findfix.find_fix_app.usuario.model.Usuario;
import com.findfix.find_fix_app.usuario.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.Map;

@RestController
@RequestMapping("/api/usuarios/foto")
@CrossOrigin(origins = "*")
public class FotoPerfilController {

    @Autowired
    private CloudinaryService cloudinaryService;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Value("${cloudinary.folder_name}")
    private String folderName;

    @Value("${cloudinary.preset_name}")
    private String presetName;

@PostMapping("/subir/{id}")
@Transactional
public ResponseEntity<?> subirFoto(@PathVariable Long id, @RequestParam("file") MultipartFile file) throws IOException {

    Usuario usuario = usuarioRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

    // 1. Subimos la imagen con el parámetro de moderación
    Map resultado = cloudinaryService.subirImagen(file, folderName, presetName);

    // 2. Extraemos el estado de la moderación de la IA
    // Cloudinary devuelve una lista de moderaciones, buscamos la de aws_rek
    java.util.List<Map> moderations = (java.util.List<Map>) resultado.get("moderation");
    
    if (moderations != null && !moderations.isEmpty()) {
        String status = (String) moderations.get(0).get("status");
        
        if ("rejected".equalsIgnoreCase(status)) {
            // Si la IA la rechaza, la borramos de Cloudinary inmediatamente para no ocupar espacio
            cloudinaryService.eliminarImagen(resultado.get("public_id").toString());
            
            // Retornamos un error 400 avisando al usuario
            return ResponseEntity.badRequest().body(Map.of(
                "error", "IMAGEN_INAPROPIADA",
                "message", "La imagen contiene contenido inapropiado y no puede ser utilizada."
            ));
        }
    }

    // 3. Si pasó la moderación (status approved o pending), procedemos normal
    if (usuario.getFotoId() != null) {
        cloudinaryService.eliminarImagen(usuario.getFotoId());
    }

    usuario.setFotoUrl(resultado.get("secure_url").toString());
    usuario.setFotoId(resultado.get("public_id").toString());
    usuarioRepository.saveAndFlush(usuario);

    return ResponseEntity.ok(Map.of("url", usuario.getFotoUrl(), "message", "Foto actualizada con éxito"));

    }

@DeleteMapping("/eliminar/{id}")
@Transactional
public ResponseEntity<?> eliminarFoto(@PathVariable Long id) throws IOException {
    Usuario usuario = usuarioRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

    // Solo intentamos borrar de Cloudinary si el ID no es nulo ni está vacío
    if (usuario.getFotoId() != null && !usuario.getFotoId().isBlank()) {
        try {
            cloudinaryService.eliminarImagen(usuario.getFotoId());
        } catch (Exception e) {
            // Logueamos el error pero permitimos que siga para limpiar la DB
            System.out.println("Error al borrar en Cloudinary (quizás ya no existía): " + e.getMessage());
        }
    }

    // Limpiamos los campos en la base de datos sí o sí
    usuario.setFotoUrl(null);
    usuario.setFotoId(null);
    usuarioRepository.saveAndFlush(usuario);

    return ResponseEntity.ok(Map.of("message", "Foto eliminada con éxito"));
}
}    
