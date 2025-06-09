package com.findfix.find_fix_app.especialista.service;

import com.findfix.find_fix_app.especialista.repository.EspecialistaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EspecialistaServiceImpl implements EspecialistaService {

    private final EspecialistaRepository especialistaRepository;
    private final OficioRepository oficioRepository;

    @Override
    public Especialista guardar(Especialista especialista) {
        if(especialistaRepository.findByDni(especialista.getDni()).isPresent()){
            throw new IllegalArgumentException("El especialista ya se encontraba registrado.");
        }
        return especialistaRepository.save(especialista);
    }

    @Override
    public Especialista actualizar(Long dni, ActualizarEspecialistaDTO dto) {
        Especialista especialista = especialistaRepository.findByDni(dni).orElseThrow(() -> new RuntimeException("Especialista no encontrado"));

        if(dto.descripcion() != null){
            especialista.setDescripcion(dto.descripcion());
        }

        if (!dto.nombre().isEmpty()){
            especialista.getUsuario().setNombre(dto.nombre());
        }

        if (!dto.apellido().isEmpty()){
            especialista.getUsuario().setApellido(dto.apellido());
        }

        if (!dto.telefono().isEmpty()){
            especialista.getUsuario().setTelefono(dto.telefono());
        }

        if (!dto.ciudad().isEmpty()){
            especialista.getUsuario().setCiudad(dto.ciudad());
        }
        return especialistaRepository.save(especialista);
    }

    @Override
    public List<Especialista> obtenerEspecialistas() {
        return especialistaRepository.findAll();
    }

    @Override
    public void eliminar(Long dni) {
        if(!especialistaRepository.existsByDni(dni)){
            throw new IllegalArgumentException("Especialista no encontrado");
        }
        especialistaRepository.deleteByDni(dni);
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
    public Especialista modificarOficioDeEspecialista(Long dni, ActualizarOficioEspDTO dto) {
        Especialista especialista = especialistaRepository.findByDni(dni).orElseThrow(()-> new EntityNotFoundException("Especialista no encontrado"));

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
            throw new IllegalArgumentException("El especialista debe tener al menos un oficio");
        }

        return especialistaRepository.save(especialista);
    }

}
