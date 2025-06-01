package com.findfix.find_fix_app.trabajoApp.repository;

import com.findfix.find_fix_app.trabajoApp.model.TrabajoApp;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TrabajoAppRepository extends JpaRepository<TrabajoApp,Long> {
}
