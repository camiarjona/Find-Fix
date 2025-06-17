package com.findfix.find_fix_app.trabajo.trabajoApp.service;

import com.findfix.find_fix_app.utils.auth.AuthService;
import com.findfix.find_fix_app.utils.enums.EstadosTrabajos;
import com.findfix.find_fix_app.especialista.model.Especialista;
import com.findfix.find_fix_app.especialista.service.EspecialistaService;
import com.findfix.find_fix_app.solicitudTrabajo.model.SolicitudTrabajo;
import com.findfix.find_fix_app.trabajo.trabajoApp.dto.ActualizarTrabajoAppDTO;
import com.findfix.find_fix_app.trabajo.trabajoApp.model.TrabajoApp;
import com.findfix.find_fix_app.trabajo.trabajoApp.repository.TrabajoAppRepository;
import com.findfix.find_fix_app.usuario.model.Usuario;
import com.findfix.find_fix_app.usuario.service.UsuarioService;
import com.findfix.find_fix_app.utils.exception.exceptions.EspecialistaNotFoundException;
import com.findfix.find_fix_app.utils.exception.exceptions.TrabajoAppException;
import com.findfix.find_fix_app.utils.exception.exceptions.TrabajoAppNotFoundException;
import com.findfix.find_fix_app.utils.exception.exceptions.UserNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TrabajoAppServiceImpl implements TrabajoAppService {

    private final TrabajoAppRepository trabajoAppRepository;
    private final AuthService authService;
    private final UsuarioService usuarioService;
    private final EspecialistaService especialistaService;

    ///  METODO APRA GUARDAR EL TRABAJO QUE AUTOMATICAMENTE LLEGA UNA VEZ QUE UNA SOLICITUD ES ACEPTADA
    @Override
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
    public List<TrabajoApp> obtenerTrabajosClientes() throws UserNotFoundException, TrabajoAppException {

        Usuario usuario = authService.obtenerUsuarioAutenticado();

        List<TrabajoApp> trabajosSolicitados = trabajoAppRepository.findByUsuario(usuario);

        if (trabajosSolicitados.isEmpty()) {
            throw new TrabajoAppException("Usted no tiene trabajos aceptados.");
        }

        return trabajosSolicitados;
    }

    /// METODO PARA OBTENER LOS TRABAJOS DESDE LA PERSPECTIVA DE LOS ESPECIALISTAS
    @Override
    public List<TrabajoApp> obtenerTrabajosEspecialista() throws UserNotFoundException, TrabajoAppException, EspecialistaNotFoundException {

        Especialista especialista = especialistaService.obtenerEspecialistaAutenticado();
        List<TrabajoApp> trabajosAceptados = trabajoAppRepository.findByEspecialista(especialista);

        if (trabajosAceptados.isEmpty()) {
            throw new TrabajoAppException("Usted no tiene trabajos en su lista.");
        }
        return trabajosAceptados;
    }

    ///  METODO PARA OBTENER LOS TRABAJOS DESDE LA PERSPECTIVA DE LOS ESPECIALISTA Y APLICANDO FILTRO DE UN ESTADO ELEGIDO
    @Override
    public List<TrabajoApp> obtenerTrabajosEspecialistaEstado(String nombreEstado) throws UserNotFoundException, TrabajoAppException, EspecialistaNotFoundException {
        if (!validezIngresoEstado(nombreEstado)) {
            throw new TrabajoAppException("El estado ingresado no es válido.");
        }

        List<TrabajoApp> trabajoApps = obtenerTrabajosEspecialista().stream().filter(trabajo -> trabajo.getEstado()
                .equals(EstadosTrabajos.valueOf(nombreEstado))).collect(Collectors.toList());

        if (trabajoApps.isEmpty()) {
            throw new TrabajoAppException("Usted no tiene ningún trabajo en ese estado.");
        }
        return trabajoApps;
    }

    //  METODO PARA VALIDAR QUE EL ESTADO INGRESADO EXISTA EN EL ENUM ESTADOS
    private Boolean validezIngresoEstado(String nombreEstado) {
        for (EstadosTrabajos e : EstadosTrabajos.values()) {
            if (e.name().equalsIgnoreCase(nombreEstado)) {
                return true;
            }
        }
        return false;
    }

    ///  BUSCA UN TRABAJO POR TITULO
    @Override
    public Optional<TrabajoApp> buscarPorTitulo(String tituloBuscado) {
        return trabajoAppRepository.findByTitulo(tituloBuscado);
    }

    ///  METODO PARA MODIFICAR UN TRABAJO
    @Override
    @Transactional
    public TrabajoApp actualizarTrabajo(String titulo, ActualizarTrabajoAppDTO dto) throws TrabajoAppNotFoundException, TrabajoAppException, UserNotFoundException, EspecialistaNotFoundException {

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
    public void modificarEstadoTrabajo(String titulo, String estadoNuevo) throws TrabajoAppNotFoundException, TrabajoAppException, UserNotFoundException, EspecialistaNotFoundException {
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

    ///  METODO PARA QUE UN CLIENTE OBTENGA UNA FICHA DE UN TRABAJO FILTRANDO POR EL TITULO DEL MISMO
    @Override
    public TrabajoApp obtenerFichaDeTrabajoParaEspecialista(String tituloBuscado) throws TrabajoAppNotFoundException, UserNotFoundException, TrabajoAppException, EspecialistaNotFoundException {
        List<TrabajoApp> trabajoAppDelEspecialista = obtenerTrabajosEspecialista();
        Optional<TrabajoApp> trabajoBuscado = trabajoAppDelEspecialista.stream().filter(trabajoApp -> trabajoApp.getTitulo().equalsIgnoreCase(tituloBuscado)).findFirst();
        if (trabajoBuscado.isEmpty()) {
            throw new TrabajoAppNotFoundException("No se pudo otorgar la ficha trabajo debido a que el titulo ingresado no pertenece a ningun registro. ");
        } else {
            return trabajoBuscado.get();
        }
    }

    @Override
    public TrabajoApp obtenerFichaDeTrabajoParaCliente(Long id) throws UserNotFoundException, TrabajoAppException, TrabajoAppNotFoundException {
        List<TrabajoApp> trabajoAppDelEspecialista = obtenerTrabajosClientes();
        Optional<TrabajoApp> trabajoBuscado = trabajoAppDelEspecialista.stream().filter(trabajoApp -> trabajoApp.getTrabajoAppId() == id).findFirst();
        if (trabajoBuscado.isEmpty()) {
            throw new TrabajoAppNotFoundException("No se pudo otorgar la ficha trabajo debido a que el id ingresado no pertenece a ningun registro. ");
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
}
