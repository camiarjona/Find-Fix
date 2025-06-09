package com.findfix.find_fix_app.rol.service;

import com.findfix.find_fix_app.exception.exceptions.RolException;
import com.findfix.find_fix_app.exception.exceptions.RolNotFoundException;
import com.findfix.find_fix_app.rol.model.Rol;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface RolService {
    void guardarRol(Rol rol) throws RolException;
    List<Rol> mostrarRoles()throws RolException;
    void eliminarRol(String nombre) throws RolNotFoundException;
    void modificarRol(String nombreNuevo,Long idBuscada) throws RolNotFoundException,RolException;
    Rol filtrarPorNombre(String nombreBuscado) throws RolNotFoundException;

}
