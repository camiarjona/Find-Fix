package com.findfix.find_fix_app.trabajo.trabajoApp.model;

import com.findfix.find_fix_app.enums.EstadosTrabajos;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(name="trabajos_app")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TrabajoApp {
    @Id
    @GeneratedValue (strategy = GenerationType.IDENTITY)
    private Long trabajoAppId;
    /// Long especialistaId;
    /// Long clienteID;
    @Column(nullable = false)
    private LocalDate fechaInicio;
    @Column(nullable = false)
    private EstadosTrabajos estado;
    @Column(nullable = false)
    private String descripcion;
    @Column(nullable = false)
    private LocalDate fechaFin;

    private Double presupuesto;
    /// Long solicitudId;
}
