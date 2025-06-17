package com.findfix.find_fix_app.favoritos.service;

import com.findfix.find_fix_app.especialista.model.Especialista;
import com.findfix.find_fix_app.usuario.model.Usuario;
import com.findfix.find_fix_app.utils.exception.exceptions.EspecialistaNotFoundException;
import com.findfix.find_fix_app.utils.exception.exceptions.FavoritoException;
import com.findfix.find_fix_app.utils.exception.exceptions.UserNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface FavoritoService {
    void agregarAFavoritos(String emailEspecialista) throws UserNotFoundException, EspecialistaNotFoundException, FavoritoException;
    void eliminarDeFavoritos(String emailEspecialista) throws UserNotFoundException, EspecialistaNotFoundException;
    List<Especialista> obtenerFavoritos() throws UserNotFoundException, FavoritoException;
    void validarExistencia(Usuario usuario, Especialista especialista) throws FavoritoException;
}
