package com.findfix.find_fix_app.rol.repository;

import com.findfix.find_fix_app.rol.model.Rol;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RolRepository extends JpaRepository<Rol, Long> {
   // Optional<Rol> findByName(String name);
}
