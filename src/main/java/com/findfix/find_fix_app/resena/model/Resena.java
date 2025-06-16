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

    @NotNull(message = "La puntuación no puede ser nula")
    @DecimalMin(value = "1.0", message = "La puntuación mínima es 1")
    @DecimalMax(value = "5.0", message = "La puntuación máxima es 5")
    @Column(nullable = false)
    private Double puntuacion;

    @NotBlank(message = "El comentario no puede estar vacío")
    @Size(min = 5, max = 500, message = "El comentario debe tener entre 5 y 500 caracteres")
    @Column(nullable = false, length = 500)
    private String comentario;

    @OneToOne
    @JoinColumn(name = "trabajo_id", nullable = false, unique = true)
    @NotNull(message = "La reseña debe estar asociada a un trabajo")
    private TrabajoApp trabajoApp;
}
