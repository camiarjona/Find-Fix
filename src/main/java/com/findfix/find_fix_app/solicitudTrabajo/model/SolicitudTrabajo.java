package com.findfix.find_fix_app.solicitudTrabajo.model;

import com.findfix.find_fix_app.enums.EstadosSolicitudes;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(name="solicitudes_trabajos")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SolicitudTrabajo {
    @Id
    @GeneratedValue (strategy = GenerationType.IDENTITY)
    private Long stId;
    /// Long especialistaId
    /// Long clienteId
    @Column(nullable = false)
    private LocalDate fechaCreacion;
    @Column(nullable = false)
    private EstadosSolicitudes estado;
    @Column(nullable = false)
    private String descripcion;
}
