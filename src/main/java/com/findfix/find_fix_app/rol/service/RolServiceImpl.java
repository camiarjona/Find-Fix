package com.findfix.find_fix_app.rol.service;

import com.findfix.find_fix_app.utils.exception.exceptions.RolException;
import com.findfix.find_fix_app.utils.exception.exceptions.RolNotFoundException;
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

 ///  guardar nuevo rol  en el sistema
    @Override
    public void guardarRol(Rol rol)  throws RolException{
        String nombre = rol.getNombre();
        rol.setNombre(nombre.toUpperCase());

        if (rolRepository.findByNombre(rol.getNombre()).isPresent()) {
            throw new RolException("El rol ingresado ya esta registrado en el sistema");
        } else {
            rolRepository.save(rol);
        }
    }
  ///  mostrar lista de roles registrados en el sistema
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
   ///  eliminar un rol registrado del sistema
    @Override
    public void eliminarRol(String nombre) throws RolNotFoundException {
        nombre = nombre.toUpperCase();
        Rol encontrado = rolRepository.findByNombre(nombre).orElseThrow(() -> new RolNotFoundException("El rol que desea eliminar no existe."));
        rolRepository.delete(encontrado);
    }
    ///  modificar un rol ya registrado en el sistema buscandolo por su id
    @Override
    public void modificarRol(String nombreNuevo, Long idBuscada) throws RolException,RolNotFoundException {
        nombreNuevo = nombreNuevo.toUpperCase();
        Rol encontrado = rolRepository.findById(idBuscada).orElseThrow(() -> new RolNotFoundException("El rol que desea modificar no existe."));
        encontrado.setNombre(nombreNuevo);
        Optional<Rol> verificacion = rolRepository.findByNombre(nombreNuevo);
        if(verificacion.isPresent())
        {
            throw new RolException("El nombre ingresado ya pertenece a un rol del sistema.");
        }

        rolRepository.save(encontrado);
    }
   ///  filtro de roles por nombre
    @Override
    public Rol filtrarPorNombre(String nombreBuscado) throws RolNotFoundException {
        Optional<Rol> encontrado = rolRepository.findByNombre(nombreBuscado);
        if(encontrado.isEmpty())
        {
            throw new RolNotFoundException("El rol que intenta buscar no esta registrado en el sistema");
        }else
        {
            return encontrado.get();
        }
    }
}
