package com.findfix.find_fix_app.usuario.controller;

import com.findfix.find_fix_app.usuario.service.CloudinaryService;
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
@CrossOrigin(origins = "*") // Ajustar según tu configuración de Angular
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
        // 1. Buscamos al usuario en Neon
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        // 2. Si ya tenía una foto, la borramos de Cloudinary para no ocupar espacio
        if (usuario.getFotoId() != null) {
            cloudinaryService.eliminarImagen(usuario.getFotoId());
        }

        // 3. Subimos la nueva imagen
        Map resultado = cloudinaryService.subirImagen(file, folderName, presetName);

        // 4. Actualizamos el usuario con la nueva URL e ID
        usuario.setFotoUrl(resultado.get("secure_url").toString());
        usuario.setFotoId(resultado.get("public_id").toString());

        // Forzamos el guardado y capturamos el retorno
        Usuario resultadoFinal = usuarioRepository.saveAndFlush(usuario); 

        System.out.println(">>> Intentando persistir ID: " + usuario.getUsuarioId());
        System.out.println(">>> URL a guardar: " + usuario.getFotoUrl());

        System.out.println("DEBUG: ¿Se guardó en BD? URL final: " + resultadoFinal.getFotoUrl());

        usuarioRepository.flush(); // Esto obliga a que si hay un error de base de datos, salte YA.

        return ResponseEntity.ok(Map.of("url", usuario.getFotoUrl(), "message", "Foto actualizada con éxito"));
    }

    @DeleteMapping("/eliminar/{id}")
    @Transactional
    public ResponseEntity<?> eliminarFoto(@PathVariable Long id) throws IOException {
    // 1. Buscamos al usuario
    Usuario usuario = usuarioRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

    // 2. Si tiene fotoId, la borramos de Cloudinary
    if (usuario.getFotoId() != null) {
        cloudinaryService.eliminarImagen(usuario.getFotoId());
    }

    // 3. Limpiamos los campos en la base de datos
    usuario.setFotoUrl(null);
    usuario.setFotoId(null);

    usuarioRepository.save(usuario);

    return ResponseEntity.ok(Map.of("message", "Foto eliminada con éxito"));
}
}