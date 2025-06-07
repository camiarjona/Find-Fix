package com.findfix.find_fix_app.rol.service;

import com.findfix.find_fix_app.exception.exceptions.RolException;
import com.findfix.find_fix_app.exception.exceptions.RolNotFoundException;
import com.findfix.find_fix_app.rol.model.Rol;
import com.findfix.find_fix_app.rol.repository.RolRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RolServiceImpl implements RolService {

    private final RolRepository rolRepository;


    @Override
    public void guardarRol(Rol rol)  throws RolException{

        if (rolRepository.findByNombre(rol.getNombre()).isPresent()) {
            throw new RolException("El rol ingresado ya esta registrado en el sistema");
        } else {
            rolRepository.save(rol);

        }

    }

    @Override
    public List<Rol> mostrarRoles() throws RolException{
        List<Rol> roles = rolRepository.findAll();
        if(roles.isEmpty())
        {
            throw new RolException("La lista de roles se encuentra vacia");
        }else {
            return roles;
        }

    }

    @Override
    public void eliminarRol(String nombre) throws RolNotFoundException {
        Rol encontrado = rolRepository.findByNombre(nombre).orElseThrow(() -> new RolNotFoundException("El rol que desea eliminar no existe."));
        rolRepository.delete(encontrado);
    }

    @Override
    public void modificarRol(String nombreNuevo, Long idBuscada) throws RolException,RolNotFoundException {
        Rol encontrado = rolRepository.findById(idBuscada).orElseThrow(() -> new RolNotFoundException("El rol que desea modificar no existe."));
        encontrado.setNombre(nombreNuevo);
        Optional<Rol> verificacion = rolRepository.findByNombre(nombreNuevo);
        if(verificacion.isPresent())
        {
            throw new RolException("El nombre ingresado ya pertenece a un rol del sistema.");
        }

        rolRepository.save(encontrado);
    }
}
