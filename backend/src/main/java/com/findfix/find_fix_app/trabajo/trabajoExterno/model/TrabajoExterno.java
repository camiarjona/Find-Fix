package com.findfix.find_fix_app.trabajo.trabajoExterno.model;

import com.findfix.find_fix_app.utils.enums.EstadosTrabajos;
import com.findfix.find_fix_app.especialista.model.Especialista;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.LocalDate;

@Entity
@Table(name="trabajos_externos")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TrabajoExterno {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long trabajoExternoId;

    @Column(nullable = false)
    private String nombreCliente;

    private LocalDate fechaInicio;

    private LocalDate fechaFin;

    private String titulo;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadosTrabajos estado;

    @Column(nullable = false)
    private String descripcion;

    @Column(nullable = false)
    private Double presupuesto;

    @ManyToOne
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "id_especialista")
    private Especialista especialista;

}
