package com.findfix.find_fix_app.especialista.model;

import com.findfix.find_fix_app.oficio.model.Oficio;
import com.findfix.find_fix_app.usuario.model.Usuario;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Fetch;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "especialistas")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Especialista {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_especialista")
    private Long especialistaId;

    @Column(unique = true)
    private Long dni;

    @Column(length = 250)
    private String descripcion;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_usuario")
    @NotNull(message = "El usuario no puede ser nulo")
    private Usuario usuario;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name ="oficios_especialistas",
            joinColumns = @JoinColumn(name = "id_especialista"),
            inverseJoinColumns = @JoinColumn(name = "id_oficio")
    )
    private Set<Oficio>oficios = new HashSet<>();






}
