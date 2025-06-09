package com.findfix.find_fix_app.especialista.repository;

import com.findfix.find_fix_app.especialista.model.Especialista;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EspecialistaRepository extends JpaRepository<Especialista, Long> {
    Optional<Especialista> findByDni(Long dni);
    boolean existsByDni(Long dni);
    void deleteByDni(Long dni);
}
