import { CommonModule, DatePipe } from '@angular/common';
import { Component, inject, OnInit, signal } from '@angular/core';
import { SolicitudEspecialistaService } from '../../../services/cliente/solicitud-especialista.service';
import { FichaCompletaSolicitud, MostrarSolicitud } from '../../../models/cliente/solicitud-especialista.model';
import { ModalAlertaComponent } from "../../../components/cliente/modal-alerta.component/modal-alerta.component";
import { ModalDetalleSolicitud } from '../../../components/cliente/modal-detalle-solicitud.component/modal-detalle-solicitud.component';

@Component({
  selector: 'app-historial-solicitudes-especialista.pages',
  standalone: true,
  imports: [CommonModule, DatePipe, ModalAlertaComponent, ModalDetalleSolicitud],
  templateUrl: './historial-solicitudes-especialista.pages.html',
  styleUrl: './historial-solicitudes-especialista.pages.css',
})
export class HistorialSolicitudesEspecialistaPages implements OnInit {

  private solicitudService = inject(SolicitudEspecialistaService);

  misSolicitudes = signal<MostrarSolicitud[]>([]);
  mensajeError = signal<string | null>(null);
  mensajeExito = signal<string | null>(null);
  isLoading = signal(true);

  isAlertaOpen = signal(false);
  solicitudParaEliminar = signal<number | null>(null);

  isDetalleOpen = signal(false);
  solicitudDetalle = signal<FichaCompletaSolicitud | null>(null);

  ngOnInit(): void {
    this.cargarSolicitudes();
  }

  cargarSolicitudes(): void {
    this.isLoading.set(true);
    this.mensajeError.set(null);
    this.solicitudService.obtenerMisSolicitudes().subscribe({
      next: (response) => {
        this.misSolicitudes.set(response.data);
        this.isLoading.set(false);
      },
      error: (err) => {
        if (err.status === 404) {
          this.misSolicitudes.set([]);
        } else {
          this.mensajeError.set(err.error.message || 'Error al cargar solicitudes');
        }
        this.isLoading.set(false);
        console.error(err);
      }
    });
  }

  abrirModalEliminar(id: number): void {
    this.solicitudParaEliminar.set(id);
    this.isAlertaOpen.set(true);
  }

  onModalCancelar(): void {
    this.isAlertaOpen.set(false);
    this.solicitudParaEliminar.set(null);
  }

  onModalConfirmarEliminar(): void {
      const id = this.solicitudParaEliminar();
      if (id === null) return;

      this.mensajeError.set(null);
      this.mensajeExito.set(null);

      this.solicitudService.eliminarSolicitud(id).subscribe({
        next: () => {
          this.mensajeExito.set('Solicitud eliminada con éxito.');
          this.cargarSolicitudes();
          setTimeout(() => this.mensajeExito.set(null), 3000);
        },
        error: (err) => this.mensajeError.set(err.error.message || 'Error al eliminar la solicitud')
      });

      this.onModalCancelar();
    }


    abrirModalDetalle(id: number): void {
    this.solicitudDetalle.set(null);
    this.isDetalleOpen.set(true); 

    this.solicitudService.obtenerDetalleSolicitud(id).subscribe({
      next: (response) => {
        this.solicitudDetalle.set(response.data); // Cargamos los datos en el modal
      },
      error: (err) => {
        this.mensajeError.set(err.error.message || 'Error al cargar el detalle');
        this.isDetalleOpen.set(false); // Cerramos el modal si hay error
      }
    });
  }

  cerrarModalDetalle(): void {
    this.isDetalleOpen.set(false);
    this.solicitudDetalle.set(null);
  }

}
