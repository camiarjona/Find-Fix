package com.findfix.find_fix_app.especialista.service;

import com.findfix.find_fix_app.utils.auth.AuthService;
import com.findfix.find_fix_app.utils.enums.CiudadesDisponibles;
import com.findfix.find_fix_app.especialista.Specifications.EspecialistaSpecifications;
import com.findfix.find_fix_app.especialista.dto.*;
import com.findfix.find_fix_app.especialista.model.Especialista;
import com.findfix.find_fix_app.especialista.repository.EspecialistaRepository;
import com.findfix.find_fix_app.utils.exception.exceptions.EspecialistaExcepcion;
import com.findfix.find_fix_app.utils.exception.exceptions.EspecialistaNotFoundException;
import com.findfix.find_fix_app.utils.exception.exceptions.UserNotFoundException;
import com.findfix.find_fix_app.oficio.model.Oficio;
import com.findfix.find_fix_app.oficio.repository.OficioRepository;
import com.findfix.find_fix_app.usuario.model.Usuario;
import com.findfix.find_fix_app.usuario.service.UsuarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.*;

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
    public Especialista obtenerEspecialistaAutenticado() throws UserNotFoundException, EspecialistaNotFoundException {
        Usuario usuario = authService.obtenerUsuarioAutenticado();
        return especialistaRepository.findByUsuario(usuario)
                .orElseThrow(() -> new EspecialistaNotFoundException("⚠️Especialista no encontrado para el usuario autenticado"));
    }

    /// Metodo para actualizar los datos y llamarlo desde el metodo de actualizar por admin o por especialista
    private void actualizarDatosEspecialista(Especialista especialista, ActualizarEspecialistaDTO dto) throws EspecialistaExcepcion {
        if (dto.tieneDescripcion()) {
            especialista.setDescripcion(dto.descripcion());
        }

        if (dto.tieneNombre()) {
            especialista.getUsuario().setNombre(dto.nombre());
        }

        if (dto.tieneApellido()) {
            especialista.getUsuario().setApellido(dto.apellido());
        }

        if (dto.tieneTelefono()) {
            especialista.getUsuario().setTelefono(dto.telefono());
        }

        if (dto.tieneCiudad()) {
            especialista.getUsuario().setCiudad(CiudadesDisponibles.desdeString(dto.ciudad()));
        }

        if (dto.tieneDni() && !dto.dni().equals(especialista.getDni())) {
            boolean existe = especialistaRepository.existsByDni(dto.dni());
            if (existe) {
                throw new EspecialistaExcepcion("⚠️El DNI ya está en uso por otro especialista.");
            }
            especialista.setDni(dto.dni());
        }
    }

    /// Metodo para que el admin actualice los atributos de un especialista
    @Override
    public Especialista actualizarEspecialistaAdmin(String email, ActualizarEspecialistaDTO dto) throws EspecialistaNotFoundException, EspecialistaExcepcion {
        Especialista especialista = especialistaRepository.findByUsuarioEmail(email)
                .orElseThrow(() -> new EspecialistaNotFoundException("⚠️Especialista no encontrado"));

        actualizarDatosEspecialista(especialista, dto);

        usuarioService.actualizarUsuarioEspecialista(especialista.getUsuario());
        return especialistaRepository.save(especialista);
    }

    /// Metodo para que el especialista actualice sus atributos
    @Override
    public Especialista actualizarEspecialista(ActualizarEspecialistaDTO dto) throws EspecialistaNotFoundException, UserNotFoundException, EspecialistaExcepcion {
        Especialista especialista = obtenerEspecialistaAutenticado();

        actualizarDatosEspecialista(especialista, dto);

        usuarioService.actualizarUsuarioEspecialista(especialista.getUsuario());
        return especialistaRepository.save(especialista);
    }

    /// Metodo para mostrar todos los especialistas para el Admin
    @Override
    public List<Especialista> obtenerEspecialistas() throws EspecialistaNotFoundException {
        List<Especialista> especialistas = especialistaRepository.findAll();
        if (especialistas.isEmpty()) {
            throw new EspecialistaNotFoundException("⚠️No hay especialistas registrados en el sistema.");
        }
        return especialistas;
    }

    /// Metodo para mostrar todos los especialistas para el cliente y especialista
    @Override
    public List<Especialista> obtenerEspecialistasDisponibles() throws EspecialistaNotFoundException {
        List<Especialista> especialistas = especialistaRepository.findAll(EspecialistaSpecifications.tieneDatosCompletos());
        if (especialistas.isEmpty()) {
            throw new EspecialistaNotFoundException("⚠️No hay especialistas disponibles en este momento.");
        }
        return especialistas;
    }


    ///Metodo para que el admin pueda eliminar un especialista por email
    @Override
    public void eliminarPorEmail(String email) throws EspecialistaNotFoundException {
        if(especialistaRepository.findByUsuarioEmail(email).isEmpty()){
            throw new EspecialistaNotFoundException("⚠️Especialista no encontrado");
        }
        especialistaRepository.deleteById(especialistaRepository.findByUsuarioEmail(email).get().getEspecialistaId());
    }

    /// Metodo para buscar un especialista por Email
    @Override
    public Optional<Especialista> buscarPorEmail(String email) {
        return especialistaRepository.findByUsuarioEmail(email);
    }

    /// Metotodo para actualizar los oficios que luego va a llamar cada metodo de actualizar oficio
    private void actualizarDatosOficiosEspecialista(Especialista especialista, ActualizarOficioEspDTO dto) throws EspecialistaExcepcion {
        Set<Oficio> oficiosActuales = especialista.getOficios();

        // Eliminar oficios con validación previa
        if (dto.tieneEliminar()) {
            Set<Oficio> oficiosAEliminar = new HashSet<>();

            for (String nombreOficio : dto.eliminar()) {
                Oficio oficio = oficioRepository.findByNombre(nombreOficio.trim().toUpperCase())
                        .orElseThrow(() -> new EspecialistaExcepcion("⚠️El oficio '" + nombreOficio + "' no existe"));

                if (!oficiosActuales.contains(oficio)) {
                    throw new EspecialistaExcepcion("⚠️El oficio '" + nombreOficio + "' no está asignado al especialista y no puede ser eliminado");
                }

                oficiosAEliminar.add(oficio);
            }

            // Validación previa a eliminar para que el set no quede vacio
            if (oficiosActuales.size() - oficiosAEliminar.size() <= 0) {
                throw new EspecialistaExcepcion("⚠️El especialista debe tener al menos un oficio asignado");
            }

            // Si pasa la validación, eliminamos
            oficiosActuales.removeAll(oficiosAEliminar);
        }

        // Agregar nuevos oficios con validación de duplicados
        if (dto.tieneAgregar()) {
            for (String nombreOficio : dto.agregar()) {
                Oficio oficio = oficioRepository.findByNombre(nombreOficio.trim().toUpperCase())
                        .orElseThrow(() -> new EspecialistaExcepcion("⚠️El oficio '" + nombreOficio + "' no existe"));

                if (oficiosActuales.contains(oficio)) {
                    throw new EspecialistaExcepcion("⚠️El oficio '" + nombreOficio + "' ya está asignado al especialista");
                }

                oficiosActuales.add(oficio);
            }
        }

        especialista.setOficios(oficiosActuales);
    }


    /// Metodo para actualizar (agregar o eliminar) oficios de un especialista por el Admin
    @Override
    public Especialista actualizarOficioDeEspecialistaAdmin(String email, ActualizarOficioEspDTO dto) throws EspecialistaNotFoundException, EspecialistaExcepcion {
        Especialista especialista = especialistaRepository.findByUsuarioEmail(email)
                .orElseThrow(() -> new EspecialistaNotFoundException("⚠️Especialista no encontrado"));

        actualizarDatosOficiosEspecialista(especialista, dto);

        return especialistaRepository.save(especialista);
    }


    /// Metodo para actualizar (agregar o eliminar) oficios
    @Override
    public Especialista actualizarOficioDeEspecialista(ActualizarOficioEspDTO dto) throws EspecialistaNotFoundException, EspecialistaExcepcion, UserNotFoundException {
        Especialista especialista = obtenerEspecialistaAutenticado();

        actualizarDatosOficiosEspecialista(especialista, dto);

        return especialistaRepository.save(especialista);
    }


/// Metodo para ver mi perfil de especialista
    @Override
    public VerPerfilEspecialistaDTO verPerfilEspecialista() throws UserNotFoundException, EspecialistaNotFoundException {
        Especialista especialista = obtenerEspecialistaAutenticado();
        return new VerPerfilEspecialistaDTO(especialista);
    }

/// Metodo para filtrar especialistas
    @Override
    public List<EspecialistaFichaCompletaDTO> filtrarEspecialistas(BuscarEspecialistaDTO filtro) throws EspecialistaExcepcion {
        List<Specification<Especialista>> specifications = new ArrayList<>();

        // 1. Filtro por ID
        if (filtro.tieneId()) {
            specifications.add(EspecialistaSpecifications.tieneId(filtro.id()));
        }

        // 2. Filtro por Oficio
        if (filtro.tieneOficio()) {
            specifications.add(EspecialistaSpecifications.tieneOficio(filtro.oficio().toUpperCase()));
        }

        // 3. Filtro por Ciudad
        if (filtro.tieneCiudad()) {
            try {
                CiudadesDisponibles ciudadEnum = CiudadesDisponibles.desdeString(filtro.ciudad());
                specifications.add(EspecialistaSpecifications.enCiudad(ciudadEnum.getNombreAmigable()));
            } catch (IllegalArgumentException e) {
                throw new EspecialistaExcepcion("⚠️La ciudad ingresada no es válida. Ciudades disponibles: " +
                        CiudadesDisponibles.ciudadesDisponibles());
            }
        }

        // 4. Filtro por DNI
        if (filtro.tieneDni()) {
            if (filtro.dni().toString().length() == 8) {
                throw new EspecialistaExcepcion("⚠️El DNI debe tener 8 dígitos");
            }
            specifications.add(EspecialistaSpecifications.tieneDni(filtro.dni()));
        }

        // 5. Filtro por Email
        if (filtro.tieneEmail()) {
            if (!filtro.email().matches("^[\\w-.]+@([\\w-]+\\.)+[\\w-]{2,4}$")) {
                throw new EspecialistaExcepcion("⚠️El formato del email no es válido");
            }
            specifications.add(EspecialistaSpecifications.tieneEmail(filtro.email()));
        }

        // 6. Filtro por Calificación Mínima
        if (filtro.tieneCalificacionMinima()) {
            if (filtro.minCalificacion() < 0 || filtro.minCalificacion() > 5) {
                throw new EspecialistaExcepcion("⚠️La calificación debe estar entre 0 y 5");
            }
            specifications.add(EspecialistaSpecifications.tieneCalificacionMinima(filtro.minCalificacion()));
        }

        Specification<Especialista> finalSpec = specifications.stream()
                .reduce(Specification::and)
                .orElse(EspecialistaSpecifications.tieneDatosCompletos())
                .and(EspecialistaSpecifications.tieneDatosCompletos());

        List<Especialista> especialistas = especialistaRepository.findAll(finalSpec);
        if (especialistas.isEmpty()) {
            throw new EspecialistaExcepcion("⚠️No se encontraron especialistas con los criterios especificados");
        }


        return especialistas.stream()
                .map(EspecialistaFichaCompletaDTO::new)
                .toList();
    }
}
