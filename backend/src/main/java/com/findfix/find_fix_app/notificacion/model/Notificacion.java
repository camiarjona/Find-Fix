package com.findfix.find_fix_app.notificacion.model;

import com.findfix.find_fix_app.usuario.model.Usuario;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "notificaciones")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Notificacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String titulo;

    @Column(nullable = false) // length por defecto es 255, podemos poner 500.
    private String mensaje;

    @Column(name = "rol_destinatario")
    private String rolDestinatario; // Guardaremos "CLIENTE", "ESPECIALISTA" o "ADMIN"

    private boolean leida = false;

    private LocalDateTime fechaCreacion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_usuario")
    private Usuario usuario;


    
    public String getRolDestinatario() {
        return rolDestinatario;
    }

    public void setRolDestinatario(String rolDestinatario) {
        this.rolDestinatario = rolDestinatario;
    }
}