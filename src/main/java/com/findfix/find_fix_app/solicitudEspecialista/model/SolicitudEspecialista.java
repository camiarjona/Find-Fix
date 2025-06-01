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
    Long seId;
   ///  Long usuarioId
   @Column(nullable = false)
    LocalDate fechaSolicitud;
   @Column(nullable = false)
    LocalDate fechaResolucion;
   @Column(nullable = false)
   EstadosSolicitudes estado;
   @Column(nullable = false)
    String descripcion;

}
