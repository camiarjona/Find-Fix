package com.findfix.find_fix_app.favoritos.service;

import com.findfix.find_fix_app.especialista.model.Especialista;
import com.findfix.find_fix_app.usuario.model.Usuario;
import com.findfix.find_fix_app.utils.exception.exceptions.EspecialistaNotFoundException;
import com.findfix.find_fix_app.utils.exception.exceptions.FavoritoException;
import com.findfix.find_fix_app.utils.exception.exceptions.UsuarioNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface FavoritoService {
    void agregarAFavoritos(String emailEspecialista) throws UsuarioNotFoundException, EspecialistaNotFoundException, FavoritoException;
    void eliminarDeFavoritos(String emailEspecialista) throws UsuarioNotFoundException, EspecialistaNotFoundException;
    List<Especialista> obtenerFavoritos() throws UsuarioNotFoundException, FavoritoException;
    void validarExistencia(Usuario usuario, Especialista especialista) throws FavoritoException;
}
