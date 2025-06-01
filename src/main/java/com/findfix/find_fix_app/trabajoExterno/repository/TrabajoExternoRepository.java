package com.findfix.find_fix_app.trabajoExterno.repository;

import com.findfix.find_fix_app.trabajoExterno.model.TrabajoExterno;
import org.hibernate.sql.ast.tree.expression.JdbcParameter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TrabajoExternoRepository extends JpaRepository<TrabajoExterno,Long> {
}
