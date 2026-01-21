package com.findfix.find_fix_app.notificacion.service;

import com.findfix.find_fix_app.notificacion.model.Notificacion;
import com.findfix.find_fix_app.notificacion.repository.NotificacionRepository;
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
    public List<Notificacion> obtenerMisNotificaciones(Usuario usuario) {
        return notificacionRepository.findByUsuarioOrderByFechaCreacionDesc(usuario);
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
    public void notificar(Usuario destinatario, String titulo, String mensaje) {
        Notificacion noti = new Notificacion();
        noti.setUsuario(destinatario);
        noti.setTitulo(titulo);
        noti.setMensaje(mensaje);
        noti.setFechaCreacion(LocalDateTime.now());
        noti.setLeida(false);
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
            System.out.println("✅ Email HTML enviado a: " + to);
        } catch (MessagingException e) {
            System.err.println("❌ Error enviando email HTML: " + e.getMessage());
        }
    }

    private String generarHtmlLindo(String titulo, String mensaje) {
        String tituloSeguro = (titulo != null) ? titulo : "Notificación";
        String mensajeSeguro = (mensaje != null) ? mensaje.replace("\n", "<br>") : "";

        String plantilla = """
                <!DOCTYPE html>
                <html lang="es">
                <head>
                    <meta charset="UTF-8">
                    <style>
                        body { font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif; background-color: #f4f4f4; margin: 0; padding: 0; }

                        .container {
                            max-width: 600px;
                            margin: 20px auto;
                            background-color: #ffffff;
                            border-radius: 12px;
                            box-shadow: 0 4px 15px rgba(0,0,0,0.1);
                            overflow: hidden;
                        }

                        .header {
                            background-color: #ff6600; /* Naranja pedido */
                            color: white;
                            padding: 30px;
                            text-align: center;
                        }

                        .logo-wrapper {
                            margin-bottom: 15px;
                        }

                        .logo-img {
                            width: 70px;
                            height: 70px;
                            background-color: white;
                            border-radius: 50%; /* Ahora sí podemos usar % tranquilo */
                            padding: 5px;
                            object-fit: contain;
                        }

                        .header h1 {
                            margin: 0;
                            font-size: 32px;
                            letter-spacing: 2px;
                            text-transform: uppercase;
                            font-weight: 900;
                            /* Simulación de borde negro grueso (Stroke) */
                            text-shadow:
                                -2px -2px 0 #000,
                                 2px -2px 0 #000,
                                -2px  2px 0 #000,
                                 2px  2px 0 #000;
                        }

                        .content {
                            padding: 40px 30px;
                            color: #333333;
                            line-height: 1.6;
                            text-align: center; /* Centrado pedido */
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
                            <div class="logo-wrapper">
                                <img src="findfix-logo.png" alt="Logo FindFix" class="logo-img">
                            </div>
                            <h1>FindFix</h1>
                        </div>

                        <div class="content">
                            <h2>{{TITULO}}</h2>
                            <p>{{MENSAJE}}</p>

                            <div>
                                <a href="https://i.postimg.cc/tgmgJPvC/findfix-logo.png" class="btn">Ir a la App</a>
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
                .replace("{{MENSAJE}}", mensajeSeguro);
    }

    @Override
    public void notificarSolicitudCambioContrasena(Usuario usuario) {
        notificar(usuario, "Solicitud de Cambio de Contraseña", "Recibimos una solicitud para cambiar tu clave.");
    }

    @Override
    public void notificarCambioContrasenaExitoso(Usuario usuario) {
        notificar(usuario, "Contraseña Actualizada", "Tu contraseña ha sido modificada exitosamente.");
    }

    @Override
    public void notificarReporteRecibido(Usuario usuario) {
        notificar(usuario, "Reporte Recibido", "Recibimos tu reporte. Lo revisaremos pronto.");
    }

    @Override
    public void notificarAdminNuevaSolicitudEspecialista(Usuario admin, String nombreSolicitante) {
        notificar(admin, "Nueva Solicitud De Especialista", "El cliente " + nombreSolicitante
                + " a realizado una solicitud para convertirse en especialista. Por favor no olvide ingresar a la app para enviar su respuesta.");
    }

    @Override
    public void notificarAdminNuevoReporte(Usuario admin) {
        notificar(admin, "Nuevo Reporte", "Hay un nuevo reporte pendiente de revisión.");
    }

    @Override
    public void notificarResolucionSolicitudRol(Usuario usuario, boolean aprobado) {
        String msg = aprobado ? "¡Felicidades! Su solicitud para ser especialista fue aceptada. ‼️Atención: para aparecer en las búsquedas de clientes, debe completar su perfil de especialista (ciudad, teléfono y al menos un oficio)." : "Tu solicitud fue rechazada por ahora, verifique la informacion y vuelva a intentarlo mas tarde.";
        notificar(usuario, "Resolución Solicitud", msg);
    }

    @Override
    public void notificarConfirmacionSolicitudEspecialistaEnviada(Usuario usuario) {
        notificar(usuario,
                "Solicitud Recibida",
                "Hemos recibido tu solicitud para ser Especialista correctamente. Nuestro equipo administrativo la revisará y te notificaremos la resolución en breve.");
    }

    @Override
    public void notificarConfirmacionSolicitudEnviada(Usuario cliente, String nombreEspecialista) {
        notificar(cliente, "Solicitud Enviada", "Le enviamos tu pedido a " + nombreEspecialista);
    }

    @Override
    public void notificarRespuestaSolicitudTrabajo(Usuario cliente, String nombreEspecialista, boolean aceptada) {
        String msg = aceptada ? "¡Aceptaron tu trabajo!" : "El especialista rechazó la solicitud.";
        notificar(cliente, "Respuesta Solicitud", msg);
    }

    @Override
    public void notificarCambioEstadoTrabajo(Usuario cliente, String estado, String nombreEspecialista) {
        notificar(cliente, "Trabajo Actualizado", "El estado es ahora: " + estado);
    }

    @Override
    public void notificarConfirmacionResenaRealizada(Usuario cliente) {
        notificar(cliente, "Reseña Publicada", "Gracias por tu opinión.");
    }

    @Override
    public void notificarNuevaSolicitudTrabajoRecibida(Usuario especialista, String nombreCliente, String servicio) {
        notificar(especialista, "¡Nueva Oportunidad!", nombreCliente + " necesita servicio de " + servicio);
    }

    @Override
    public void notificarNuevoTrabajoCreado(Usuario especialista, String nombreCliente) {
        notificar(especialista, "Trabajo Iniciado", "Has comenzado un trabajo con " + nombreCliente);
    }

    @Override
    public void notificarConfirmacionTrabajoFinalizado(Usuario especialista) {
        notificar(especialista, "Trabajo Finalizado", "Marcaste el trabajo como terminado.");
    }

    @Override
    public void notificarNuevaResenaRecibida(Usuario especialista, String nombreCliente) {
        notificar(especialista, "Nueva Reseña", nombreCliente + " te dejó una calificación.");
    }
}