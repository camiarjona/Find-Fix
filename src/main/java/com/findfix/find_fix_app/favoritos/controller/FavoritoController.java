package com.findfix.find_fix_app.favoritos.controller;

import com.findfix.find_fix_app.especialista.dto.EspecialistaListadoDTO;
import com.findfix.find_fix_app.especialista.model.Especialista;
import com.findfix.find_fix_app.favoritos.service.FavoritoService;
import com.findfix.find_fix_app.utils.apiResponse.ApiResponse;
import com.findfix.find_fix_app.utils.exception.exceptions.EspecialistaNotFoundException;
import com.findfix.find_fix_app.utils.exception.exceptions.FavoritoException;
import com.findfix.find_fix_app.utils.exception.exceptions.UserNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Stream;

@RestController
@RequestMapping("/favoritos")
@RequiredArgsConstructor
@Validated
public class FavoritoController {
    private final FavoritoService favoritoService;

    @GetMapping
    public ResponseEntity<ApiResponse<Stream<EspecialistaListadoDTO>>> obtenerFavoritos() throws UserNotFoundException, FavoritoException {
        List<Especialista> favoritos = favoritoService.obtenerFavoritos();

        String mensaje = favoritos.isEmpty() ?
                "No hay especialistas en su lista de favoritos" :
                "⭐Mis favoritos⭐";

        return ResponseEntity.ok(new ApiResponse<>(mensaje,
                favoritos.stream().map(EspecialistaListadoDTO::new)));
    }

    @DeleteMapping("/eliminar/{emailEspecialista}")
    public ResponseEntity<ApiResponse<String>> eliminar(@PathVariable String emailEspecialista) throws UserNotFoundException, EspecialistaNotFoundException {
        favoritoService.eliminarDeFavoritos(emailEspecialista);
        return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse<>(
                "El especialista se ha eliminado de su lista✅",
                "Especialista: " + emailEspecialista));
    }

    @PostMapping("/agregar/{emailEspecialista}")
    public ResponseEntity<ApiResponse<String>> agregar(@PathVariable String emailEspecialista) throws UserNotFoundException, EspecialistaNotFoundException, FavoritoException {
        favoritoService.agregarAFavoritos(emailEspecialista);
        return ResponseEntity.status(HttpStatus.CREATED).body(new ApiResponse<>(
                "Especialista agregado a su lista con éxito✅",
                "Especialista: " + emailEspecialista));
    }

}
