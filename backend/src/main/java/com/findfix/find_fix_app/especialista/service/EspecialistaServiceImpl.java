package com.findfix.find_fix_app.especialista.service;

import com.findfix.find_fix_app.auth.service.AuthServiceImpl;
import com.findfix.find_fix_app.especialista.Specifications.EspecialistaSpecifications;
import com.findfix.find_fix_app.especialista.dto.*;
import com.findfix.find_fix_app.especialista.model.Especialista;
import com.findfix.find_fix_app.especialista.repository.EspecialistaRepository;
import com.findfix.find_fix_app.utils.exception.exceptions.EspecialistaExcepcion;
import com.findfix.find_fix_app.utils.exception.exceptions.EspecialistaNotFoundException;
import com.findfix.find_fix_app.utils.exception.exceptions.RolNotFoundException;
import com.findfix.find_fix_app.utils.exception.exceptions.UsuarioNotFoundException;
import com.findfix.find_fix_app.oficio.model.Oficio;
import com.findfix.find_fix_app.oficio.repository.OficioRepository;
import com.findfix.find_fix_app.usuario.model.Usuario;
import com.findfix.find_fix_app.usuario.service.UsuarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import com.findfix.find_fix_app.utils.geo.GeoUtils;

@Service
@RequiredArgsConstructor
public class EspecialistaServiceImpl implements EspecialistaService {

    private final EspecialistaRepository especialistaRepository;
    private final OficioRepository oficioRepository;
    private final UsuarioService usuarioService;
    private final AuthServiceImpl authServiceImpl;
    private final EspecialistaDesvinculacionService especialistaDesvinculacionService;

    /// Metodo para guardar un usuario como especialista
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void guardar(Usuario usuario) {
        Especialista especialista = new Especialista();
        especialista.setUsuario(usuario);

        especialistaRepository.save(especialista);
    }

    /// Metodo para traerme el especialista según el usuario registrado
    public Especialista obtenerEspecialistaAutenticado()
            throws UsuarioNotFoundException, EspecialistaNotFoundException {
        Usuario usuario = authServiceImpl.obtenerUsuarioAutenticado();
        return especialistaRepository.findByUsuario(usuario)
                .orElseThrow(() -> new EspecialistaNotFoundException(
                        "⚠️Especialista no encontrado para el usuario autenticado"));
    }

    /// Metodo para actualizar los datos y llamarlo desde el metodo de actualizar
    /// por admin o por especialista
    private void actualizarDatosEspecialista(Especialista especialista, ActualizarEspecialistaDTO dto)
            throws EspecialistaExcepcion {
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
            especialista.getUsuario().setCiudad(dto.ciudad());

            if (dto.tieneCoordenadas()) {
                especialista.getUsuario().setLatitud(dto.latitud());
                especialista.getUsuario().setLongitud(dto.longitud());
            }
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
    @Transactional(rollbackFor = Exception.class)
    public Especialista actualizarEspecialistaAdmin(String email, ActualizarEspecialistaDTO dto)
            throws EspecialistaNotFoundException, EspecialistaExcepcion {
        Especialista especialista = especialistaRepository.findByUsuarioEmail(email)
                .orElseThrow(() -> new EspecialistaNotFoundException("⚠️Especialista no encontrado"));

        actualizarDatosEspecialista(especialista, dto);

        usuarioService.actualizarUsuarioEspecialista(especialista.getUsuario());
        return especialistaRepository.save(especialista);
    }

    /// Metodo para que el especialista actualice sus atributos
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Especialista actualizarEspecialista(ActualizarEspecialistaDTO dto)
            throws EspecialistaNotFoundException, UsuarioNotFoundException, EspecialistaExcepcion {
        Especialista especialista = obtenerEspecialistaAutenticado();

        actualizarDatosEspecialista(especialista, dto);

        usuarioService.actualizarUsuarioEspecialista(especialista.getUsuario());
        return especialistaRepository.save(especialista);
    }

    /// Metodo para mostrar todos los especialistas para el Admin
    @Override
    @Transactional(readOnly = true)
    public List<Especialista> obtenerEspecialistas() throws EspecialistaNotFoundException {
        List<Especialista> especialistas = especialistaRepository.findAll();
        if (especialistas.isEmpty()) {
            throw new EspecialistaNotFoundException("⚠️No hay especialistas registrados en el sistema.");
        }
        return especialistas;
    }

    /// Metodo para mostrar todos los especialistas para el cliente y especialista
    @Override
    @Transactional(readOnly = true)
    public List<Especialista> obtenerEspecialistasDisponibles()
            throws EspecialistaNotFoundException, UsuarioNotFoundException {
        List<Especialista> especialistas = especialistaRepository
                .findAll(EspecialistaSpecifications.tieneDatosCompletos());

        try {
            Usuario usuario = authServiceImpl.obtenerUsuarioAutenticado();
            List<Especialista> especialistasDisponibles = especialistas.stream()
                    .filter(e -> !usuario.equals(e.getUsuario())).toList();
            if (especialistasDisponibles.isEmpty()) {
                throw new EspecialistaNotFoundException("⚠️No hay especialistas disponibles en este momento.");
            }
            return especialistasDisponibles;

        } catch (UsuarioNotFoundException e) {
            return especialistas;
        }

    }

    /// Metodo para que el admin pueda eliminar un especialista por email
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void eliminarPorEmail(String email) throws EspecialistaNotFoundException, RolNotFoundException {
        Especialista especialista = especialistaRepository.findByUsuarioEmail(email)
                .orElseThrow(() -> new EspecialistaNotFoundException("⚠️Especialista no encontrado"));

        especialistaDesvinculacionService.desvincularEspecialista(especialista);
        usuarioService.eliminarRol(especialista.getUsuario(), "ESPECIALISTA");

        especialistaRepository.delete(especialista);
    }

    /// Metodo para buscar un especialista por Email
    @Override
    @Transactional(readOnly = true)
    public Optional<Especialista> buscarPorEmail(String email) {
        return especialistaRepository.findByUsuarioEmail(email);
    }

    /// Metotodo para actualizar los oficios que luego va a llamar cada metodo de
    /// actualizar oficio
    private void actualizarDatosOficiosEspecialista(Especialista especialista, ActualizarOficioEspDTO dto)
            throws EspecialistaExcepcion {
        Set<Oficio> oficiosActuales = especialista.getOficios();

        // Eliminar oficios con validación previa
        if (dto.tieneEliminar()) {
            Set<Oficio> oficiosAEliminar = new HashSet<>();

            for (String nombreOficio : dto.eliminar()) {
                Oficio oficio = oficioRepository.findByNombre(nombreOficio.trim().toUpperCase())
                        .orElseThrow(() -> new EspecialistaExcepcion("⚠️El oficio '" + nombreOficio + "' no existe"));

                if (!oficiosActuales.contains(oficio)) {
                    throw new EspecialistaExcepcion("⚠️El oficio '" + nombreOficio
                            + "' no está asignado al especialista y no puede ser eliminado");
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
                    throw new EspecialistaExcepcion(
                            "⚠️El oficio '" + nombreOficio + "' ya está asignado al especialista");
                }

                oficiosActuales.add(oficio);
            }
        }

        especialista.setOficios(oficiosActuales);
    }

    /// Metodo para actualizar (agregar o eliminar) oficios de un especialista por
    /// el Admin
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Especialista actualizarOficioDeEspecialistaAdmin(String email, ActualizarOficioEspDTO dto)
            throws EspecialistaNotFoundException, EspecialistaExcepcion {
        Especialista especialista = especialistaRepository.findByUsuarioEmail(email)
                .orElseThrow(() -> new EspecialistaNotFoundException("⚠️Especialista no encontrado"));

        actualizarDatosOficiosEspecialista(especialista, dto);

        return especialistaRepository.save(especialista);
    }

    /// Metodo para actualizar (agregar o eliminar) oficios
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Especialista actualizarOficioDeEspecialista(ActualizarOficioEspDTO dto)
            throws EspecialistaNotFoundException, EspecialistaExcepcion, UsuarioNotFoundException {
        Especialista especialista = obtenerEspecialistaAutenticado();

        actualizarDatosOficiosEspecialista(especialista, dto);

        return especialistaRepository.save(especialista);
    }

    /// Metodo para ver mi perfil de especialista
    @Override
    @Transactional(readOnly = true)
    public VerPerfilEspecialistaDTO verPerfilEspecialista()
            throws UsuarioNotFoundException, EspecialistaNotFoundException {
        Especialista especialista = obtenerEspecialistaAutenticado();
        return new VerPerfilEspecialistaDTO(especialista);
    }

    /// Metodo para filtrar especialistas
    @Override
    @Transactional(readOnly = true) // Cambiar a readOnly para búsquedas
    public List<EspecialistaFichaCompletaDTO> filtrarEspecialistas(BuscarEspecialistaDTO filtro)
            throws EspecialistaExcepcion, UsuarioNotFoundException {

        // 1. Filtros de Base de Datos (SQL)
        List<Specification<Especialista>> specifications = new ArrayList<>();

        if (filtro.tieneId()) specifications.add(EspecialistaSpecifications.tieneId(filtro.id()));
        if (filtro.tieneOficio())
            specifications.add(EspecialistaSpecifications.tieneOficio(filtro.oficio().toUpperCase()));
        if (filtro.tieneCiudad()) {
            try {
                specifications.add(EspecialistaSpecifications.enCiudad(filtro.ciudad()));
            } catch (IllegalArgumentException e) {
                throw new EspecialistaExcepcion("⚠️La ciudad ingresada no es válida.");
            }
        }
        if (filtro.tieneDni()) specifications.add(EspecialistaSpecifications.tieneDni(filtro.dni()));
        if (filtro.tieneEmail()) specifications.add(EspecialistaSpecifications.tieneEmail(filtro.email()));
        if (filtro.tieneCalificacionMinima())
            specifications.add(EspecialistaSpecifications.tieneCalificacionMinima(filtro.minCalificacion()));

        Specification<Especialista> finalSpec = specifications.stream()
                .reduce(Specification::and)
                .orElse(EspecialistaSpecifications.tieneDatosCompletos())
                .and(EspecialistaSpecifications.tieneDatosCompletos());

        // 2. Traer lista (convertida a ArrayList para poder modificarla)
        List<Especialista> especialistas = new ArrayList<>(especialistaRepository.findAll(finalSpec));

        // 3. Filtrar al usuario logueado
        try {
            Usuario usuarioLogueado = authServiceImpl.obtenerUsuarioAutenticado();
            especialistas.removeIf(e -> e.getUsuario().equals(usuarioLogueado));
        } catch (UsuarioNotFoundException e) { /* Ignorar si no está logueado */ }

        // -----------------------------------------------------------
        // 4. AQUÍ ESTABA EL ERROR. BORRA 'this.aplicarFiltros();'
        // Y PON ESTA LÓGICA DE ORDENAMIENTO EN SU LUGAR:
        // -----------------------------------------------------------
        if (filtro.tieneUbicacion()) {
            especialistas.sort((e1, e2) -> {
                // BLINDAJE: Si un especialista no tiene coordenadas (ej: Nicolás/Camila), va al final.
                boolean e1SinDatos = e1.getUsuario().getLatitud() == null || e1.getUsuario().getLongitud() == null;
                boolean e2SinDatos = e2.getUsuario().getLatitud() == null || e2.getUsuario().getLongitud() == null;

                if (e1SinDatos && e2SinDatos) return 0; // Ambos sin datos -> igual
                if (e1SinDatos) return 1;  // e1 sin datos -> va al fondo
                if (e2SinDatos) return -1; // e2 sin datos -> va al fondo

                // Cálculo de distancia real con Haversine
                double dist1 = com.findfix.find_fix_app.utils.geo.GeoUtils.calcularDistancia(
                        filtro.latitudUsuario(), filtro.longitudUsuario(),
                        e1.getUsuario().getLatitud(), e1.getUsuario().getLongitud()
                );

                double dist2 = com.findfix.find_fix_app.utils.geo.GeoUtils.calcularDistancia(
                        filtro.latitudUsuario(), filtro.longitudUsuario(),
                        e2.getUsuario().getLatitud(), e2.getUsuario().getLongitud()
                );

                return Double.compare(dist1, dist2); // Ordenar de menor a mayor distancia
            });
            // -----------------------------------------------------------

            if (especialistas.isEmpty()) {
                throw new EspecialistaExcepcion("⚠️No se encontraron especialistas con los criterios especificados");
            }

            return especialistas.stream()
                    .map(EspecialistaFichaCompletaDTO::new)
                    .toList();
        }
        Specification<Especialista> finalSpecE = specifications.stream()
                .reduce(Specification::and)
                .orElse(EspecialistaSpecifications.tieneDatosCompletos())
                .and(EspecialistaSpecifications.tieneDatosCompletos());

        List<Especialista> especialistasList = new ArrayList<>(especialistaRepository.findAll(finalSpecE));
        try {
            Usuario usuarioLogueado = authServiceImpl.obtenerUsuarioAutenticado();
            especialistas.removeIf(e -> e.getUsuario().equals(usuarioLogueado));
        } catch (UsuarioNotFoundException e) {
        }

            if (especialistas.isEmpty()) {
                throw new EspecialistaExcepcion("⚠️No se encontraron especialistas con los criterios especificados");
            }

            return especialistas.stream()
                    .map(EspecialistaFichaCompletaDTO::new)
                    .toList();
        }
    }