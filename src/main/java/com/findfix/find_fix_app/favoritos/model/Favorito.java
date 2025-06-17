package com.findfix.find_fix_app.favoritos.model;

import com.findfix.find_fix_app.especialista.model.Especialista;
import com.findfix.find_fix_app.usuario.model.Usuario;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "favoritos")
@Data
@NoArgsConstructor
public class Favorito {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Usuario usuario;

    @ManyToOne
    private Especialista especialista;

    public Favorito(Usuario usuario, Especialista especialista) {
        this.usuario = usuario;
        this.especialista = especialista;
    }
}
