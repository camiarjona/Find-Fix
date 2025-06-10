package com.findfix.find_fix_app.especialista.service;

import com.findfix.find_fix_app.especialista.dto.ActualizarEspecialistaDTO;
import com.findfix.find_fix_app.especialista.dto.ActualizarOficioEspDTO;
import com.findfix.find_fix_app.especialista.model.Especialista;
import com.findfix.find_fix_app.especialista.repository.EspecialistaRepository;
import com.findfix.find_fix_app.exception.exceptions.EspecialistaExcepcion;
import com.findfix.find_fix_app.exception.exceptions.SpecialistRequestNotFoundException;
import com.findfix.find_fix_app.exception.exceptions.UserNotFoundException;
import com.findfix.find_fix_app.oficio.model.Oficio;
import com.findfix.find_fix_app.oficio.repository.OficioRepository;
import com.findfix.find_fix_app.usuario.model.Usuario;
import com.findfix.find_fix_app.usuario.service.UsuarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class EspecialistaServiceImpl implements EspecialistaService {

    private final EspecialistaRepository especialistaRepository;
    private final OficioRepository oficioRepository;
    private final UsuarioService usuarioService;

    @Override
    public Especialista guardar(Long id) throws UserNotFoundException {
        Usuario usuario = usuarioService.buscarPorId(id).orElseThrow(() -> new UserNotFoundException("Usuario no encontrado"));

        Especialista especialista = new Especialista();
        especialista.setUsuario(usuario);


        return especialistaRepository.save(especialista);
    }


    @Override
    public Especialista actualizarEspecialista(String email, ActualizarEspecialistaDTO dto) throws SpecialistRequestNotFoundException {
        Especialista especialista = especialistaRepository.findByUsuarioEmail(email)
                .orElseThrow(() -> new SpecialistRequestNotFoundException("Especialista no encontrado"));

        if (dto.descripcion() != null && !dto.descripcion().isEmpty()) {
            especialista.setDescripcion(dto.descripcion());
        }

        if (dto.nombre() != null && !dto.nombre().isEmpty()) {
            especialista.getUsuario().setNombre(dto.nombre());
        }

        if (dto.apellido() != null && !dto.apellido().isEmpty()) {
            especialista.getUsuario().setApellido(dto.apellido());
        }

        if (dto.telefono() != null && !dto.telefono().isEmpty()) {
            especialista.getUsuario().setTelefono(dto.telefono());
        }

        if (dto.ciudad() != null && !dto.ciudad().isEmpty()) {
            especialista.getUsuario().setCiudad(dto.ciudad());
        }
        if (dto.dni() != null) {
            especialista.setDni(dto.dni());
        }

        /// usuarioService.actualizar(especialista);
        return especialistaRepository.save(especialista);
    }


    @Override
    public List<Especialista> obtenerEspecialistas() {
        return especialistaRepository.findAll();
    }

    @Override
    public List<Especialista> obtenerEspecialistasDisponibles() {
        return especialistaRepository.findAll();
    }

    @Override
    public void eliminarPorEmail(String email) throws SpecialistRequestNotFoundException {
        if(especialistaRepository.findByUsuarioEmail(email).isEmpty()){
            throw new SpecialistRequestNotFoundException("Especialista no encontrado");
        }
        especialistaRepository.deleteById(especialistaRepository.findByUsuarioEmail(email).get().getEspecialistaId());
    }

    @Override
    public Optional<Especialista> buscarPorDni(Long dni) {
        return especialistaRepository.findByDni(dni);
    }

    @Override
    public Optional<Especialista> buscarPorId(Long id) {
        return especialistaRepository.findById(id);
    }


    @Override
    public Optional<Especialista> buscarPorEmail(String email) {
        return especialistaRepository.findByUsuarioEmail(email);
    }

    @Override
    public Especialista actualizarOficioDeEspecialista(String email, ActualizarOficioEspDTO dto) throws SpecialistRequestNotFoundException, EspecialistaExcepcion {
        Especialista especialista = especialistaRepository.findByUsuarioEmail(email).orElseThrow(()-> new SpecialistRequestNotFoundException("Especialista no encontrado"));

        ///Se eliminan los oficios que esten en la lista para eliminar
        if(dto.eliminar() != null && !dto.eliminar().isEmpty()){
            Set<Oficio> oficiosAEliminar = oficioRepository.findAllById(dto.eliminar()).stream().collect(Collectors.toSet());
            especialista.getOficios().removeAll(oficiosAEliminar);
        }

        ///Se agregan los oficios que esten en la lista para agregar
        if(dto.agregar() != null && !dto.agregar().isEmpty()){
            Set<Oficio>oficiosAAgregar = oficioRepository.findAllById(dto.agregar()).stream().collect(Collectors.toSet());
            especialista.getOficios().addAll(oficiosAAgregar);
        }

        ///validacion para que quede al menos un oficio en la lista
        if(especialista.getOficios().isEmpty()){
            throw new EspecialistaExcepcion("El especialista debe tener al menos un oficio");
        }

        /// usuarioService.actualizar(especialista);
        return especialistaRepository.save(especialista);
    }

    @Override
    public List<Especialista> buscarPorOficio(String nombreOficio) {
        Optional<Oficio> oficioOptional = oficioRepository.findByNombre(nombreOficio);
        if (oficioOptional.isPresent()) {
            return especialistaRepository.findAllByOficios(oficioOptional.get());
        } else {
            return List.of();
        }
    }

    @Override
    public List<Especialista> buscarPorCiudad(String ciudad) {
        return especialistaRepository.findByUsuario_Ciudad(ciudad).stream().collect(Collectors.toList());
    }

}
