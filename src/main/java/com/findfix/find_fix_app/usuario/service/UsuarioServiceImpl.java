package com.findfix.find_fix_app.usuario.service;

import com.findfix.find_fix_app.utils.auth.AuthService;
import com.findfix.find_fix_app.utils.enums.CiudadesDisponibles;
import com.findfix.find_fix_app.utils.exception.exceptions.*;
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
import org.springframework.transaction.annotation.Transactional;

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
    private final UsuarioDesvinculacionService usuarioDesvinculacionService;

    //metodo para guardar un usuario basico
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void actualizarUsuarioEspecialista(Usuario usuario) {
        usuarioRepository.save(usuario);
    }

    @Override
    @Transactional(readOnly = true)
    public List<String> ciudadesDisponibles() {
        return CiudadesDisponibles.ciudadesDisponibles();
    }

    @Override
    public boolean tieneRol(Usuario usuario, String rol) {
        return usuario.getRoles().stream().anyMatch(r -> r.getNombre().equalsIgnoreCase(rol));
    }

    @Override
    @Transactional(readOnly = true)
    public List<Usuario> filtrarUsuarios(BuscarUsuarioDTO filtro) throws UsuarioException {
        Specification<Usuario> spec = (root, query, cb) -> cb.conjunction();

        if (filtro.tieneRol()) {
            spec = spec.and(UsuarioSpecifications.tieneRol(filtro.rol()));
        }
        if (filtro.tieneRoles()) {
            spec = spec.and(UsuarioSpecifications.tieneAlgunRol(filtro.roles()));
        }
        if (filtro.tieneEmail()) {
            spec = spec.and(UsuarioSpecifications.tieneEmail(filtro.email()));
        }
        if (filtro.tieneId()) {
            spec = spec.and(UsuarioSpecifications.tieneId(filtro.id()));
        }

        List<Usuario> usuariosEncontrados = usuarioRepository.findAll(spec);

        if (usuariosEncontrados.isEmpty()) {
            throw new UsuarioException("\uD83D\uDE13No hay coincidencias con su búsqueda\uD83D\uDE13");
        }

        return usuariosEncontrados;
    }

    // metodo para obtener lista completa de usuarios
    @Override
    @Transactional(readOnly = true)
    public List<Usuario> obtenerUsuarios() {
        return usuarioRepository.findAll();
    }

    //metodo para registrar un usuario nuevo
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void registrarNuevoUsuario(RegistroDTO registroDTO) throws RolException, UsuarioException {

        if (usuarioRepository.findByEmail(registroDTO.email()).isPresent()) {
            throw new UsuarioException("❗Ya existe un usuario registrado con ese email.");
        }

        Usuario usuario = new Usuario();

        usuario.setEmail(registroDTO.email());
        usuario.setPassword(passwordEncoder.encode(registroDTO.password()));
        usuario.setNombre(registroDTO.nombre());
        usuario.setApellido(registroDTO.apellido());
        usuario.setCiudad(CiudadesDisponibles.NO_ESPECIFICADO);
        usuario.setTelefono("No especificado.");

        Rol rol;

        //Se registra con rol cliente por default
        rol = rolRepository.findByNombre("CLIENTE")
                .orElseThrow(() -> new RolException("❌Rol no encontrado❌"));

        usuario.getRoles().add(rol);


        usuarioRepository.save(usuario);
    }

    // metodo para actualizar la contraseña de un usuario
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void actualizarPassword(ActualizarPasswordDTO actualizarPasswordDTO) throws UsuarioNotFoundException {

        Usuario usuario = authService.obtenerUsuarioAutenticado();

        if (!passwordEncoder.matches(actualizarPasswordDTO.passwordActual(), usuario.getPassword())) {
            throw new IllegalArgumentException("❌La contraseña actual es incorrecta❌");
        }

        usuario.setPassword(passwordEncoder.encode(actualizarPasswordDTO.passwordNuevo()));
        usuarioRepository.save(usuario);
    }

    // metodo para actualizar atributos de un usuario
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void actualizarUsuario(ActualizarUsuarioDTO actualizarUsuarioDTO) throws UsuarioNotFoundException {

        Usuario usuario = authService.obtenerUsuarioAutenticado();

        actualizarDatosAModificar(actualizarUsuarioDTO, usuario);

        usuarioRepository.save(usuario);
    }

    //metodo para eliminar un usuario por su email
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void eliminarPorEmail(String email) throws UsuarioNotFoundException {
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new UsuarioNotFoundException("❌Usuario no encontrado❌"));

        usuarioDesvinculacionService.desvincularUsuario(usuario);

        usuarioRepository.delete(usuario);
    }

    @Override
    public Optional<Usuario> obtenerUsuarioPorEmail(String email) {
        return usuarioRepository.findByEmail(email);
    }

    //metodo para que el admin pueda actualizar los datos de un usuario
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void actualizarUsuarioAdmin(ActualizarUsuarioDTO actualizarUsuarioDTO, String email) throws UsuarioNotFoundException {
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new UsuarioNotFoundException("❌Usuario no encontrado❌"));

        actualizarDatosAModificar(actualizarUsuarioDTO, usuario);

        usuarioRepository.save(usuario);
    }

    @Transactional(rollbackFor = Exception.class)
    public void actualizarDatosAModificar(ActualizarUsuarioDTO actualizarUsuarioDTO, Usuario usuario) {

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
    @Transactional(rollbackFor = Exception.class)
    public void agregarRol(Usuario usuario, String nombreRol) throws RolNotFoundException {
        Rol rol = rolRepository.findByNombre(nombreRol)
                .orElseThrow(() -> new RolNotFoundException("❌Rol no encontrado❌"));

        usuario.getRoles().add(rol);
        usuarioRepository.save(usuario);
    }

    //metodo para eliminar un rol
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void eliminarRol(Usuario usuario, String nombreRol) throws RolNotFoundException {
        Rol rol = rolRepository.findByNombre(nombreRol)
                .orElseThrow(() -> new RolNotFoundException("❌Rol no encontrado❌"));

        usuario.getRoles().remove(rol);
        usuarioRepository.save(usuario);
    }

    //metodo para que un usuario visualice su perfil
    @Override
    @Transactional(readOnly = true)
    public VerPerfilUsuarioDTO verPerfilUsuario() throws UsuarioNotFoundException {
        Usuario usuario = authService.obtenerUsuarioAutenticado();
        return new VerPerfilUsuarioDTO(usuario);
    }

    // Metodo para buscar un usuario por su email para autenticacion
    @Override
    @Transactional(readOnly = true)
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
