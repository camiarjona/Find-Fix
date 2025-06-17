package com.findfix.find_fix_app.usuario.service;

import com.findfix.find_fix_app.utils.auth.AuthService;
import com.findfix.find_fix_app.utils.enums.CiudadesDisponibles;
import com.findfix.find_fix_app.utils.exception.exceptions.RolException;
import com.findfix.find_fix_app.utils.exception.exceptions.RolNotFoundException;
import com.findfix.find_fix_app.utils.exception.exceptions.UserException;
import com.findfix.find_fix_app.utils.exception.exceptions.UserNotFoundException;
import com.findfix.find_fix_app.rol.model.Rol;
import com.findfix.find_fix_app.rol.repository.RolRepository;
import com.findfix.find_fix_app.usuario.specifications.UsuarioSpecifications;
import com.findfix.find_fix_app.usuario.dto.*;
import com.findfix.find_fix_app.usuario.model.Usuario;
import com.findfix.find_fix_app.usuario.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UsuarioServiceImpl implements UsuarioService, UserDetailsService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final RolRepository rolRepository;
    private final AuthService authService;

    //metodo para guardar un usuario basico
    @Override
    public void actualizarUsuarioEspecialista(Usuario usuario) {
        usuarioRepository.save(usuario);
    }

    @Override
    public List<String> ciudadesDisponibles() {
        return CiudadesDisponibles.ciudadesDisponibles();
    }

    @Override
    public List<Usuario> filtrarUsuarios(BuscarUsuarioDTO filtro) throws UserException {
        Specification<Usuario> spec = (root, query, cb) -> cb.conjunction();

        if(filtro.tieneRol()) {
            spec = spec.and(UsuarioSpecifications.tieneRol(filtro.rol()));
        }
        if(filtro.tieneRoles()) {
            spec = spec.and(UsuarioSpecifications.tieneAlgunRol(filtro.roles()));
        }
        if(filtro.tieneEmail()) {
            spec = spec.and(UsuarioSpecifications.tieneEmail(filtro.email()));
        }
        if(filtro.tieneId()){
            spec = spec.and(UsuarioSpecifications.tieneId(filtro.id()));
        }

        List<Usuario> usuariosEncontrados = usuarioRepository.findAll(spec);

        if(usuariosEncontrados.isEmpty()){
            throw new UserException("\uD83D\uDE13No hay coincidencias con su búsqueda\uD83D\uDE13");
        }

        return usuariosEncontrados;
    }

    // metodo para obtener lista completa de usuarios
    @Override
    public List<Usuario> obtenerUsuarios() {
        return usuarioRepository.findAll();
    }

    //metodo para registrar un usuario nuevo
    @Override
    public void registrarNuevoUsuario(RegistroDTO registroDTO) throws RolException, UserException {

        if (usuarioRepository.findByEmail(registroDTO.email()).isPresent()) {
            throw new UserException("❗Ya existe un usuario registrado con ese email.");
        }

        Usuario usuario = new Usuario();

        usuario.setEmail(registroDTO.email());
        usuario.setPassword(passwordEncoder.encode(registroDTO.password()));
        usuario.setNombre(registroDTO.nombre());
        usuario.setApellido(registroDTO.apellido());
        usuario.setCiudad(CiudadesDisponibles.NO_ESPECIFICADO);
        usuario.setTelefono("No especificado.");

        Rol rol;

        if (obtenerUsuarios().isEmpty()) {
            //si la lista esta vacia, al primer usuario creado se le asigna rol de admin
            rol = rolRepository.findByNombre("ADMIN")
                    .orElseThrow(() -> new RolException("❌Rol no encontrado❌"));
            usuario.getRoles().add(rol);
        } else {
            //Se registra con rol cliente por default
            rol = rolRepository.findByNombre("CLIENTE")
                    .orElseThrow(() -> new RolException("❌Rol no encontrado❌"));

            usuario.getRoles().add(rol);
        }

        usuarioRepository.save(usuario);
    }

    // metodo para eliminar un usuario por id
    @Override
    public void eliminarPorId(Long id) throws UserNotFoundException {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("❌Usuario no encontrado❌"));

        usuarioRepository.delete(usuario);
    }

    // metodo para actualizar la contraseña de un usuario
    @Override
    public void actualizarPassword(ActualizarPasswordDTO actualizarPasswordDTO) throws UserNotFoundException {

        Usuario usuario = authService.obtenerUsuarioAutenticado();

        if (!passwordEncoder.matches(actualizarPasswordDTO.passwordActual(), usuario.getPassword())) {
            throw new IllegalArgumentException("❌La contraseña actual es incorrecta❌");
        }

        usuario.setPassword(passwordEncoder.encode(actualizarPasswordDTO.passwordNuevo()));
        usuarioRepository.save(usuario);
    }

    // metodo para actualizar atributos de un usuario
    @Override
    public void actualizarUsuario(ActualizarUsuarioDTO actualizarUsuarioDTO) throws UserNotFoundException {

        Usuario usuario = authService.obtenerUsuarioAutenticado();

        actualizarDatosAModificar(actualizarUsuarioDTO, usuario);

        usuarioRepository.save(usuario);
    }

    @Override
    public void actualizarRolesUsuario(String email, ActualizarRolesUsuarioDTO usuarioRolesDTO) throws UserNotFoundException {
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("❌Usuario no encontrado❌"));

        // agregar roles
        if (usuarioRolesDTO.tieneRolesAgregar()) {
            Set<Rol> rolesAAgregar = usuarioRolesDTO.rolesAgregar().stream()
                    .map(nombre -> rolRepository.findByNombre(nombre)
                            .orElseThrow(() -> new RuntimeException("❌Rol no encontrado: " + nombre)))
                    .collect(Collectors.toSet());

            usuario.getRoles().addAll(rolesAAgregar);
        }

        //eliminar roles
        if (usuarioRolesDTO.tieneRolesEliminar()) {
            for (String nombreRol : usuarioRolesDTO.rolesEliminar()) {
                usuario.getRoles().removeIf(r -> r.getNombre().equalsIgnoreCase(nombreRol));
            }
        }

        usuarioRepository.save(usuario);
    }

    //metodo para eliminar un usuario por su email
    @Override
    public void eliminarPorEmail(String email) throws UserNotFoundException {
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("❌Usuario no encontrado❌"));

        usuarioRepository.delete(usuario);
    }

    //metodo para que el admin pueda actualizar los datos de un usuario
    @Override
    public void actualizarUsuarioAdmin(ActualizarUsuarioDTO actualizarUsuarioDTO, String email) throws UserNotFoundException {
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("❌Usuario no encontrado❌"));

        actualizarDatosAModificar(actualizarUsuarioDTO, usuario);

        usuarioRepository.save(usuario);
    }

    public void actualizarDatosAModificar(ActualizarUsuarioDTO actualizarUsuarioDTO, Usuario usuario){

        if (actualizarUsuarioDTO.tieneNombre()) {
            usuario.setNombre(actualizarUsuarioDTO.nombre());
        }
        if (actualizarUsuarioDTO.tieneApellido()) {
            usuario.setApellido(actualizarUsuarioDTO.apellido());
        }
        if (actualizarUsuarioDTO.tieneTelefono()) {
            usuario.setTelefono(actualizarUsuarioDTO.telefono());
        }
        if (actualizarUsuarioDTO.tieneCiudad()) {
            usuario.setCiudad(CiudadesDisponibles.desdeString(actualizarUsuarioDTO.ciudad()));
        }
    }

    // metodo para asignar un rol específico a un usuario (para solicitudes)
    @Override
    public void agregarRol(Usuario usuario, String nombreRol) throws RolNotFoundException {
        Rol rol = rolRepository.findByNombre(nombreRol)
                .orElseThrow(() -> new RolNotFoundException("❌Rol no encontrado❌"));

        usuario.getRoles().add(rol);
        usuarioRepository.save(usuario);
    }

    //metodo para que un usuario visualice su perfil
    @Override
    public VerPerfilUsuarioDTO verPerfilUsuario() throws UserNotFoundException {
        Usuario usuario = authService.obtenerUsuarioAutenticado();
        return new VerPerfilUsuarioDTO(usuario);
    }

    // Metodo para buscar un usuario por su email para autenticacion
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<Usuario> userOptional = usuarioRepository.findByEmail(username);
        Usuario usuario = userOptional.orElseThrow(() -> new UsernameNotFoundException("❌Usuario no encontrado❌"));

        // retorna un user de spring security
        return new org.springframework.security.core.userdetails.User(
                usuario.getEmail(),
                usuario.getPassword(),
                getAuthorities(usuario.getRoles())
        );
    }

    // Metodo que convierte los roles del usuario en autoridades para spring security
    private Collection<? extends GrantedAuthority> getAuthorities(Set<Rol> roles) {
        // Prefijo "ROLE_" es requerido por Spring Security para roles
        return roles.stream()
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role.getNombre()))
                .collect(Collectors.toList());
    }


}
