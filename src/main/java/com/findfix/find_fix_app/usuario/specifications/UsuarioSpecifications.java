package com.findfix.find_fix_app.usuario.specifications;

import com.findfix.find_fix_app.rol.model.Rol;
import com.findfix.find_fix_app.usuario.model.Usuario;
import jakarta.persistence.criteria.Join;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;

public class UsuarioSpecifications {

    public static Specification<Usuario> tieneRol(String nombreRol) {
        return (root, query, criteriaBuilder) -> {
            if (nombreRol == null) return null;
            Join<Usuario, Rol> roles = root.join("roles");
            return criteriaBuilder.equal(roles.get("nombre"), nombreRol);
        };
    }

    public static Specification<Usuario> tieneAlgunRol(List<String> nombresRoles) {
        return (root, query, criteriaBuilder) -> {
            if (nombresRoles == null || nombresRoles.isEmpty()) return null;
            Join<Usuario, Rol> roles = root.join("roles");
            return roles.get("nombre").in(nombresRoles);
        };
    }

    public static Specification<Usuario> tieneEmail(String email) {
        return (root, query, criteriaBuilder) -> {
            if (email == null) return null;
            return criteriaBuilder.equal(root.get("email"), email);
        };
    }

    public static Specification<Usuario> tieneId(Long id) {
        return (root, query, criteriaBuilder) -> {
            if (id == null) return null;
            return criteriaBuilder.equal(root.get("id"), id);
        };
    }
}
