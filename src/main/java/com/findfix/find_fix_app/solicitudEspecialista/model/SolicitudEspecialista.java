package com.findfix.find_fix_app.solicitudEspecialista.model;

import com.findfix.find_fix_app.enums.EstadosSolicitudes;
import com.findfix.find_fix_app.usuario.model.Usuario;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(name = "solicitudes_especialistas")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SolicitudEspecialista {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_solicitud_especialista")
    private Long seId;
    @Column(nullable = false)
    //momento en el que se envia la solicitud
    private LocalDate fechaSolicitud;
    @Column(nullable = false)
    //momento en el que la solicitud es aceptada o rechazada
    private LocalDate fechaResolucion;
    @Column(nullable = false)
    private EstadosSolicitudes estado;
    @Column(nullable = false)
    private String motivo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_usuario")
    private Usuario usuario;

}
