import { Component, inject, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { ModalConfirmacionComponent } from '../../../components/cliente/modal-confirmacion.component/modal-confirmacion.component';
import { SolicitudTrabajoService } from '../../../services/cliente/solicitud-trabajo.service';

@Component({
  selector: 'app-mis-solicitudes.page',
  standalone: true,
  imports: [CommonModule, FormsModule, ModalConfirmacionComponent],
  templateUrl: './mis-solicitudes.page.html',
  styleUrl: './mis-solicitudes.page.css',
})
export class MisSolicitudesPage implements OnInit {

  private router = inject(Router);
  private solicitudTrabajoService = inject(SolicitudTrabajoService);

  // Señales y Estado
  solicitudesVisibles = signal<any[]>([]);
  todasLasSolicitudes: any[] = [];
  estaCargando = signal(true);

  // Estado del Modal
  alertaVisible = signal(false);
  mensajeAlerta = signal('');
  tipoAlerta = signal<'success' | 'error' | 'pregunta'>('success');

  // Para guardar la solicitud que se quiere eliminar temporalmente
  solicitudAEliminar: any = null;

  // --- Filtros ---
  filtroEstado: 'TODAS' | 'PENDIENTE' | 'FINALIZADA' = 'PENDIENTE';
  filtroTexto = '';

  ngOnInit() {
    this.cargarDatosReales();
  }

  // --- Helpers de Formato ---
  formatearEstado(estado: string): string {
    if (!estado) return '';
    return estado.charAt(0).toUpperCase() + estado.slice(1).toLowerCase().replace(/_/g, ' ');
  }

  obtenerClaseEstado(estado: string): string {
    switch (estado) {
      case 'PENDIENTE': return 'pendiente';
      case 'ACEPTADO': return 'aceptado';
      case 'RECHAZADO': return 'rechazado';
      default: return 'default';
    }
  }

  // --- Manejo del Modal ---
  mostrarAlerta(mensaje: string, tipo: 'success' | 'error' | 'pregunta' = 'success') {
    this.mensajeAlerta.set(mensaje);
    this.tipoAlerta.set(tipo);
    this.alertaVisible.set(true);
  }

  cerrarAlerta() {
    this.alertaVisible.set(false);
    this.solicitudAEliminar = null;
  }

  confirmarAccion() {
    if (this.tipoAlerta() === 'pregunta' && this.solicitudAEliminar) {
      this.procesarEliminacion(this.solicitudAEliminar);
    } else {
      this.cerrarAlerta();
    }
  }

  //  INTEGRACIÓN CON BACKEND
  cargarDatosReales() {
    this.estaCargando.set(true);
    this.solicitudTrabajoService.obtenerMisSolicitudesEnviadas().subscribe({
      next: (response) => {
        this.todasLasSolicitudes = response.data.map(s => ({
          id: s.id,
          especialista: `${s.nombreEspecialista} ${s.apellidoEspecialista}`,
          descripcion: s.descripcion,
          fechaSolicitud: s.fechaCreacion,
          estado: s.estado,
        }));
        this.aplicarFiltros();
        this.estaCargando.set(false);
      },
      error: (err) => {
        console.error(err);
        this.mostrarAlerta('Error al cargar solicitudes', 'error');
        this.estaCargando.set(false);
      }
    });
  }

  // Filtros
  aplicarFiltros() {
    let resultado = this.todasLasSolicitudes;

    //Filtro Estado
    if (this.filtroEstado === 'PENDIENTE') {
      resultado = resultado.filter(s => s.estado === 'PENDIENTE');
    } else if (this.filtroEstado === 'FINALIZADA') {
      resultado = resultado.filter(s => s.estado === 'ACEPTADO' || s.estado === 'RECHAZADO');
    }

    // Filtro Texto
    if (this.filtroTexto) {
      const texto = this.filtroTexto.toLowerCase();
      resultado = resultado.filter(s =>
        (s.especialista && s.especialista.toLowerCase().includes(texto)) ||
        (s.titulo && s.titulo.toLowerCase().includes(texto))
      );
    }

    this.solicitudesVisibles.set(resultado);
  }

  cambiarFiltroEstado(nuevoEstado: 'TODAS' | 'PENDIENTE' | 'FINALIZADA') {
    this.filtroEstado = nuevoEstado;
    this.aplicarFiltros();
  }

  // Acciones del Cliente

  prepararEliminacion(solicitud: any) {
    this.solicitudAEliminar = solicitud;
    this.mostrarAlerta(
      `¿Estás seguro de que deseas cancelar la solicitud enviada a ${solicitud.especialista}? Esta acción no se puede deshacer.`,
      'pregunta'
    );
  }

  procesarEliminacion(solicitud: any) {
    if (!solicitud.id) {
        console.error("Error: Intentando eliminar solicitud sin ID");
        this.cerrarAlerta();
        this.mostrarAlerta("Error interno: No se encontró el ID de la solicitud", "error");
        return;
    }

    this.solicitudTrabajoService.eliminarSolicitud(solicitud.id).subscribe({
      next: () => {
        this.cargarDatosReales();
        this.cerrarAlerta();
        this.mostrarAlerta('Solicitud eliminada', 'success');
      },
      error: (err) => {
        console.error(err);
        this.cerrarAlerta();
        this.mostrarAlerta('No se pudo eliminar la solicitud.', 'error');
      }
    });

  }
}
