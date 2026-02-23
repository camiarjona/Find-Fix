package com.findfix.find_fix_app.notificacion.service;

import com.findfix.find_fix_app.especialista.model.Especialista;
import com.findfix.find_fix_app.notificacion.dto.NotificacionDTO;
import com.findfix.find_fix_app.notificacion.model.Notificacion;
import com.findfix.find_fix_app.notificacion.repository.NotificacionRepository;
import com.findfix.find_fix_app.trabajo.trabajoApp.model.TrabajoApp;
import com.findfix.find_fix_app.usuario.model.Usuario;
import com.findfix.find_fix_app.utils.exception.exceptions.NotificacionException;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.mail.javamail.MimeMessageHelper;

@Service
@RequiredArgsConstructor
public class NotificacionServiceImpl implements NotificacionService {

    private final JavaMailSender mailSender;
    private final NotificacionRepository notificacionRepository;

    @Value("${spring.mail.username}")
    private String remitente;

    @Override
    public List<NotificacionDTO> obtenerMisNotificaciones(Usuario usuario, String rolVista) {
        List<Notificacion> notificaciones = notificacionRepository
                .findByUsuario_UsuarioIdAndRolDestinatarioOrderByFechaCreacionDesc(usuario.getUsuarioId(), rolVista);
        return notificaciones.stream()
                .map(n -> new NotificacionDTO(
                        n.getId(),
                        n.getTitulo(),
                        n.getMensaje(),
                        n.isLeida(),
                        n.getFechaCreacion(),
                        n.getRolDestinatario()))
                .toList();
    }

    @Override
    public void marcarComoLeida(Long idNotificacion) {
        Notificacion noti = notificacionRepository.findById(idNotificacion)
                .orElseThrow(
                        () -> new NotificacionException("La notificación con id " + idNotificacion + " no existe."));

        noti.setLeida(true);
        notificacionRepository.save(noti);
    }

    @Override
    public void notificar(Usuario destinatario, String titulo, String mensaje, String rolDestinatario) {
        Notificacion noti = new Notificacion();
        noti.setUsuario(destinatario);
        noti.setTitulo(titulo);
        noti.setMensaje(mensaje);
        noti.setFechaCreacion(LocalDateTime.now());
        noti.setLeida(false);
        noti.setRolDestinatario(rolDestinatario);
        notificacionRepository.save(noti);
        try {
            enviarEmail(destinatario.getEmail(), titulo, mensaje);
        } catch (Exception e) {
            System.err.println("⚠ Alerta: Email falló para " + destinatario.getEmail() + ": " + e.getMessage());
        }
    }

    @Async
    public void enviarEmail(String to, String subject, String body) {
        try {

            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setTo(to);
            helper.setSubject("FindFix: " + subject);
            helper.setFrom(remitente);
            String htmlContent = generarHtmlLindo(subject, body);
            helper.setText(htmlContent, true);
            mailSender.send(message);
            System.out.println("Email HTML enviado a: " + to);
        } catch (MessagingException e) {
            System.err.println("Error enviando email HTML: " + e.getMessage());
        }
    }

    private String generarHtmlLindo(String titulo, String mensaje) {
        String tituloSeguro = (titulo != null) ? titulo : "Notificación";
        String mensajeSeguro = (mensaje != null) ? mensaje.replace("\n", "<br>") : "";

        // URL de tu App (cambiala por la real si tenés, o dejala así)
        String urlApp = "http://localhost:4200";

        String plantilla = """
                <!DOCTYPE html>
                <html lang="es">
                <head>
                    <meta charset="UTF-8">
                    <meta http-equiv="X-UA-Compatible" content="IE=edge">
                    <meta name="viewport" content="width=device-width, initial-scale=1.0">
                    <style>
                        /* Reset básico */
                        body { font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif; background-color: #f4f4f4; margin: 0; padding: 0; -webkit-text-size-adjust: 100%; -ms-text-size-adjust: 100%; }
                        table, td { border-collapse: collapse; mso-table-lspace: 0pt; mso-table-rspace: 0pt; }
                        img { border: 0; height: auto; line-height: 100%; outline: none; text-decoration: none; -ms-interpolation-mode: bicubic; }

                        .container {
                            max-width: 600px;
                            margin: 20px auto;
                            background-color: #ffffff;
                            border-radius: 12px;
                            box-shadow: 0 4px 15px rgba(0,0,0,0.1);
                            overflow: hidden;
                        }

                        .header {
                            background-color: #ff6600;
                            padding: 30px 20px;
                            text-align: center;
                        }

                        .header h1 {
                            margin: 15px 0 0 0;
                            color: white;
                            font-size: 32px;
                            letter-spacing: 2px;
                            text-transform: uppercase;
                            font-weight: 900;
                            text-shadow: -2px -2px 0 #000, 2px -2px 0 #000, -2px 2px 0 #000, 2px 2px 0 #000;
                        }

                        .content {
                            padding: 40px 30px;
                            color: #333333;
                            line-height: 1.6;
                            text-align: center;
                        }

                        .content h2 {
                            color: #ff6600;
                            margin-top: 0;
                            font-size: 24px;
                        }

                        .btn {
                            display: inline-block;
                            padding: 14px 30px;
                            background-color: #ff6600;
                            color: white !important;
                            text-decoration: none;
                            border-radius: 30px;
                            margin-top: 25px;
                            font-weight: bold;
                            font-size: 16px;
                        }

                        .footer {
                            background-color: #f9f9f9;
                            padding: 20px;
                            text-align: center;
                            font-size: 12px;
                            color: #888888;
                            border-top: 1px solid #eeeeee;
                        }
                    </style>
                </head>
                <body>
                    <div class="container">

                        <div class="header">
                            <table border="0" cellpadding="0" cellspacing="0" width="100%">
                                <tr>
                                    <td align="center">
                                        <img src="https://i.postimg.cc/tgmgJPvC/findfix-logo.png"
                                             alt="Logo FindFix"
                                             width="70"
                                             height="70"
                                             style="display: block; width: 70px; height: 70px; background-color: white; border-radius: 50%; padding: 5px; border: 0;">
                                    </td>
                                </tr>
                            </table>

                            <h1>FindFix</h1>
                        </div>

                        <div class="content">
                            <h2>{{TITULO}}</h2>
                            <p>{{MENSAJE}}</p>

                            <div>
                                <a href="{{URL_APP}}" class="btn">Ir a la App</a>
                            </div>
                        </div>

                        <div class="footer">
                            <p>Estás recibiendo este correo porque usas <strong>FindFix</strong>.</p>
                            <p>&copy; 2026 FindFix Team</p>
                        </div>
                    </div>
                </body>
                </html>
                """;

        return plantilla
                .replace("{{TITULO}}", tituloSeguro)
                .replace("{{MENSAJE}}", mensajeSeguro)
                .replace("{{URL_APP}}", urlApp);
    }

    @Override
    public void notificarBienvenida(Usuario usuario, String rolDestinatario) {
        String titulo = "¡Bienvenido a FindFix! 🚀";
        String mensaje = "Hola " + usuario.getNombre() + ", gracias por unirte. " +
                "Ya podés buscar especialistas o postularte para ofrecer servicios.";

        notificar(usuario, titulo, mensaje, rolDestinatario);
    }

    @Override
    public void notificarSolicitudCambioContrasena(Usuario usuario, String rolDestinatario) {
        notificar(usuario, "Solicitud de Cambio de Contraseña", "Recibimos una solicitud para cambiar tu clave.",
                rolDestinatario);
    }

    @Override
    public void notificarCambioContrasenaExitoso(Usuario usuario, String rolDestinatario) {
        notificar(usuario, "Contraseña Actualizada",
                "Tu contraseña ha sido modificada exitosamente, si usted no fue el responsable de esta accion proceda a mandar un reporte a nuestro soporte.",
                rolDestinatario);
    }

    @Override
    public void notificarReporteRecibido(Usuario usuario, String rolDestinatario) {
        notificar(usuario, "Reporte Recibido", "Recibimos tu reporte. Lo revisaremos pronto.", rolDestinatario);
    }

    @Override
    public void notificarAdminNuevaSolicitudEspecialista(Usuario admin, String nombreSolicitante,
            String rolDestinatario) {
        notificar(admin, "Nueva Solicitud De Especialista", "El cliente " + nombreSolicitante
                + " a realizado una solicitud para convertirse en especialista. Por favor no olvide ingresar a la app para enviar su respuesta.",
                rolDestinatario);
    }

    @Override
    public void notificarAdminNuevoReporte(Usuario admin, String rolDestinatario) {
        notificar(admin, "Nuevo Reporte", "Hay un nuevo reporte pendiente de revisión.", rolDestinatario);
    }

    @Override
    public void notificarResolucionSolicitudRol(Usuario usuario, boolean aprobado, String rolDestinatario) {
        String msg = aprobado
                ? "¡Felicidades! Su solicitud para ser especialista fue aceptada. ‼️Atención: para aparecer en las búsquedas de clientes, debe completar su perfil de especialista (ciudad, teléfono y al menos un oficio)."
                : "Tu solicitud fue rechazada por ahora, verifique la informacion y vuelva a intentarlo mas tarde.";
        notificar(usuario, "Resolución Solicitud", msg, rolDestinatario);
    }

    @Override
    public void notificarConfirmacionSolicitudEspecialistaEnviada(Usuario usuario, String rolDestinatario) {
        notificar(usuario,
                "Solicitud Recibida",
                "Hemos recibido tu solicitud para ser Especialista correctamente. Nuestro equipo administrativo la revisará y te notificaremos la resolución en breve.",
                rolDestinatario);
    }

    @Override
    public void notificarConfirmacionSolicitudEnviada(Usuario cliente, String nombreEspecialista,
            String rolDestinatario) {
        notificar(
                cliente, "Solicitud Enviada", "Hola " + cliente.getNombre() + "," + " le enviamos tu pedido a "
                        + nombreEspecialista + " se te notificara la resolucion de la solicitud por esta misma via.",
                rolDestinatario);
    }

    @Override
    public void notificarRespuestaSolicitudTrabajo(Usuario cliente, String nombreEspecialista, boolean aceptada,
            String rolDestinatario) {
        String msg = aceptada
                ? "¡Aceptaron tu solicitud!, ya puedes visualizar el trabajo creado en tu seccion `Mis trabajos`, no  olvides que tienes la informacion necesaria para contactar a tu profesional a cargo."
                : "Lamentablemente el especialista" + nombreEspecialista
                        + "rechazó tu solicitud enviada, vuelve a intentarlo con otro de nuestros especialistas registrados.";
        notificar(cliente, "Respuesta Solicitud", msg, rolDestinatario);
    }

    @Override
    public void notificarCambioEstadoTrabajo(Usuario cliente, String estado, String nombreEspecialista,
            String rolDestinatario) {
        notificar(cliente, "Trabajo Actualizado",
                "El especialista " + nombreEspecialista + " a cargo de su trabajo, a realizado un cambio en su estado. "
                        + "ESTADO: |" + estado + "| Ingrese a la app y su seccion Mis Trabajos para mas detalles.",
                rolDestinatario);
    }

    @Override
    public void notificarConfirmacionResenaRealizada(Usuario cliente, String nombreEspecialista,
            String rolDestinatario) {
        notificar(cliente, "Reseña Publicada", "La reseña realizada al especialista " + nombreEspecialista
                + " fue publicada. Gracias por tu opinión!.", rolDestinatario);
    }

    @Override
    public void notificarNuevaSolicitudTrabajoRecibida(Usuario especialista, String nombreCliente, String servicio,
            String rolDestinatario) {
        notificar(especialista, "¡Nueva Oportunidad!", nombreCliente
                + " te envio una solicitud de trabajo. Ingresa a la app para visualizar los detalles y enviarle una respuesta. ",
                rolDestinatario);
    }

    @Override
    public void notificarNuevoTrabajoCreado(Usuario especialista, String nombreCliente, String rolDestinatario) {
        notificar(especialista, "Trabajo Creado", "Se creo un trabajo con el cliente " + nombreCliente
                + " recuerda ingresar a la app para ver los detalles en tu seccion de trabajos y controlar los estados del trabajo.",
                rolDestinatario);
    }

    @Override
    public void notificarConfirmacionTrabajoFinalizado(Usuario especialista, TrabajoApp trabajoApp,
            String rolDestinatario) {
        notificar(especialista, "Trabajo Finalizado",
                "Marcaste el trabajo: " + trabajoApp.getTitulo()
                        + " como finalizado, para mas detalles ingrese a su seccion de trabajos desde la app.",
                rolDestinatario);
    }

    @Override
    public void notificarConfirmacionTrabajoIniciado(Usuario especialista, TrabajoApp trabajoApp,
            String rolDestinatario) {
        notificar(especialista, "Trabajo iniciado", "Marcaste el trabajo con nombre: " + trabajoApp.getTitulo()
                + " |EN PROCESO|, por lo tanto se ha dado inicio al mismo, para mas detalles ingrese a su seccion de trabajos desde la app.",
                rolDestinatario);
    }

    @Override
    public void notificarNuevaResenaRecibida(Usuario especialista, String nombreCliente, String rolDestinatario) {
        notificar(especialista, "Nueva Reseña", nombreCliente
                + " te dejó una calificación en tu trabajo! Ingresa a la app y dirigite a la seccion de tus reseñas para mas detalles.",
                rolDestinatario);
    }

    @Override
    public void notificarTokenRegistro(Usuario usuario, String token) {
        String urlActivacion = "http://localhost:4200/confirmar-cuenta?token=" + token;

        String titulo = "Activa tu cuenta de FindFix";

        String mensaje = "Hola " + usuario.getNombre() + ", estás a un solo paso de unirte a FindFix. <br><br>" +
                "Por favor, copia y pega el siguiente enlace en tu navegador para activar tu cuenta: <br><br>" +
                "<a href='" + urlActivacion + "'>" + urlActivacion + "</a>";

        enviarEmail(usuario.getEmail(), titulo, mensaje);
    }
}