package com.findfix.find_fix_app.usuario.specifications;

import com.findfix.find_fix_app.rol.model.Rol;
import com.findfix.find_fix_app.usuario.model.Usuario;
import jakarta.persistence.criteria.Join;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;

public class UsuarioSpecifications {

    // filtra segun un rol especifico
    public static Specification<Usuario> tieneRol(String nombreRol) {
        return (root, query, criteriaBuilder) -> {
            if (nombreRol == null) return null;
            //realiza un join entre ambas tablas
            Join<Usuario, Rol> roles = root.join("roles");
            //WHERE nombre =?, muestra todos los usuarios que coinciden con el rol
            return criteriaBuilder.equal(roles.get("nombre"), nombreRol);
        };
    }

    // filtra segun uno o varios roles
    public static Specification<Usuario> tieneAlgunRol(List<String> nombresRoles) {
        return (root, query, criteriaBuilder) -> {
            if (nombresRoles == null || nombresRoles.isEmpty()) return null;
            Join<Usuario, Rol> roles = root.join("roles");
            // WHERE ... IN..., muestra todos los usuarios que coinciden con algún rol
            return roles.get("nombre").in(nombresRoles);
        };
    }

    //filtra segun email
    public static Specification<Usuario> tieneEmail(String email) {
        return (root, query, criteriaBuilder) -> {
            if (email == null) return null;
            //WHERE email =?, muestra el usuario que coincide con el email
            return criteriaBuilder.equal(root.get("email"), email);
        };
    }

    //filtra segun id
    public static Specification<Usuario> tieneId(Long id) {
        return (root, query, criteriaBuilder) -> {
            if (id == null) return null;
            //WHERE id =?, muestra el usuario que coincide con el id
            return criteriaBuilder.equal(root.get("id"), id);
        };
    }
}
