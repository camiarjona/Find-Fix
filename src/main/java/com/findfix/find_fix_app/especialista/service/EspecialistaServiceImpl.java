package com.findfix.find_fix_app.especialista.service;

import com.findfix.find_fix_app.auth.service.AuthService;
import com.findfix.find_fix_app.enums.CiudadesDisponibles;
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
    private final AuthService authService;

    /// Metodo para guardar un usuario como especialista
    @Override
    public Especialista guardar(Usuario usuario){
        Especialista especialista = new Especialista();
        especialista.setUsuario(usuario);

        return especialistaRepository.save(especialista);
    }


    /// Metodo para traerme el especialista segun el usuario registrado
    public Especialista obtenerEspecialistaAutenticado() throws UserNotFoundException, SpecialistRequestNotFoundException {
        Usuario usuario = authService.obtenerUsuarioAutenticado();
        return especialistaRepository.findByUsuario(usuario)
                .orElseThrow(() -> new SpecialistRequestNotFoundException("Especialista no encontrado para el usuario autenticado"));
    }

    /// Metodo para validar los string que se ingresan
    private boolean esValorValido(String valor) {
        return valor != null && !valor.isEmpty();
    }

    /// Metodo para actualizar los datos y llamarlo desde el metodo de actualizar por admin o por especialista
    private void actualizarDatosEspecialista(Especialista especialista, ActualizarEspecialistaDTO dto) throws EspecialistaExcepcion {
        if (esValorValido(dto.descripcion())) {
            especialista.setDescripcion(dto.descripcion());
        }

        if (esValorValido(dto.nombre())) {
            especialista.getUsuario().setNombre(dto.nombre());
        }

        if (esValorValido(dto.apellido())) {
            especialista.getUsuario().setApellido(dto.apellido());
        }

        if (esValorValido(dto.telefono())) {
            especialista.getUsuario().setTelefono(dto.telefono());
        }

        if (esValorValido(dto.ciudad())) {
            especialista.getUsuario().setCiudad(CiudadesDisponibles.desdeString(dto.ciudad()));
        }

        if (dto.dni() != null && !dto.dni().equals(especialista.getDni())) {
            boolean existe = especialistaRepository.existsByDni(dto.dni());
            if (existe) {
                throw new EspecialistaExcepcion("El DNI ya está en uso por otro especialista.");
            }
            especialista.setDni(dto.dni());
        }
    }

    /// Metodo para que el admin actualice los atributos de un especialista
    @Override
    public Especialista actualizarEspecialistaAdmin(String email, ActualizarEspecialistaDTO dto) throws SpecialistRequestNotFoundException, EspecialistaExcepcion {
        Especialista especialista = especialistaRepository.findByUsuarioEmail(email)
                .orElseThrow(() -> new SpecialistRequestNotFoundException("Especialista no encontrado"));

        actualizarDatosEspecialista(especialista, dto);

        usuarioService.actualizarUsuarioEspecialista(especialista.getUsuario());
        return especialistaRepository.save(especialista);
    }

    /// Metodo para que el especialista actualice sus atributos
    @Override
    public Especialista actualizarEspecialista(ActualizarEspecialistaDTO dto) throws SpecialistRequestNotFoundException, UserNotFoundException, EspecialistaExcepcion {
        Especialista especialista = obtenerEspecialistaAutenticado();

        actualizarDatosEspecialista(especialista, dto);

        usuarioService.actualizarUsuarioEspecialista(especialista.getUsuario());
        return especialistaRepository.save(especialista);
    }

    /// Metodo para mostrar todos los especialistas para el Admin
    @Override
    public List<Especialista> obtenerEspecialistas() throws SpecialistRequestNotFoundException {
        List<Especialista> especialistas = especialistaRepository.findAll();
        if (especialistas.isEmpty()) {
            throw new SpecialistRequestNotFoundException("No hay especialistas registrados en el sistema.");
        }
        return especialistas;
    }

    /// Metodo para mostrar todos los especialistas para el cliente y especialista
    @Override
    public List<Especialista> obtenerEspecialistasDisponibles() throws SpecialistRequestNotFoundException {
        List<Especialista> especialistas = especialistaRepository.findAll();
        if (especialistas.isEmpty()) {
            throw new SpecialistRequestNotFoundException("No hay especialistas disponibles en este momento.");
        }
        return especialistas;
    }


    ///Metodo para que el admin pueda eliminar un especialista por email
    @Override
    public void eliminarPorEmail(String email) throws SpecialistRequestNotFoundException {
        if(especialistaRepository.findByUsuarioEmail(email).isEmpty()){
            throw new SpecialistRequestNotFoundException("Especialista no encontrado");
        }
        especialistaRepository.deleteById(especialistaRepository.findByUsuarioEmail(email).get().getEspecialistaId());
    }

    /// Metodo para buscar un especialista por DNI
    @Override
    public Optional<Especialista> buscarPorDni(Long dni) {
        return especialistaRepository.findByDni(dni);
    }

    /// Metodo para buscar un especialista por ID
    @Override
    public Optional<Especialista> buscarPorId(Long id) {
        return especialistaRepository.findById(id);
    }


    /// Metodo para buscar un especialista por Email
    @Override
    public Optional<Especialista> buscarPorEmail(String email) {
        return especialistaRepository.findByUsuarioEmail(email);
    }

    /// Metotodo para actualoizar los oficios que luego va a llamar cada metodo de actualizar oficio
    private void actualizarDatosOficiosEspecialista(Especialista especialista, ActualizarOficioEspDTO dto) throws EspecialistaExcepcion {
        // Eliminar oficios
        if(dto.eliminar() != null && !dto.eliminar().isEmpty()) {
            Set<Oficio> oficiosAEliminar = oficioRepository.findAllById(dto.eliminar())
                    .stream()
                    .collect(Collectors.toSet());
            especialista.getOficios().removeAll(oficiosAEliminar);
        }

        // Agregar oficios
        if(dto.agregar() != null && !dto.agregar().isEmpty()) {
            Set<Oficio> oficiosAAgregar = oficioRepository.findAllById(dto.agregar())
                    .stream()
                    .collect(Collectors.toSet());
            especialista.getOficios().addAll(oficiosAAgregar);
        }

        // Validar que quede al menos un oficio
        if(especialista.getOficios().isEmpty()) {
            throw new EspecialistaExcepcion("El especialista debe tener al menos un oficio");
        }
    }

    /// Metodo para actualizar (agregar o eliminar) oficios de un especialista por el Admin
    @Override
    public Especialista actualizarOficioDeEspecialistaAdmin(String email, ActualizarOficioEspDTO dto) throws SpecialistRequestNotFoundException, EspecialistaExcepcion {
        Especialista especialista = especialistaRepository.findByUsuarioEmail(email)
                .orElseThrow(() -> new SpecialistRequestNotFoundException("Especialista no encontrado"));

        actualizarDatosOficiosEspecialista(especialista, dto);

        return especialistaRepository.save(especialista);
    }


    /// Metodo para actualizar (agregar o eliminar) oficios
    @Override
    public Especialista actualizarOficioDeEspecialista(ActualizarOficioEspDTO dto) throws SpecialistRequestNotFoundException, EspecialistaExcepcion, UserNotFoundException {
        Especialista especialista = obtenerEspecialistaAutenticado();

        actualizarDatosOficiosEspecialista(especialista, dto);

        return especialistaRepository.save(especialista);
    }

    /// Metodo para buscar especialistas de un oficio en particular
    @Override
    public List<Especialista> buscarPorOficio(String nombreOficio) {
        Optional<Oficio> oficioOptional = oficioRepository.findByNombre(nombreOficio);
        if (oficioOptional.isPresent()) {
            return especialistaRepository.findAllByOficios(oficioOptional.get());
        } else {
            return List.of();
        }
    }


    /// Metodo para buscar especialistas de un oficio en particular
    @Override
    public List<Especialista> buscarPorCiudad(String ciudad) {
        return especialistaRepository.findByUsuario_Ciudad(ciudad).stream().collect(Collectors.toList());
    }

}
