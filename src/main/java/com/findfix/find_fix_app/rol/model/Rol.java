package com.findfix.find_fix_app.rol.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "roles")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Rol {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long rolId;
    @NotBlank(message = "El nombre es obligatorio")
    @NotEmpty(message = "El nombre no puede estar vacio")
    @Size(min = 4, max = 20, message = "El nombre debe tener entre 3 y 20 caracteres")
    @Pattern(regexp = "^[a-zA-Z]+$", message = "El nombre solo debe contener letras")
    @Column(nullable = false)
    private String nombre;


}
