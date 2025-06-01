package com.findfix.find_fix_app.trabajoExterno.model;

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
    Long trabajoExternoId;
    @Column(nullable = false)
    String nombre_cliente;
    @Column(nullable = false)
    LocalDate fechaInicio;
    @Column(nullable = false)
    LocalDate fechaFin;
    @Column(nullable = false)
    EstadosTrabajos estado;
    @Column(nullable = false)
    String descripcion;
    /// Long especialistaId;
}
