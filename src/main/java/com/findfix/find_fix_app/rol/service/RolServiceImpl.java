package com.findfix.find_fix_app.rol.service;

import com.findfix.find_fix_app.utils.exception.exceptions.RolException;
import com.findfix.find_fix_app.utils.exception.exceptions.RolNotFoundException;
import com.findfix.find_fix_app.rol.model.Rol;
import com.findfix.find_fix_app.rol.repository.RolRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RolServiceImpl implements RolService {

    private final RolRepository rolRepository;

 ///  guardar nuevo rol  en el sistema
    @Override
    @Transactional(rollbackFor = Exception.class)
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
    @Transactional(readOnly = true)
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
    @Transactional(rollbackFor = Exception.class)
    public void eliminarRol(String nombre) throws RolNotFoundException {
        nombre = nombre.toUpperCase();
        Rol encontrado = rolRepository.findByNombre(nombre).orElseThrow(() -> new RolNotFoundException("El rol que desea eliminar no existe."));
        rolRepository.delete(encontrado);
    }
    
   ///  filtro de roles por nombre
    @Override
    @Transactional(readOnly = true)
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
