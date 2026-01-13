package com.findfix.find_fix_app.favoritos.service;

import com.findfix.find_fix_app.especialista.model.Especialista;
import com.findfix.find_fix_app.especialista.service.EspecialistaService;
import com.findfix.find_fix_app.favoritos.model.Favorito;
import com.findfix.find_fix_app.favoritos.repository.FavoritoRepository;
import com.findfix.find_fix_app.usuario.model.Usuario;
import com.findfix.find_fix_app.utils.auth.service.AuthServiceImpl;
import com.findfix.find_fix_app.utils.exception.exceptions.EspecialistaNotFoundException;
import com.findfix.find_fix_app.utils.exception.exceptions.FavoritoException;
import com.findfix.find_fix_app.utils.exception.exceptions.UsuarioNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FavoritoServiceImpl implements FavoritoService {
    private final FavoritoRepository favoritoRepository;
    private final EspecialistaService especialistaService;
    private final AuthServiceImpl authServiceImpl;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void agregarAFavoritos(String emailEspecialista) throws UsuarioNotFoundException, EspecialistaNotFoundException, FavoritoException {
        Usuario usuario = authServiceImpl.obtenerUsuarioAutenticado();

        Especialista especialista = especialistaService.buscarPorEmail(emailEspecialista)
                .orElseThrow(() -> new EspecialistaNotFoundException("Especialista no encontrado."));

        validarExistencia(usuario, especialista);

        favoritoRepository.save(new Favorito(usuario, especialista));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void eliminarDeFavoritos(String emailEspecialista) throws UsuarioNotFoundException, EspecialistaNotFoundException {
        Usuario usuario = authServiceImpl.obtenerUsuarioAutenticado();

        Especialista especialista = especialistaService.buscarPorEmail(emailEspecialista)
                .orElseThrow(() -> new EspecialistaNotFoundException("Especialista no encontrado."));

        favoritoRepository.deleteByUsuarioAndEspecialista(usuario, especialista);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Especialista> obtenerFavoritos() throws UsuarioNotFoundException, FavoritoException {
        Usuario usuario = authServiceImpl.obtenerUsuarioAutenticado();

        return favoritoRepository.findAllByUsuario(usuario)
                .stream()
                .map(Favorito::getEspecialista)
                .toList();
    }

    @Override
    public void validarExistencia(Usuario usuario, Especialista especialista) throws FavoritoException {
        if (favoritoRepository.existsByUsuarioAndEspecialista(usuario, especialista)) {
            throw new FavoritoException("El especialista ya se encuentra agregado en su lista.");
        }
    }
}
