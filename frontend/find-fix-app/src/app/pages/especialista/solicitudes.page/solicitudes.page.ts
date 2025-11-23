import { CommonModule } from '@angular/common';
import { Component, inject, signal } from '@angular/core';
import { EspecialistaService } from '../../../services/especialista/especialista.service';
import { SolicitudRecibida } from '../../../models/especialista/especialista.model';
import { ModalConfirmacionComponent } from "../../../components/cliente/modal-confirmacion.component/modal-confirmacion.component";

@Component({
  selector: 'app-solicitudes.page',
  standalone: true,
  imports: [CommonModule, ModalConfirmacionComponent],
  templateUrl: './solicitudes.page.html',
  styleUrl: './solicitudes.page.css',
})
export class SolicitudesPage {
private especialistaService = inject(EspecialistaService);

  solicitudes = signal<SolicitudRecibida[]>([]);

 

  // --- Lógica del Modal ---
  mostrarModal = signal(false);
  datosModal = signal({
    titulo: '',
    mensaje: '',
    tipo: 'pregunta' as 'pregunta' | 'exito',
    textoConfirmar: 'Confirmar'
  });


  accionPendiente: { id: number; estado: 'ACEPTADA' | 'RECHAZADA' } | null = null;

  ngOnInit() {
    this.cargarDatosFalsos();
  }

  cargarDatosFalsos() {

    const dataMock: SolicitudRecibida[] = [
      {
        id: 1,
        descripcion: 'Hola, tengo una filtración debajo del lavabo...',
        fecha: new Date().toISOString(),
        estado: 'PENDIENTE',
        idCliente: 101,
        nombreCliente: 'Mariana López'
      },
      {
        id: 2,
        descripcion: 'Compré un ventilador y necesito que alguien lo instale...',
        fecha: '2023-11-20T10:00:00',
        estado: 'PENDIENTE',
        idCliente: 102,
        nombreCliente: 'Carlos Rodriguez'
      },
      {
        id: 3,
        descripcion: 'Salta la térmica cuando prendo el aire...',
        fecha: '2023-11-18T15:30:00',
        estado: 'PENDIENTE',
        idCliente: 103,
        nombreCliente: 'Estefanía Banini',
      }
    ];
    this.solicitudes.set(dataMock);
  }

  iniciarRespuesta(id: number, estado: 'ACEPTADA' | 'RECHAZADA') {
    this.accionPendiente = { id, estado };

    const verbo = estado === 'ACEPTADA' ? 'aceptar' : 'rechazar';
    this.datosModal.set({
      titulo: `¿${estado === 'ACEPTADA' ? 'Aceptar' : 'Rechazar'} solicitud?`,
      mensaje: `Estás a punto de ${verbo} este trabajo. ¿Deseas continuar?`,
      tipo: 'pregunta',
      textoConfirmar: `Sí, ${verbo}`
    });

    this.mostrarModal.set(true);
  }

  confirmarAccion() {
    if (this.datosModal().tipo === 'exito') {
      this.cerrarModal();
      return;
    }

    if (this.accionPendiente) {
      const { id, estado } = this.accionPendiente;

      // Simulación de llamada al servicio
      // this.especialistaService.responderSolicitud(id, estado).subscribe(...)

      this.solicitudes.update(prev => prev.filter(s => s.id !== id));

      this.datosModal.set({
        titulo: '¡Listo!',
        mensaje: `La solicitud fue ${estado === 'ACEPTADA' ? 'aceptada' : 'rechazada'} correctamente.`,
        tipo: 'exito',
        textoConfirmar: 'Entendido'
      });

      this.accionPendiente = null;
    }
  }

  cerrarModal() {
    this.mostrarModal.set(false);
    this.accionPendiente = null;
  }
}
