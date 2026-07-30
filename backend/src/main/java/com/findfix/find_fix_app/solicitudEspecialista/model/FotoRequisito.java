package com.findfix.find_fix_app.solicitudEspecialista.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "fotos_requisitos")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FotoRequisito {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_foto_requisito")
    private Long id;

    @Column(nullable = false)
    private String url;

    @Column(name = "public_id", nullable = false)
    private String publicId; // Clave para borrar de Cloudinary si el admin rechaza la solicitud

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_solicitud_especialista", nullable = false)
    private SolicitudEspecialista solicitud;
}