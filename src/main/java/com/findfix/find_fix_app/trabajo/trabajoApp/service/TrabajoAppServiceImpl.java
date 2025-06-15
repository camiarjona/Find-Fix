package com.findfix.find_fix_app.trabajo.trabajoApp.service;

import com.findfix.find_fix_app.auth.service.AuthService;
import com.findfix.find_fix_app.enums.EstadosTrabajos;
import com.findfix.find_fix_app.especialista.model.Especialista;
import com.findfix.find_fix_app.especialista.service.EspecialistaService;
import com.findfix.find_fix_app.exception.exceptions.*;
import com.findfix.find_fix_app.trabajo.trabajoApp.dto.ActualizarTrabajoAppDTO;
import com.findfix.find_fix_app.trabajo.trabajoApp.model.TrabajoApp;
import com.findfix.find_fix_app.trabajo.trabajoApp.repository.TrabajoAppRepository;
import com.findfix.find_fix_app.usuario.model.Usuario;
import com.findfix.find_fix_app.usuario.service.UsuarioService;
import lombok.RequiredArgsConstructor;
import org.aspectj.weaver.patterns.IVerificationRequired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TrabajoAppServiceImpl implements TrabajoAppService{

    private final TrabajoAppRepository trabajoAppRepository;
    private final AuthService authService;
    private final UsuarioService usuarioService;
    private final EspecialistaService especialistaService;
    ///  METODO APRA GUARDAR EL TRABAJO QUE AUTOMATICAMENTE LLEGA UNA VEZ QUE UNA SOLICITUD ES ACEPTADA
    @Override
    public void guardarTrabajo(TrabajoApp trabajoApp) {
          trabajoAppRepository.save(trabajoApp);
    }
   ///  METODO PARA OBTENER LOS TRABAJOS DESDE LA PERSPECTIVA DE LOS CLIENTES
    @Override
    public List<TrabajoApp> obtenerTrabajosClientes() throws UserNotFoundException,TrabajoAppException {

        String emailUsuario = authService.obtenerEmailUsuarioAutenticado();
        List<TrabajoApp> trabajoApps = trabajoAppRepository.findAll().stream().filter(trabajoApp -> trabajoApp.getUsuario().getEmail().equals(emailUsuario)).collect(Collectors.toList());
        if(trabajoApps.isEmpty())
        {
            throw new TrabajoAppException("Usted no tiene ningun registro de trabajos.");
        }
        return trabajoApps;
    }
    /// METODO PARA OBTENER LOS TRABAJOS DESDE LA PERSPECTIVA DE LOS ESPECIALISTAS
    @Override
    public List<TrabajoApp> obtenerTrabajosEspecialista() throws UserNotFoundException, TrabajoAppException, SpecialistRequestNotFoundException {
        Especialista especialista = especialistaService.obtenerEspecialistaAutenticado();
        List<TrabajoApp> trabajoApps = trabajoAppRepository.findAll().stream().filter(trabajoApp -> trabajoApp.getEspecialista().getDni().equals(especialista.getDni())).collect(Collectors.toList());
        if(trabajoApps.isEmpty())
        {
            throw new TrabajoAppException("Usted no tiene ningun registro de trabajos.");
        }
        return trabajoApps;
    }

  ///  METODO PARA OBTENER LOS TRABAJOS DESDE LA PERSPECTIVA DE LOS ESPECIALISTA Y APLICANDO FILTRO DE UN ESTADO ELEGIDO
    @Override
    public List<TrabajoApp> obtenerTrabajosEspecialistaEstado(String nombreEstado) throws UserNotFoundException, SpecialistRequestNotFoundException, TrabajoAppException {
        if(!validezIngresoEstado(nombreEstado))
        {
            throw new TrabajoAppException("El estado ingresado no es valido.");
        }
        Especialista especialista = especialistaService.obtenerEspecialistaAutenticado();
        List<TrabajoApp> trabajoApps = trabajoAppRepository.findAll().stream().filter(trabajoApp -> trabajoApp.getEspecialista().getDni().equals(especialista.getDni())&&trabajoApp.getEstado().name().equalsIgnoreCase(nombreEstado)).collect(Collectors.toList());
        if(trabajoApps.isEmpty())
        {
            throw new TrabajoAppException("Usted no tiene ningun trabajo en ese estado.");
        }
        return trabajoApps;
    }

    /*@Override
    public Optional<TrabajoApp> buscarPorEstado(String nombreEstado) {
           ///  en realidad no serviria ???

    }*/
  ///  METODO PARA VALIDAR QUE EL ESTADO INGRESADO EXISTA EN EL ENUM ESTADOS
    private Boolean validezIngresoEstado(String nombreEstado)
    {
        for(EstadosTrabajos e : EstadosTrabajos.values())
        {
            if(e.name().equalsIgnoreCase(nombreEstado))
            {
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
    ///  METODO PARA MODIFICAR UN TRABAJO (NO SOLO EL ESTADO)
    @Override
    public TrabajoApp actualizarTrabajo(String titulo, ActualizarTrabajoAppDTO dto) throws TrabajoAppNotFoundException, TrabajoAppException, UserNotFoundException, SpecialistRequestNotFoundException {
        List<TrabajoApp> trabajosAppDelEspecialista = obtenerTrabajosEspecialista();
        Optional<TrabajoApp> trabajoAppBuscado = trabajosAppDelEspecialista.stream().filter(trabajoApp -> trabajoApp.getTitulo().equalsIgnoreCase(titulo)).findFirst();
       if(trabajoAppBuscado.isPresent())
       {
           throw new TrabajoAppNotFoundException("El trabajo que desea modificar no esta registrado en el sistema.");
       }else
       {

           if(verificacionEstadoFinalizado(trabajoAppBuscado.get()))
           {
               throw new TrabajoAppException("El trabajo se encuentra finalizado por lo tanto ya no se pueden realizar cambios en sus datos");
           }

           Optional<TrabajoApp> verificacion = buscarPorTitulo(dto.titulo());
           if(verificacion.isPresent())
           {
               throw new TrabajoAppException("El titulo que ingreso ya pertenece a un trabajo del sistema.");
           }else{
               trabajoAppBuscado.get().setTitulo(dto.titulo());
           }


           if(datoStringValido(dto.descripcion()))
           {
               trabajoAppBuscado.get().setDescripcion(dto.descripcion());
           }
           String estadoNormalizado = dto.estado().toUpperCase().replace("","_");
           if(datoStringValido(estadoNormalizado)&&validezIngresoEstado(estadoNormalizado)&&!trabajoAppBuscado.get().getEstado().name().equalsIgnoreCase(estadoNormalizado)&&!trabajoAppBuscado.get().getEstado().equals(EstadosTrabajos.EN_PROCESO)&&estadoNormalizado.equalsIgnoreCase(EstadosTrabajos.CREADO.name())&&!trabajoAppBuscado.get().getEstado().equals(EstadosTrabajos.EN_REVISION)&&estadoNormalizado.equalsIgnoreCase(EstadosTrabajos.CREADO.name()))
           {
               if(EstadosTrabajos.EN_PROCESO.equals(EstadosTrabajos.valueOf(estadoNormalizado))&&trabajoAppBuscado.get().getEstado().equals(EstadosTrabajos.CREADO))
               {
                   trabajoAppBuscado.get().setFechaInicio(LocalDate.now());
               }
               if(EstadosTrabajos.FINALIZADO.equals(EstadosTrabajos.valueOf(estadoNormalizado)))
               {
                   trabajoAppBuscado.get().setFechaFin(LocalDate.now());
               }
               trabajoAppBuscado.get().setEstado(EstadosTrabajos.valueOf(estadoNormalizado));
           }
           if(dto.presupuesto()!=null)
           {
               trabajoAppBuscado.get().setPresupuesto(dto.presupuesto());
           }
       }

       trabajoAppRepository.save(trabajoAppBuscado.get());
       return trabajoAppBuscado.get();
    }
  ///  METODO AUXILIAR APRA VERIFICAR
    public boolean datoStringValido(String datoString)
    {
        if(datoString.isEmpty()||datoString==null)
        {
            return false;
        }else
        {
            return true;
        }
    }

    ///  Este metodo solo modifica el estado de un trabajo
    @Override
    public void modificarEstadoTrabajo(String titulo, String nombreEstado) throws TrabajoAppNotFoundException, TrabajoAppException, UserNotFoundException, SpecialistRequestNotFoundException {
        ///  filtramos los trabajos del espewcialista que esta queriendo modificar el trabajo
        List<TrabajoApp> trabajosAppDelEspecialista = obtenerTrabajosEspecialista();
        Optional<TrabajoApp> trabajoBuscado = trabajosAppDelEspecialista.stream().filter(trabajoApp -> trabajoApp.getTitulo().equalsIgnoreCase(titulo)).findFirst();
        ///  buscamos y verificamos que exista el trabajo
          if(!trabajoBuscado.isPresent())
          {
              new TrabajoAppNotFoundException("El trabajo al que le quiere cambiar el estado no está registrado");
          }

        ///  Verificamos que el trabajo no se encuentra ya en finalizado porque si es asi no puede cambiarlo de ninguna manera. (usamos metodo auxiliar)
        if(verificacionEstadoFinalizado(trabajoBuscado.get()))
        {
         throw new TrabajoAppException("El trabajo ya se encuentra finalizado, por lo tanto ya no se puede modificar su estado.");
        }
        ///  verificamos que el estado ingresado exista en nuestro estadosTrabajos
            if(validezIngresoEstado(nombreEstado))
            {
                verificacionEstadoAdicionales(trabajoBuscado.get(),nombreEstado);
                if(EstadosTrabajos.EN_PROCESO.equals(EstadosTrabajos.valueOf(nombreEstado))&&trabajoBuscado.get().getEstado().equals(EstadosTrabajos.CREADO))
                {
                    trabajoBuscado.get().setFechaInicio(LocalDate.now());
                }
                if(EstadosTrabajos.FINALIZADO.equals(EstadosTrabajos.valueOf(nombreEstado)))
                {
                    trabajoBuscado.get().setFechaFin(LocalDate.now());
                }
                trabajoBuscado.get().setEstado(EstadosTrabajos.valueOf(nombreEstado));

            }else ///  sino exception
            {
                throw new TrabajoAppException("El estado ingresado no es valido");
            }
        trabajoAppRepository.save(trabajoBuscado.get());
    }

   ///  METODO AUXILIAR PARA VERIFICAR
    public void verificacionEstadoAdicionales(TrabajoApp trabajoApp,String nombreEstado) throws TrabajoAppException
    {

        if(trabajoApp.getEstado().name().equalsIgnoreCase(nombreEstado))
        {
            throw new TrabajoAppException("El trabajo ya se encuentra en ese estado");
        }

        if(trabajoApp.getEstado().equals(EstadosTrabajos.EN_PROCESO)&&nombreEstado.equalsIgnoreCase(EstadosTrabajos.CREADO.name()))
        {
            throw new TrabajoAppException("El trabajo ya esta en proceso, no puede volver atras");
        }

        if(trabajoApp.getEstado().equals(EstadosTrabajos.EN_REVISION)&&nombreEstado.equalsIgnoreCase(EstadosTrabajos.CREADO.name()))
        {
            throw new TrabajoAppException("El trabajo no puede volver a 'CREADO'. Como mucho puede revertir De EN REVISION A PROCESO ");
        }



    }
  /// METODO AUXILIAR PARA VERIFICAR
    public Boolean verificacionEstadoFinalizado(TrabajoApp trabajoApp)
    {
        if(trabajoApp.getEstado().equals(EstadosTrabajos.FINALIZADO))
        {
            return true;
        }else
        {
            return false;
        }
    }

    ///  METODO PARA QUE UN CLIENTE OBTENGA UNA FICHA DE UN TRABAJO FILTRANDO POR EL TITULO DEL MISMO
    @Override
    public TrabajoApp obtenerFichaDeTrabajoParaEspecialista(String tituloBuscado) throws TrabajoAppNotFoundException, UserNotFoundException, TrabajoAppException, SpecialistRequestNotFoundException {
       List<TrabajoApp> trabajoAppDelEspecialista =  obtenerTrabajosEspecialista();
        Optional<TrabajoApp> trabajoBuscado = trabajoAppDelEspecialista.stream().filter(trabajoApp -> trabajoApp.getTitulo().equalsIgnoreCase(tituloBuscado)).findFirst();
        if(!trabajoBuscado.isPresent())
        {
          throw new TrabajoAppNotFoundException("No se pudo otorgar la ficha trabajo debido a que el titulo ingresado no pertenece a ningun registro. ");
        }else {
            return trabajoBuscado.get();
        }
    }


}
