package com.findfix.find_fix_app.favoritos.service;

import com.findfix.find_fix_app.especialista.model.Especialista;
import com.findfix.find_fix_app.especialista.service.EspecialistaService;
import com.findfix.find_fix_app.favoritos.model.Favorito;
import com.findfix.find_fix_app.favoritos.repository.FavoritoRepository;
import com.findfix.find_fix_app.usuario.model.Usuario;
import com.findfix.find_fix_app.usuario.service.UsuarioService;
import com.findfix.find_fix_app.utils.auth.AuthService;
import com.findfix.find_fix_app.utils.exception.exceptions.EspecialistaNotFoundException;
import com.findfix.find_fix_app.utils.exception.exceptions.FavoritoException;
import com.findfix.find_fix_app.utils.exception.exceptions.UserNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FavoritoServiceImpl implements FavoritoService {
    private final FavoritoRepository favoritoRepository;
    private final UsuarioService usuarioService;
    private final EspecialistaService especialistaService;
    private final AuthService authService;

    @Override
    public void agregarAFavoritos(String emailEspecialista) throws UserNotFoundException, EspecialistaNotFoundException, FavoritoException {
        Usuario usuario = authService.obtenerUsuarioAutenticado();

        Especialista especialista = especialistaService.buscarPorEmail(emailEspecialista)
                .orElseThrow(() -> new EspecialistaNotFoundException("Especialista no encontrado."));

        validarExistencia(usuario, especialista);

        favoritoRepository.save(new Favorito(usuario, especialista));
    }

    @Override
    public void eliminarDeFavoritos(String emailEspecialista) throws UserNotFoundException, EspecialistaNotFoundException {
        Usuario usuario = authService.obtenerUsuarioAutenticado();

        Especialista especialista = especialistaService.buscarPorEmail(emailEspecialista)
                .orElseThrow(() -> new EspecialistaNotFoundException("Especialista no encontrado."));

        favoritoRepository.deleteByUsuarioAndEspecialista(usuario, especialista);
    }

    @Override
    public List<Especialista> obtenerFavoritos() throws UserNotFoundException {
        Usuario usuario = authService.obtenerUsuarioAutenticado();
        return favoritoRepository.findAllByUsuario(usuario)
                .stream()
                .map(Favorito::getEspecialista)
                .collect(Collectors.toList());
    }

    @Override
    public void validarExistencia(Usuario usuario, Especialista especialista) throws FavoritoException {
        if (favoritoRepository.existsByUsuarioAndEspecialista(usuario, especialista)) {
            throw new FavoritoException("El especialista ya se encuentra agregado en su lista.");
        }
    }
}
