package com.findfix.find_fix_app.resena.model;

import com.findfix.find_fix_app.trabajo.trabajoApp.model.TrabajoApp;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Entity
@Table(name = "resenas")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Resena {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_resena")
    private Long resenaId;

    @Column(nullable = false)
    private Double puntuacion;

    @Column(nullable = false, length = 500)
    private String comentario;

    @OneToOne
    @JoinColumn(name = "id_trabajo_app", nullable = false, unique = true)
    private TrabajoApp trabajoApp;
}
