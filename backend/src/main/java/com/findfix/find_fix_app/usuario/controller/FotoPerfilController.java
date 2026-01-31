package com.findfix.find_fix_app.usuario.controller;

import com.findfix.find_fix_app.usuario.service.CloudinaryService;
import com.findfix.find_fix_app.usuario.model.Usuario;
import com.findfix.find_fix_app.usuario.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

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

        usuarioRepository.save(usuario);

        return ResponseEntity.ok(Map.of("url", usuario.getFotoUrl(), "message", "Foto actualizada con éxito"));
    }
}