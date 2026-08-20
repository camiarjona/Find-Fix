package com.findfix.find_fix_app.utils.initializer;

import com.findfix.find_fix_app.especialista.model.Especialista;
import com.findfix.find_fix_app.especialista.repository.EspecialistaRepository;
import com.findfix.find_fix_app.favoritos.model.Favorito;
import com.findfix.find_fix_app.favoritos.repository.FavoritoRepository;
import com.findfix.find_fix_app.oficio.model.Oficio;
import com.findfix.find_fix_app.oficio.repository.OficioRepository;
import com.findfix.find_fix_app.resena.model.Resena;
import com.findfix.find_fix_app.resena.repository.ResenaRepository;
import com.findfix.find_fix_app.rol.model.Rol;
import com.findfix.find_fix_app.rol.repository.RolRepository;
import com.findfix.find_fix_app.solicitudTrabajo.model.SolicitudTrabajo;
import com.findfix.find_fix_app.solicitudTrabajo.repository.SolicitudTrabajoRepository;
import com.findfix.find_fix_app.trabajo.trabajoApp.model.TrabajoApp;
import com.findfix.find_fix_app.trabajo.trabajoApp.repository.TrabajoAppRepository;
import com.findfix.find_fix_app.trabajo.trabajoExterno.model.TrabajoExterno;
import com.findfix.find_fix_app.trabajo.trabajoExterno.repository.TrabajoExternoRepository;
import com.findfix.find_fix_app.usuario.model.Usuario;
import com.findfix.find_fix_app.usuario.repository.UsuarioRepository;
import com.findfix.find_fix_app.utils.enums.EstadosSolicitudes;
import com.findfix.find_fix_app.utils.enums.EstadosTrabajos;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private final RolRepository rolRepository;
    private final OficioRepository oficioRepository;
    private final UsuarioRepository usuarioRepository;
    private final EspecialistaRepository especialistaRepository;
    private final SolicitudTrabajoRepository solicitudTrabajoRepository;
    private final TrabajoAppRepository trabajoAppRepository;
    private final TrabajoExternoRepository trabajoExternoRepository;
    private final ResenaRepository resenaRepository;
    private final FavoritoRepository favoritoRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        precargarRoles();
        precargarOficios();
        precargarAdmin();

        List<Usuario> clientes = precargarClientes();
        List<Especialista> especialistas = precargarEspecialistas();

        List<SolicitudTrabajo> solicitudes = precargarSolicitudesTrabajo(clientes, especialistas);
        precargarTrabajosApp(solicitudes);
        precargarTrabajosExternos(especialistas);
        precargarFavoritos(clientes, especialistas);
    }


    private void precargarRoles() {
        List<String> roles = List.of("CLIENTE", "ESPECIALISTA", "ADMIN");

        for (String nombre : roles) {
            if (!rolRepository.existsByNombre(nombre)) {
                rolRepository.save(new Rol(null, nombre));
                log.info("Rol precargado: {}", nombre);
            }
        }
    }

    private void precargarOficios() {
        List<String> oficios = List.of("ELECTRICISTA", "PLOMERO", "CARPINTERO", "PINTOR", "GASISTA");

        for (String nombre : oficios) {
            if (!oficioRepository.existsByNombreIgnoreCase(nombre)) {
                oficioRepository.save(new Oficio(null, nombre));
                log.info("Oficio precargado: {}", nombre);
            }
        }
    }

    private void precargarAdmin() {
        String emailAdmin = "findfixapp.utn@gmail.com";

        if (!usuarioRepository.existsByEmail(emailAdmin)) {
            Rol rolAdmin = rolRepository.findByNombre("ADMIN")
                    .orElseThrow(() -> new RuntimeException("Rol ADMIN no encontrado"));

            Usuario admin = new Usuario();
            admin.setEmail(emailAdmin);
            admin.setNombre("Administrador");
            admin.setApellido("Principal");
            admin.setPassword(passwordEncoder.encode("admin123"));
            admin.setRoles(Set.of(rolAdmin));
            admin.setCiudad("No especificado");
            admin.setTelefono("No especificado");

            usuarioRepository.save(admin);
            log.info("Usuario ADMIN creado correctamente con email: {} y contrasena: admin123", emailAdmin);
        } else {
            log.info("Ya existe un usuario administrador con email: {}", emailAdmin);
        }
    }


    private List<Usuario> precargarClientes() {
        List<Usuario> clientes = new ArrayList<>();

        Rol rolCliente = rolRepository.findByNombre("CLIENTE")
                .orElseThrow(() -> new RuntimeException("Rol CLIENTE no encontrado"));

        Object[][] datos = {
                {"ana.torres@example.com", "Ana", "Torres", "223-4001111", "Centro", -38.0055, -57.5426},
                {"pedro.diaz@example.com", "Pedro", "Diaz", "223-4002222", "Constitucion", -37.9930, -57.5680},
                {"sofia.martinez@example.com", "Sofia", "Martinez", "223-4003333", "La Perla", -38.0010, -57.5300},
                {"lucas.romero@example.com", "Lucas", "Romero", "223-4004444", "Playa Grande", -38.0190, -57.5340},
                {"valentina.gil@example.com", "Valentina", "Gil", "223-4005555", "San Carlos", -37.9700, -57.5900},
                {"matias.herrera@example.com", "Matias", "Herrera", "223-4006666", "Puerto", -38.0330, -57.5330},
                {"camila.ortiz@example.com", "Camila", "Ortiz", "223-4007777", "Los Troncos", -37.9980, -57.5480},
                {"nicolas.vega@example.com", "Nicolas", "Vega", "223-4008888", "Chauvin", -38.0090, -57.5500},
        };

        for (Object[] d : datos) {
            String email = (String) d[0];

            if (usuarioRepository.existsByEmail(email)) {
                usuarioRepository.findByEmail(email).ifPresent(clientes::add);
                continue;
            }

            Usuario cliente = new Usuario();
            cliente.setEmail(email);
            cliente.setNombre((String) d[1]);
            cliente.setApellido((String) d[2]);
            cliente.setPassword(passwordEncoder.encode("cliente123"));
            cliente.setTelefono((String) d[3]);
            cliente.setCiudad((String) d[4]);
            cliente.setLatitud((Double) d[5]);
            cliente.setLongitud((Double) d[6]);
            cliente.setActivo(true);
            cliente.setRoles(Set.of(rolCliente));

            clientes.add(usuarioRepository.save(cliente));
            log.info("Cliente precargado: {}", email);
        }

        return clientes;
    }


    private List<Especialista> precargarEspecialistas() {
        List<Especialista> especialistas = new ArrayList<>();

        Rol rolEspecialista = rolRepository.findByNombre("ESPECIALISTA")
                .orElseThrow(() -> new RuntimeException("Rol ESPECIALISTA no encontrado"));
        Rol rolCliente = rolRepository.findByNombre("CLIENTE")
                .orElseThrow(() -> new RuntimeException("Rol CLIENTE no encontrado"));

        // email, nombre, apellido, telefono, barrio, lat, lon, dni, descripcion, oficios[]
        Object[][] datos = {
                {"juan.perez@example.com", "Juan", "Perez", "223-5001111", "San Carlos", -37.9700, -57.5900,
                        30111222L, "Electricista matriculado con 10 anios de experiencia en instalaciones domiciliarias e industriales.",
                        new String[]{"ELECTRICISTA"}},
                {"martin.alvarez@example.com", "Martin", "Alvarez", "223-5001112", "Sierra de los Padres", -37.9350, -57.6300,
                        30111223L, "Electricista especializado en tableros y automatizacion basica del hogar.",
                        new String[]{"ELECTRICISTA"}},
                {"marta.gomez@example.com", "Marta", "Gomez", "223-5002222", "Los Troncos", -37.9980, -57.5480,
                        29222333L, "Especialista en plomeria, destapaciones y reparacion de canierias.",
                        new String[]{"PLOMERO"}},
                {"diego.sosa@example.com", "Diego", "Sosa", "223-5005555", "Divino Rostro", -38.0250, -57.5650,
                        28555666L, "Gasista matriculado, tambien realiza trabajos de plomeria general.",
                        new String[]{"GASISTA", "PLOMERO"}},
                {"carlos.ruiz@example.com", "Carlos", "Ruiz", "223-5003333", "Puerto", -38.0330, -57.5330,
                        27333444L, "Carpintero especializado en muebles a medida y restauracion.",
                        new String[]{"CARPINTERO"}},
                {"valeria.nunez@example.com", "Valeria", "Nunez", "223-5003334", "Constitucion", -37.9930, -57.5680,
                        27333445L, "Carpintera especializada en mobiliario de oficina y placares a medida.",
                        new String[]{"CARPINTERO"}},
                {"laura.fernandez@example.com", "Laura", "Fernandez", "223-5004444", "Chauvin", -38.0090, -57.5500,
                        31444555L, "Pintora de interiores y exteriores, trabajos residenciales y comerciales.",
                        new String[]{"PINTOR"}},
                {"ezequiel.molina@example.com", "Ezequiel", "Molina", "223-5004445", "Playa Grande", -38.0190, -57.5340,
                        31444556L, "Pintor especializado en revestimientos texturados y fachadas.",
                        new String[]{"PINTOR"}},
                {"gustavo.paz@example.com", "Gustavo", "Paz", "223-5005556", "Centro", -38.0055, -57.5426,
                        28555667L, "Gasista matriculado, especializado en habilitaciones comerciales.",
                        new String[]{"GASISTA"}},
                {"rocio.medina@example.com", "Rocio", "Medina", "223-5005557", "La Perla", -38.0010, -57.5300,
                        28555668L, "Gasista con experiencia en instalaciones residenciales nuevas.",
                        new String[]{"GASISTA"}},
        };

        for (Object[] d : datos) {
            String email = (String) d[0];

            if (usuarioRepository.existsByEmail(email)) {
                usuarioRepository.findByEmail(email)
                        .flatMap(especialistaRepository::findByUsuario)
                        .ifPresent(especialistas::add);
                continue;
            }

            Usuario usuario = new Usuario();
            usuario.setEmail(email);
            usuario.setNombre((String) d[1]);
            usuario.setApellido((String) d[2]);
            usuario.setPassword(passwordEncoder.encode("especialista123"));
            usuario.setTelefono((String) d[3]);
            usuario.setCiudad((String) d[4]);
            usuario.setLatitud((Double) d[5]);
            usuario.setLongitud((Double) d[6]);
            usuario.setActivo(true);
            usuario.setRoles(Set.of(rolEspecialista, rolCliente));
            usuario = usuarioRepository.save(usuario);

            Set<Oficio> oficios = new HashSet<>();
            for (String nombreOficio : (String[]) d[9]) {
                oficioRepository.findByNombre(nombreOficio)
                        .ifPresent(oficios::add);
            }

            Especialista especialista = new Especialista();
            especialista.setDni((Long) d[7]);
            especialista.setDescripcion((String) d[8]);
            especialista.setUsuario(usuario);
            especialista.setOficios(oficios);

            especialistas.add(especialistaRepository.save(especialista));
            log.info("Especialista precargado (tambien cliente): {}", email);
        }

        return especialistas;
    }


    private List<SolicitudTrabajo> precargarSolicitudesTrabajo(List<Usuario> clientes, List<Especialista> especialistas) {
        List<SolicitudTrabajo> solicitudes = new ArrayList<>();

        if (clientes.size() < 8 || especialistas.size() < 10) {
            log.warn("No hay suficientes clientes/especialistas para precargar solicitudes.");
            return solicitudes;
        }

        if (solicitudTrabajoRepository.count() > 0) {
            log.info("Ya existen solicitudes de trabajo cargadas, se omite la precarga.");
            return solicitudTrabajoRepository.findAll();
        }

        Object[][] datos = {
                {clientes.get(0), especialistas.get(0), "Necesito instalar tomas nuevas en la cocina.", EstadosSolicitudes.ACEPTADO, 39},
                {clientes.get(1), especialistas.get(2), "Perdida de agua debajo de la pileta de cocina.", EstadosSolicitudes.ACEPTADO, 37},
                {clientes.get(2), especialistas.get(4), "Fabricacion de biblioteca a medida para living.", EstadosSolicitudes.ACEPTADO, 34},
                {clientes.get(3), especialistas.get(6), "Pintura completa de departamento de 2 ambientes.", EstadosSolicitudes.ACEPTADO, 29},
                {clientes.get(4), especialistas.get(8), "Revision de instalacion de gas para habilitacion.", EstadosSolicitudes.ACEPTADO, 24},
                {clientes.get(5), especialistas.get(1), "Cambio de tablero electrico completo.", EstadosSolicitudes.ACEPTADO, 19},
                {clientes.get(6), especialistas.get(3), "Destapacion de canieria principal.", EstadosSolicitudes.ACEPTADO, 17},
                {clientes.get(7), especialistas.get(5), "Restauracion de mesa de living.", EstadosSolicitudes.ACEPTADO, 14},
                {clientes.get(0), especialistas.get(7), "Pintura de fachada de casa.", EstadosSolicitudes.ACEPTADO, 11},
                {clientes.get(1), especialistas.get(9), "Instalacion de cocina a gas nueva.", EstadosSolicitudes.ACEPTADO, 8},

                {clientes.get(2), especialistas.get(0), "Cortocircuito recurrente en el tablero principal.", EstadosSolicitudes.PENDIENTE, 3},
                {clientes.get(3), especialistas.get(2), "Perdida en canilla de patio.", EstadosSolicitudes.PENDIENTE, 2},
                {clientes.get(4), especialistas.get(5), "Reparacion de puerta de placard.", EstadosSolicitudes.PENDIENTE, 1},
                {clientes.get(5), especialistas.get(7), "Pintura de habitacion infantil.", EstadosSolicitudes.PENDIENTE, 1},

                {clientes.get(6), especialistas.get(1), "Instalacion de aire acondicionado.", EstadosSolicitudes.RECHAZADO, 10},
                {clientes.get(7), especialistas.get(8), "Revision de calefon.", EstadosSolicitudes.RECHAZADO, 8},
        };

        for (Object[] d : datos) {
            SolicitudTrabajo solicitud = new SolicitudTrabajo();
            solicitud.setUsuario((Usuario) d[0]);
            solicitud.setEspecialista((Especialista) d[1]);
            solicitud.setDescripcion((String) d[2]);
            solicitud.setEstado((EstadosSolicitudes) d[3]);

            int diasAtras = (int) d[4];
            LocalDate fechaCreacion = LocalDate.now().minusDays(diasAtras);
            solicitud.setFechaCreacion(fechaCreacion);

            if (solicitud.getEstado() != EstadosSolicitudes.PENDIENTE) {
                solicitud.setFechaResolucion(fechaCreacion.plusDays(1));
            }

            solicitudes.add(solicitudTrabajoRepository.save(solicitud));
        }

        log.info("{} solicitudes de trabajo precargadas.", solicitudes.size());
        return solicitudes;
    }

    private void precargarTrabajosApp(List<SolicitudTrabajo> solicitudes) {
        if (solicitudes.size() < 10) {
            log.warn("No hay suficientes solicitudes aceptadas para precargar trabajos app.");
            return;
        }

        if (trabajoAppRepository.count() > 0) {
            log.info("Ya existen trabajos app cargados, se omite la precarga.");
            return;
        }

        Object[][] datos = {
                {"Instalacion electrica cocina", "Tomas nuevas y reordenamiento de tablero seccional.", EstadosTrabajos.FINALIZADO, 85000.0, 38, 35, 5.0, "Excelente trabajo, muy prolijo y puntual."},
                {"Reparacion de perdida en pileta", "Cambio de sifon y sellado de canierias.", EstadosTrabajos.FINALIZADO, 32000.0, 36, 34, 4.5, "Solucion el problema rapido, buen trato."},
                {"Biblioteca a medida living", "Fabricacion e instalacion de biblioteca de melamina.", EstadosTrabajos.EN_REVISION, 150000.0, 33, -1, null, null},
                {"Pintura departamento 2 ambientes", "Pintura completa interior, dos manos de latex.", EstadosTrabajos.EN_PROCESO, 120000.0, 28, -1, null, null},
                {"Revision instalacion de gas", "Chequeo de instalacion completa para habilitacion.", EstadosTrabajos.FINALIZADO, 45000.0, 23, 21, 4.0, "Buen servicio, cumplio los tiempos."},
                {"Cambio de tablero electrico", "Reemplazo completo de tablero y disyuntores.", EstadosTrabajos.EN_REVISION, 98000.0, 18, -1, null, null},
                {"Destapacion canieria principal", "Destapacion con maquina y verificacion de perdidas.", EstadosTrabajos.CREADO, 40000.0, 16, -1, null, null},
                {"Restauracion de mesa de living", "Lijado, tratamiento y barnizado de mesa de madera maciza.", EstadosTrabajos.EN_PROCESO, 60000.0, 13, -1, null, null},
                {"Pintura de fachada", "Pintura exterior con imprimacion y sellador.", EstadosTrabajos.CREADO, 90000.0, 10, -1, null, null},
                {"Instalacion cocina a gas nueva", "Conexion y verificacion de fugas de cocina nueva.", EstadosTrabajos.FINALIZADO, 55000.0, 7, 5, 5.0, "Instalacion impecable, muy recomendable."},
        };

        for (int i = 0; i < datos.length; i++) {
            Object[] d = datos[i];
            SolicitudTrabajo solicitud = solicitudes.get(i);

            TrabajoApp trabajo = new TrabajoApp();
            trabajo.setTitulo((String) d[0]);
            trabajo.setDescripcion((String) d[1]);
            trabajo.setEstado((EstadosTrabajos) d[2]);
            trabajo.setPresupuesto((Double) d[3]);
            trabajo.setEspecialista(solicitud.getEspecialista());
            trabajo.setUsuario(solicitud.getUsuario());
            trabajo.setSolicitudTrabajo(solicitud);

            int diasInicio = (int) d[4];
            trabajo.setFechaInicio(LocalDate.now().minusDays(diasInicio));

            int diasFin = (int) d[5];
            if (diasFin >= 0) {
                trabajo.setFechaFin(LocalDate.now().minusDays(diasFin));
            }

            trabajo = trabajoAppRepository.save(trabajo);

            Double puntuacion = (Double) d[6];
            String comentario = (String) d[7];
            if (trabajo.getEstado() == EstadosTrabajos.FINALIZADO && puntuacion != null) {
                Resena resena = new Resena();
                resena.setPuntuacion(puntuacion);
                resena.setComentario(comentario);
                resena.setTrabajoApp(trabajo);
                resenaRepository.save(resena);
            }
        }

        log.info("Trabajos app y resenias precargados.");
    }


    private void precargarTrabajosExternos(List<Especialista> especialistas) {
        if (especialistas.size() < 10) {
            log.warn("No hay suficientes especialistas para precargar trabajos externos.");
            return;
        }

        if (trabajoExternoRepository.count() > 0) {
            log.info("Ya existen trabajos externos cargados, se omite la precarga.");
            return;
        }

        Object[][] datos = {
                {especialistas.get(0), "Roberto Ledesma", "Tablero electrico local comercial", "Actualizacion de tablero y disyuntores.", EstadosTrabajos.FINALIZADO, 95000.0, 50, 47},
                {especialistas.get(1), "Silvia Paredes", "Instalacion de luminarias LED", "Cambio de luminarias en todo el domicilio.", EstadosTrabajos.EN_PROCESO, 40000.0, 10, -1},
                {especialistas.get(2), "Familia Aguirre", "Cambio de canierias baño", "Reemplazo de canierias de agua caliente y fria.", EstadosTrabajos.FINALIZADO, 60000.0, 45, 42},
                {especialistas.get(3), "Kiosco El Sol", "Conexion de gas cocina industrial", "Instalacion y habilitacion de cocina industrial.", EstadosTrabajos.CREADO, 70000.0, 5, -1},
                {especialistas.get(4), "Maria Elena Suarez", "Deck de madera patio", "Construccion de deck de 20m2.", EstadosTrabajos.EN_PROCESO, 200000.0, 15, -1},
                {especialistas.get(5), "Estudio Contable Reyes", "Muebles de oficina a medida", "Fabricacion de escritorios y estanterias.", EstadosTrabajos.FINALIZADO, 180000.0, 60, 55},
                {especialistas.get(6), "Comercio Don Jose", "Pintura fachada local", "Pintura exterior e imprimacion.", EstadosTrabajos.CREADO, 70000.0, 6, -1},
                {especialistas.get(7), "Familia Ibarra", "Pintura interior casa nueva", "Pintura completa de casa a estrenar.", EstadosTrabajos.EN_REVISION, 130000.0, 20, -1},
                {especialistas.get(8), "Familia Bianchi", "Instalacion de gas cocina nueva", "Conexion de cocina y verificacion de fugas.", EstadosTrabajos.FINALIZADO, 40000.0, 48, 46},
                {especialistas.get(9), "Panaderia La Espiga", "Revision instalacion de gas industrial", "Chequeo completo de instalacion de gas.", EstadosTrabajos.EN_PROCESO, 85000.0, 9, -1},
        };

        for (Object[] d : datos) {
            TrabajoExterno trabajo = new TrabajoExterno();
            trabajo.setEspecialista((Especialista) d[0]);
            trabajo.setNombreCliente((String) d[1]);
            trabajo.setTitulo((String) d[2]);
            trabajo.setDescripcion((String) d[3]);
            trabajo.setEstado((EstadosTrabajos) d[4]);
            trabajo.setPresupuesto((Double) d[5]);

            int diasInicio = (int) d[6];
            trabajo.setFechaInicio(LocalDate.now().minusDays(diasInicio));

            int diasFin = (int) d[7];
            if (diasFin >= 0) {
                trabajo.setFechaFin(LocalDate.now().minusDays(diasFin));
            }

            trabajoExternoRepository.save(trabajo);
        }

        log.info("Trabajos externos precargados.");
    }
    

    private void precargarFavoritos(List<Usuario> clientes, List<Especialista> especialistas) {
        if (clientes.size() < 7 || especialistas.size() < 10) {
            return;
        }

        // cliente, especialista
        Object[][] datos = {
                {clientes.get(0), especialistas.get(2)},
                {clientes.get(0), especialistas.get(4)},
                {clientes.get(1), especialistas.get(0)},
                {clientes.get(2), especialistas.get(6)},
                {clientes.get(3), especialistas.get(8)},
                {clientes.get(4), especialistas.get(1)},
                {clientes.get(5), especialistas.get(9)},
                {clientes.get(6), especialistas.get(5)},
        };

        for (Object[] d : datos) {
            Usuario usuario = (Usuario) d[0];
            Especialista especialista = (Especialista) d[1];

            if (!favoritoRepository.existsByUsuarioAndEspecialista(usuario, especialista)) {
                favoritoRepository.save(new Favorito(usuario, especialista));
            }
        }

        log.info("Favoritos precargados.");
    }
}