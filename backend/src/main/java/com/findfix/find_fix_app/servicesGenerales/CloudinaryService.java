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

    public Map subirImagen(MultipartFile archivo, String carpeta, String preset) throws IOException {
        Map opciones = ObjectUtils.asMap(
            "folder", carpeta,
                "upload_preset", preset,
                "resource_type", "image"
        );

        return cloudinary.uploader().upload(archivo.getBytes(), opciones);
    }

    /**
     * Elimina una imagen de la nube para no acumular basura
     * @param publicId El ID que tenemos guardado en la base de datos (fotoId)
     */
    public Map eliminarImagen(String publicId) throws IOException {
        return cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());
    }
}