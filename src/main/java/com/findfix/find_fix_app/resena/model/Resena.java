package com.findfix.find_fix_app.resena.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Entity
@Table(name="resenas")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Resena {

    @Id
    @GeneratedValue(strategy =GenerationType.IDENTITY)
    private Long resenaId;
    @Column(nullable = false)
    private Double puntuacion;
    @Column(nullable = false)
    private String comentario;
    /// int id_trabajo

}
