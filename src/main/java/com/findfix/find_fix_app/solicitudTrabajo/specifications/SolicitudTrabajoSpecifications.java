package com.findfix.find_fix_app.solicitudTrabajo.specifications;

import com.findfix.find_fix_app.utils.enums.EstadosSolicitudes;
import com.findfix.find_fix_app.solicitudTrabajo.model.SolicitudTrabajo;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;

public class SolicitudTrabajoSpecifications {

    //criteria builder genera las condiciones del WHERE en la consulta

    public static Specification<SolicitudTrabajo> fechaEntre(LocalDate desde, LocalDate hasta) {
        return (root, query, criteriaBuilder) -> {
            if (desde == null && hasta == null) return null;
            if(desde != null && hasta != null && !desde.isBefore(hasta)) {
                //BETWEEN sql, muestra todas las solicitudes entre desde y hasta
             return criteriaBuilder.between(root.get("fechaCreacion"), desde, hasta);
            } else if(desde != null) {
                //fechaCreacion >= desde, muestra todas las solicitudes posteriores al desde
                return criteriaBuilder.greaterThanOrEqualTo(root.get("fechaCreacion"), desde);
            } else {
                //fechaCreacion <= hasta, muestra todas las solicitudes anteriores al hasta
                return criteriaBuilder.lessThanOrEqualTo(root.get("fechaCreacion"), hasta);
            }
        };
    }

    public static Specification<SolicitudTrabajo> estadoEs(EstadosSolicitudes estado) {
        return (root, query, criteriaBuilder) ->  {
            if (estado == null) return null;
            return criteriaBuilder.equal(root.get("estado"), estado);
        };
    }

}
