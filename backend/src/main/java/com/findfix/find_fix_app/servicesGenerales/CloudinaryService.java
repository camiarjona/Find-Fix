package com.findfix.find_fix_app.servicesGenerales;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@Service
public class CloudinaryService {

    @Autowired
    private Cloudinary cloudinary;

    /**
     * Sube una imagen a Cloudinary
     * @param archivo El archivo que viene del frontend (ngx-dropzone)
     * @param carpeta Nombre de la carpeta en Cloudinary (ej: "usuarios_findfix")
     * @param preset Nombre del Upload Preset (ej: "preset_findfix")
     */
    public Map subirImagen(MultipartFile archivo, String carpeta, String preset) throws IOException {
        Map opciones = ObjectUtils.asMap(
            "folder", carpeta,
                "upload_preset", preset,
                "resource_type", "image",
                "moderation", "aws_rek"
        );

        return cloudinary.uploader().upload(archivo.getBytes(), opciones);
    }

    /**
     * @param publicId
     */
    public Map eliminarImagen(String publicId) throws IOException {
        return cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());
    }
}