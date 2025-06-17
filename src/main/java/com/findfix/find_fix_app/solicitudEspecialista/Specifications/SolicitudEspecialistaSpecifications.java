package com.findfix.find_fix_app.solicitudEspecialista.Specifications;

import com.findfix.find_fix_app.utils.enums.EstadosSolicitudes;
import com.findfix.find_fix_app.solicitudEspecialista.model.SolicitudEspecialista;
import com.findfix.find_fix_app.usuario.model.Usuario;
import jakarta.persistence.criteria.Join;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;

public class SolicitudEspecialistaSpecifications {

    public static Specification<SolicitudEspecialista> tieneEstado(EstadosSolicitudes estado) {
        return (root, query, criteriaBuilder) -> {
            if (estado == null) return null;
            return criteriaBuilder.equal(root.get("estado"), estado);
        };
    }

    public static Specification<SolicitudEspecialista> tieneFechaSolicitud(LocalDate fechaSolicitud) {
        return (root, query, criteriaBuilder) -> {
            if (fechaSolicitud == null) return null;
            return criteriaBuilder.equal(root.get("fechaSolicitud"), fechaSolicitud);
        };
    }

    public static Specification<SolicitudEspecialista> tieneFechaSolicitudEntre(LocalDate inicio, LocalDate fin) {
        return (root, query, criteriaBuilder) -> {
            if (inicio == null || fin == null) return null;
            return criteriaBuilder.between(root.get("fechaSolicitud"), inicio, fin);
        };
    }

    public static Specification<SolicitudEspecialista> tieneUsuarioEmail(String email) {
        return (root, query, criteriaBuilder) -> {
            if (email == null) return null;
            Join<SolicitudEspecialista, Usuario> usuario = root.join("usuario");
            return criteriaBuilder.equal(usuario.get("email"), email);
        };
    }

    public static Specification<SolicitudEspecialista> tieneId(Long id) {
        return (root, query, criteriaBuilder) -> {
            if (id == null) return null;
            return criteriaBuilder.equal(root.get("id"), id);
        };
    }

}
