package com.findfix.find_fix_app.trabajo.trabajoExterno.model;

import com.findfix.find_fix_app.enums.EstadosTrabajos;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.cglib.core.Local;

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
    @Column(nullable = false)
    private LocalDate fechaInicio;
    @Column(nullable = false)
    private LocalDate fechaFin;
    @Column(nullable = false)
    private EstadosTrabajos estado;
    @Column(nullable = false)
    private String descripcion;

    private Double presupuesto;
    /// Long especialistaId;
}
