package com.findfix.find_fix_app.solicitudTrabajo.model;

import com.findfix.find_fix_app.utils.enums.EstadosSolicitudes;
import com.findfix.find_fix_app.especialista.model.Especialista;
import com.findfix.find_fix_app.usuario.model.Usuario;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "solicitudes_trabajos")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SolicitudTrabajo {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_solicitud_trabajo")
    private Long solicitudTrabajoId;

    @Column(nullable = false)
    private LocalDate fechaCreacion;

    private LocalDate fechaResolucion;

    @Column(nullable = false)
    private EstadosSolicitudes estado;

    @Column(nullable = false)
    private String descripcion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_usuario")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Usuario usuario;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_especialista")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Especialista especialista;

}
