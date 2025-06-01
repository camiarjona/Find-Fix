package com.findfix.find_fix_app.resena.repository;

import com.findfix.find_fix_app.resena.model.Resena;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ResenaRepository extends JpaRepository<Resena,Long> {
}
