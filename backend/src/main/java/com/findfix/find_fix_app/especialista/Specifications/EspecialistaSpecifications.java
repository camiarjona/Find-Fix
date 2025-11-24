package com.findfix.find_fix_app.especialista.Specifications;


import com.findfix.find_fix_app.especialista.model.Especialista;
import com.findfix.find_fix_app.utils.enums.CiudadesDisponibles;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

public class EspecialistaSpecifications {

        public static Specification<Especialista> tieneId(Long id) {
            return (root, query, cb) -> id != null ? cb.equal(root.get("especialistaId"), id) : null;
        }

        public static Specification<Especialista> tieneOficio(String nombreOficio) {
            return (root, query, cb) -> {
                if (nombreOficio == null || nombreOficio.isEmpty()) return null;
                return cb.equal(root.join("oficios").get("nombre"), nombreOficio);
            };
        }

        public static Specification<Especialista> enCiudad(CiudadesDisponibles ciudad) {
            return (root, query, cb) -> ciudad != null ? cb.equal(root.get("usuario").get("ciudad"), ciudad) : null;
        }

        public static Specification<Especialista> tieneDni(Long dni) {
            return (root, query, cb) -> dni != null ? cb.equal(root.get("dni"), dni) : null;
        }

        public static Specification<Especialista> tieneEmail(String email) {
            return (root, query, cb) -> email != null ? cb.equal(root.get("usuario").get("email"), email) : null;
        }

        public static Specification<Especialista> tieneCalificacionMinima(Double minCalificacion) {
            return (root, query, cb) -> minCalificacion != null ?
                    cb.greaterThanOrEqualTo(root.get("calificacionPromedio"), minCalificacion) :
                    null;
        }

    public static Specification<Especialista> tieneDatosCompletos() {
        return (root, query, criteriaBuilder) -> {
            // Join inner con oficios (filtra especialistas que tienen al menos un oficio)
            root.join("oficios", JoinType.INNER);

            // Evitar duplicados por múltiples oficios
            query.distinct(true);

            // Predicados para validar que telefono no sea nulo ni vacío ni solo espacios
            Predicate telefonoNoNulo = criteriaBuilder.isNotNull(root.get("usuario").get("telefono"));
            Predicate telefonoNoVacio = criteriaBuilder.notEqual(
                    criteriaBuilder.trim(root.get("usuario").get("telefono")),
                    ""
            );

            // Predicado para que ciudad no sea nulo
            Predicate ciudadNoNula = criteriaBuilder.isNotNull(root.get("usuario").get("ciudad"));

            // Combinar todos los predicados con AND
            return criteriaBuilder.and(telefonoNoNulo, telefonoNoVacio, ciudadNoNula);
        };
    }
}

