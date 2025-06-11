package com.findfix.find_fix_app.solicitudEspecialista.model;

import com.findfix.find_fix_app.enums.EstadosSolicitudes;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Table (name = "solicitudes_especialistas")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SolicitudEspecialista {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long seId;
   @Column(nullable = false)
   private LocalDate fechaSolicitud;
   @Column(nullable = false)
   private LocalDate fechaResolucion;
   @Column(nullable = false)
   private EstadosSolicitudes estado;
   @Column(nullable = false)
   private String motivo;


}
