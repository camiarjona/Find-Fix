package com.findfix.find_fix_app.trabajo.service;

import com.findfix.find_fix_app.utils.exception.exceptions.EspecialistaNotFoundException;
import com.findfix.find_fix_app.utils.exception.exceptions.TrabajoAppException;
import com.findfix.find_fix_app.utils.exception.exceptions.TrabajoExternoException;
import com.findfix.find_fix_app.utils.exception.exceptions.UsuarioNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public interface TrabajoService {
    Map<String, List<?>> verTodosMisTrabajos() throws UsuarioNotFoundException, EspecialistaNotFoundException, TrabajoAppException, TrabajoExternoException;

}
