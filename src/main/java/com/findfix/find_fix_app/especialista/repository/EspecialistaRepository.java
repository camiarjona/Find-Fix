package com.findfix.find_fix_app.especialista.repository;

import com.findfix.find_fix_app.especialista.model.Especialista;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EspecialistaRepository extends JpaRepository<Especialista, Long> {
}
