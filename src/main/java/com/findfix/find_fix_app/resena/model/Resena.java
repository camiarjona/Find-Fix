package com.findfix.find_fix_app.resena.model;

import com.findfix.find_fix_app.trabajo.trabajoApp.model.TrabajoApp;
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
    @Column(name = "id_resena")
    private Long resenaId;
    @Column(nullable = false)
    private Double puntuacion;
    @Column(nullable = false)
    private String comentario;

    @OneToOne(mappedBy = "trabajo",fetch = FetchType.LAZY)
    @JoinColumn(name = "id_trabajo", nullable = false, unique = true)
    private TrabajoApp trabajo;

}
