package com.findfix.find_fix_app.trabajo.trabajoApp.service;

import com.findfix.find_fix_app.trabajo.trabajoApp.specifications.TrabajoAppSpecifications;
import com.findfix.find_fix_app.trabajo.trabajoExterno.dto.BuscarTrabajoExternoDTO;
import com.findfix.find_fix_app.utils.auth.AuthService;
import com.findfix.find_fix_app.utils.enums.EstadosTrabajos;
import com.findfix.find_fix_app.especialista.model.Especialista;
import com.findfix.find_fix_app.especialista.service.EspecialistaService;
import com.findfix.find_fix_app.solicitudTrabajo.model.SolicitudTrabajo;
import com.findfix.find_fix_app.trabajo.trabajoApp.dto.ActualizarTrabajoAppDTO;
import com.findfix.find_fix_app.trabajo.trabajoApp.model.TrabajoApp;
import com.findfix.find_fix_app.trabajo.trabajoApp.repository.TrabajoAppRepository;
import com.findfix.find_fix_app.usuario.model.Usuario;
import com.findfix.find_fix_app.utils.exception.exceptions.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TrabajoAppServiceImpl implements TrabajoAppService {

    private final TrabajoAppRepository trabajoAppRepository;
    private final AuthService authService;
    private final EspecialistaService especialistaService;

    ///  METODO APRA GUARDAR EL TRABAJO QUE AUTOMATICAMENTE LLEGA UNA VEZ QUE UNA SOLICITUD ES ACEPTADA
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void registrarDesdeSolicitud(SolicitudTrabajo solicitudTrabajo, Especialista especialista) {
        TrabajoApp trabajoApp = new TrabajoApp();
        trabajoApp.setEspecialista(especialista);
        trabajoApp.setUsuario(solicitudTrabajo.getUsuario());
        trabajoApp.setEstado(EstadosTrabajos.CREADO);
        trabajoApp.setDescripcion(solicitudTrabajo.getDescripcion());
        trabajoApp.setSolicitudTrabajo(solicitudTrabajo);
        trabajoApp = trabajoAppRepository.save(trabajoApp);
        trabajoApp.setTitulo("NuevoTrabajo" + trabajoApp.getTrabajoAppId());

        trabajoAppRepository.save(trabajoApp);
    }

    ///  METODO PARA OBTENER LOS TRABAJOS DESDE LA PERSPECTIVA DE LOS CLIENTES
    @Override
    @Transactional(readOnly = true)
    public List<TrabajoApp> obtenerTrabajosClientes() throws UsuarioNotFoundException, TrabajoAppException {

        Usuario usuario = authService.obtenerUsuarioAutenticado();

        List<TrabajoApp> trabajosSolicitados = trabajoAppRepository.findByUsuario(usuario);

        if (trabajosSolicitados.isEmpty()) {
            throw new TrabajoAppException("Usted no tiene trabajos aceptados.");
        }

        return trabajosSolicitados;
    }

    /// METODO PARA OBTENER LOS TRABAJOS DESDE LA PERSPECTIVA DE LOS ESPECIALISTAS
    @Override
    @Transactional(readOnly = true)
    public List<TrabajoApp> obtenerTrabajosEspecialista() throws UsuarioNotFoundException, TrabajoAppException, EspecialistaNotFoundException {

        Especialista especialista = especialistaService.obtenerEspecialistaAutenticado();
        List<TrabajoApp> trabajosAceptados = trabajoAppRepository.findByEspecialista(especialista);

        if (trabajosAceptados.isEmpty()) {
            throw new TrabajoAppException("Usted no tiene trabajos en su lista.");
        }
        return trabajosAceptados;
    }

    ///  BUSCA UN TRABAJO POR TITULO
    @Override
    @Transactional(readOnly = true)
    public Optional<TrabajoApp> buscarPorTitulo(String tituloBuscado) {
        return trabajoAppRepository.findByTitulo(tituloBuscado);
    }

    ///  METODO PARA MODIFICAR UN TRABAJO
    @Override
    @Transactional(rollbackFor = Exception.class)
    public TrabajoApp actualizarTrabajo(String titulo, ActualizarTrabajoAppDTO dto) throws TrabajoAppNotFoundException, TrabajoAppException, UsuarioNotFoundException, EspecialistaNotFoundException {

        Especialista especialista = especialistaService.obtenerEspecialistaAutenticado();

        TrabajoApp trabajoApp = trabajoAppRepository.findByTitulo(titulo)
                .orElseThrow(() -> new TrabajoAppNotFoundException("El trabajo que desea modificar no esta registrado en el sistema."));

        //  si todos los datos del dto estan vacio o son null significa que no se mandaron datos para modificar
        if (!dto.tieneDescripcion() && !dto.tieneTitulo() && !dto.tienePresupuesto()) {
            throw new TrabajoAppException("No se pudo realizar modificacion debido a falta de datos");
        }

        validarEspecialista(trabajoApp, especialista);

        if (dto.tieneTitulo()) {
            Optional<TrabajoApp> verificacion = buscarPorTitulo(dto.titulo());
            if (verificacion.isEmpty()) {
                trabajoApp.setTitulo(dto.titulo());
            }
        }

        if (dto.tieneDescripcion()) {
            trabajoApp.setDescripcion(dto.descripcion());
        }

        if (dto.tienePresupuesto()) {
            trabajoApp.setPresupuesto(dto.presupuesto());
        }

        return trabajoAppRepository.save(trabajoApp);
    }

    // metodo para modificar el estado de un trabajo
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void modificarEstadoTrabajo(String titulo, String estadoNuevo) throws TrabajoAppNotFoundException, TrabajoAppException, UsuarioNotFoundException, EspecialistaNotFoundException {
        //obtenemos el especialista autenticado
        Especialista especialista = especialistaService.obtenerEspecialistaAutenticado();

        //buscamos el trabajo solicitado
        TrabajoApp trabajoApp = trabajoAppRepository.findByTitulo(titulo)
                .orElseThrow(() -> new TrabajoAppNotFoundException("El trabajo que desea modificar no esta registrado en el sistema."));

        //validamos el especialista
        validarEspecialista(trabajoApp, especialista);

        if (estadoNuevo != null && !estadoNuevo.isEmpty()) {

            //validamos los estados
            validarEstado(trabajoApp, estadoNuevo);

            EstadosTrabajos estado = EstadosTrabajos.desdeString(estadoNuevo);
            trabajoApp.setEstado(estado);

            //si el estado nuevo es en proceso, se le asigna fecha de inicio, y si es finalizado se le asigna fecha de fin
            if (estado.equals(EstadosTrabajos.EN_PROCESO)) {
                trabajoApp.setFechaInicio(LocalDate.now());
            } else if (estado.equals(EstadosTrabajos.FINALIZADO)) {
                trabajoApp.setFechaFin(LocalDate.now());
            }
        }
        trabajoAppRepository.save(trabajoApp);
    }

    // metodo para validar el estado de un trabajo
    public void validarEstado(TrabajoApp trabajoApp, String nombreEstado) throws TrabajoAppException {

        if (trabajoApp.getEstado().name().equalsIgnoreCase(nombreEstado)) {
            throw new TrabajoAppException("El trabajo ya se encuentra en ese estado");
        }

        if (trabajoApp.getEstado().equals(EstadosTrabajos.CREADO) && !nombreEstado.equalsIgnoreCase(EstadosTrabajos.EN_PROCESO.name())) {
            throw new TrabajoAppException("El estado debe pasar obligatoriamente por el estado en proceso");
        }

        if (trabajoApp.getEstado().equals(EstadosTrabajos.EN_PROCESO) && nombreEstado.equalsIgnoreCase(EstadosTrabajos.CREADO.name())) {
            throw new TrabajoAppException("El trabajo ya esta en proceso, no puede volver atras");
        }

        if (trabajoApp.getEstado().equals(EstadosTrabajos.EN_REVISION) && nombreEstado.equalsIgnoreCase(EstadosTrabajos.CREADO.name())) {
            throw new TrabajoAppException("El trabajo no puede volver a 'CREADO'. Como mucho puede revertir De EN REVISION A PROCESO ");
        }
    }

    ///  METODO PARA QUE UN ESPECIALISTA OBTENGA UNA FICHA DE UN TRABAJO FILTRANDO POR EL TITULO DEL MISMO
    @Override
    @Transactional(readOnly = true)
    public TrabajoApp obtenerFichaDeTrabajoParaEspecialista(String tituloBuscado) throws TrabajoAppNotFoundException, UsuarioNotFoundException, TrabajoAppException, EspecialistaNotFoundException {
        List<TrabajoApp> trabajoAppDelEspecialista = obtenerTrabajosEspecialista();
        Optional<TrabajoApp> trabajoBuscado = trabajoAppDelEspecialista.stream().filter(trabajoApp -> trabajoApp.getTitulo().equalsIgnoreCase(tituloBuscado)).findFirst();
        if (trabajoBuscado.isEmpty()) {
            throw new TrabajoAppNotFoundException("No se pudo otorgar la ficha trabajo debido a que el titulo ingresado no pertenece a ningun registro. ");
        } else {
            return trabajoBuscado.get();
        }
    }

    @Override
    @Transactional(readOnly = true)
    public TrabajoApp obtenerFichaDeTrabajoParaCliente(Long id) throws UsuarioNotFoundException, TrabajoAppException, TrabajoAppNotFoundException {
        List<TrabajoApp> trabajoAppDelCliente = obtenerTrabajosClientes();
        Optional<TrabajoApp> trabajoBuscado = trabajoAppDelCliente.stream().filter(trabajoApp -> Objects.equals(trabajoApp.getTrabajoAppId(), id)).findFirst();
        if (trabajoBuscado.isEmpty()) {
            throw new TrabajoAppNotFoundException("No se pudo otorgar la ficha trabajo debido a que el id ingresado no pertenece a ningun registro.");
        } else {
            return trabajoBuscado.get();
        }
    }

    //metodo para validar que el trabajo le corresponda al especialista autenticado
    @Override
    public void validarEspecialista(TrabajoApp trabajoApp, Especialista especialista) throws TrabajoAppException {
        if (!Objects.equals(especialista.getEspecialistaId(), trabajoApp.getEspecialista().getEspecialistaId())) {
            throw new TrabajoAppException("El trabajo que desea modificar no le pertenece. Corrobore el id ingresado.");
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<TrabajoApp> buscarPorId(Long trabajoId) {
        return trabajoAppRepository.findById(trabajoId);
    }

    @Transactional(readOnly = true)
    @Override
    public List<TrabajoApp> filtrarTrabajosApp(BuscarTrabajoExternoDTO filtro) throws UsuarioNotFoundException, EspecialistaNotFoundException, TrabajoAppException {
        Especialista especialista = especialistaService.obtenerEspecialistaAutenticado();

        Specification<TrabajoApp> spec = (root, query, criteriaBuilder) -> criteriaBuilder.conjunction();

        if (filtro.tieneTitulo()) {
            spec = spec.and(TrabajoAppSpecifications.tituloEs(filtro.titulo()));
        }
        if (filtro.tieneEstado()) {
            spec = spec.and(TrabajoAppSpecifications.estadoEs(EstadosTrabajos.desdeString(filtro.estado())));
        }
        if (filtro.tieneId()) {
            spec = spec.and(TrabajoAppSpecifications.idEs(filtro.id()));
        }
        if (filtro.tieneFecha()) {
            spec = spec.and(TrabajoAppSpecifications.fechaEntre(filtro.desde(), filtro.hasta()));
        }

        List<TrabajoApp> trabajosEncontrados = trabajoAppRepository.findAll(spec)
                .stream().filter(trabajoExterno -> trabajoExterno.getEspecialista().equals(especialista))
                .toList();

        if (trabajosEncontrados.isEmpty()) {
            throw new TrabajoAppException("\uD83D\uDE13No hay coincidencias con su búsqueda\uD83D\uDE13");
        }

        return trabajosEncontrados;
    }

    @Transactional(readOnly = true)
    @Override
    public List<TrabajoApp> filtrarPorEstadoCliente(String estado) throws UsuarioNotFoundException, TrabajoAppException {
        List<TrabajoApp> trabajosCliente = obtenerTrabajosClientes();

        List<TrabajoApp> trabajosEncontrados = trabajosCliente.stream().filter(t -> t.getEstado().equals(EstadosTrabajos.desdeString(estado))).toList();

        if (trabajosEncontrados.isEmpty()) {
            throw new TrabajoAppException("\uD83D\uDE13No hay coincidencias con su búsqueda\uD83D\uDE13");
        }

        return trabajosEncontrados;
    }
}
