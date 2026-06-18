package com.findfix.find_fix_app.trabajo.fotoTrabajo.model;

import com.findfix.find_fix_app.especialista.model.Especialista;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.EqualsAndHashCode;

@Entity
@Table(name = "foto_trabajo")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FotoTrabajo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String url;

    @Column(name = "public_id", nullable = false, columnDefinition = "TEXT")
    private String publicId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "especialista_id")
    @JsonIgnore
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Especialista especialista;
}