package com.findfix.find_fix_app.trabajo.trabajoApp.model;

import com.findfix.find_fix_app.utils.enums.EstadosTrabajos;
import com.findfix.find_fix_app.especialista.model.Especialista;
import com.findfix.find_fix_app.resena.model.Resena;
import com.findfix.find_fix_app.solicitudTrabajo.model.SolicitudTrabajo;
import com.findfix.find_fix_app.usuario.model.Usuario;
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
    @Column(name = "id_trabajo_app")
    private Long trabajoAppId;

    private LocalDate fechaInicio;

    @Column(unique = true)
    private String titulo;

    @Column(nullable = false)
    private EstadosTrabajos estado;

    private String descripcion;

    private LocalDate fechaFin;

    private Double presupuesto;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_especialista")
    private Especialista especialista;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_usuario")
    private Usuario usuario;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_solicitud_trabajo")
    private SolicitudTrabajo solicitudTrabajo;

    @OneToOne(mappedBy = "trabajoApp", cascade = CascadeType.ALL, orphanRemoval = true)
    private Resena  resena;
}
