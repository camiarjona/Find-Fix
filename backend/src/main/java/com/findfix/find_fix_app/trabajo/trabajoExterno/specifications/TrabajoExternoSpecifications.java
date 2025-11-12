package com.findfix.find_fix_app.trabajo.trabajoExterno.specifications;

import com.findfix.find_fix_app.utils.enums.EstadosTrabajos;
import com.findfix.find_fix_app.trabajo.trabajoExterno.model.TrabajoExterno;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;

public class TrabajoExternoSpecifications {

    public static Specification<TrabajoExterno> estadoEs(EstadosTrabajos estado) {
        return (root, query, criteriaBuilder) ->  {
            if (estado == null) return null;
            return criteriaBuilder.equal(root.get("estado"), estado);
        };
    }

    public static Specification<TrabajoExterno>tituloEs(String titulo) {
        return (root, query, criteriaBuilder) -> {
            if (titulo == null) return null;
            return criteriaBuilder.equal(criteriaBuilder.lower(root.get("titulo")), titulo.toLowerCase());
        };
    }

    public static Specification<TrabajoExterno> idEs(Long id) {
        return (root, query, criteriaBuilder) -> {
            if (id == null) return null;
            return criteriaBuilder.equal(root.get("id"), id);
        };
    }

    public static Specification<TrabajoExterno> fechaEntre(LocalDate desde, LocalDate hasta) {
        return (root, query, criteriaBuilder) -> {
            if (desde == null && hasta == null) return null;
            if(desde != null && hasta != null && !desde.isBefore(hasta)) {
                //BETWEEN sql, muestra todas las solicitudes entre desde y hasta
                return criteriaBuilder.between(root.get("fechaInicio"), desde, hasta);
            } else if(desde != null) {
                //fechaInicio >= desde, muestra todas las solicitudes posteriores al desde
                return criteriaBuilder.greaterThanOrEqualTo(root.get("fechaInicio"), desde);
            } else {
                //fechaInicio <= hasta, muestra todas las solicitudes anteriores al hasta
                return criteriaBuilder.lessThanOrEqualTo(root.get("fechaInicio"), hasta);
            }
        };
    }
}
