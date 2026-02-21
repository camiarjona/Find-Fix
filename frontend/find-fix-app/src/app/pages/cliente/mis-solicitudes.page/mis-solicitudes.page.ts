import { Component, inject, OnInit, signal, computed } from '@angular/core';
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

  // --- Datos ---
  todasLasSolicitudes: any[] = []; // Los datos crudos del back
  solicitudesFiltradas: any[] = []; // Datos después de pasar por el filtro de estado/texto
  solicitudesVisibles = signal<any[]>([]); // Lo que se ve en la página actual
  estaCargando = signal(true);

  // --- Paginación ---
  currentPage = signal(0);
  pageSize = 6;
  totalPages = signal(0);

  // --- Estado del Modal ---
  alertaVisible = signal(false);
  mensajeAlerta = signal('');
  tipoAlerta = signal<'success' | 'error' | 'pregunta'>('success');
  solicitudAEliminar: any = null;

  // --- Filtros ---
  filtroEstado: 'TODAS' | 'PENDIENTE' | 'FINALIZADA' = 'PENDIENTE';
  filtroTexto = '';

  ngOnInit() {
    this.cargarDatosReales();
  }

  cargarDatosReales() {
    this.estaCargando.set(true);
    this.solicitudTrabajoService.obtenerMisSolicitudesEnviadas().subscribe({
      next: (response) => {
        this.todasLasSolicitudes = response.data.map(s => ({
          id: s.id,
          especialista: `${s.nombreEspecialista} ${s.apellidoEspecialista}`,
          fotoUrl: s.fotoUrlEspecialista,
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

  aplicarFiltros() {
    // 1. Filtrar la lista completa por Estado y Texto
    let resultado = this.todasLasSolicitudes;

    if (this.filtroEstado === 'PENDIENTE') {
      resultado = resultado.filter(s => s.estado === 'PENDIENTE');
    } else if (this.filtroEstado === 'FINALIZADA') {
      resultado = resultado.filter(s => s.estado === 'ACEPTADO' || s.estado === 'RECHAZADO');
    }

    if (this.filtroTexto) {
      const texto = this.filtroTexto.toLowerCase();
      resultado = resultado.filter(s =>
        (s.especialista && s.especialista.toLowerCase().includes(texto)) ||
        (s.descripcion && s.descripcion.toLowerCase().includes(texto))
      );
    }

    // 2. Guardar la lista filtrada y calcular páginas
    this.solicitudesFiltradas = resultado;
    this.totalPages.set(Math.ceil(this.solicitudesFiltradas.length / this.pageSize));

    // 3. Cortar la lista para la página actual
    this.actualizarVistaPaginada();
  }

  actualizarVistaPaginada() {
    const inicio = this.currentPage() * this.pageSize;
    const fin = inicio + this.pageSize;
    this.solicitudesVisibles.set(this.solicitudesFiltradas.slice(inicio, fin));
  }

  cambiarFiltroEstado(nuevoEstado: 'TODAS' | 'PENDIENTE' | 'FINALIZADA') {
    this.filtroEstado = nuevoEstado;
    this.currentPage.set(0); // Reset a primera página
    this.aplicarFiltros();
  }

  cambiarPagina(nuevaPagina: number) {
    this.currentPage.set(nuevaPagina);
    this.actualizarVistaPaginada();
    window.scrollTo({ top: 0, behavior: 'smooth' }); // Feedback visual de cambio
  }

  // --- Helpers y Acciones ---
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

  prepararEliminacion(solicitud: any) {
    this.solicitudAEliminar = solicitud;
    this.mostrarAlerta(
      `¿Estás seguro de que deseas cancelar la solicitud enviada a ${solicitud.especialista}?`,
      'pregunta'
    );
  }

  procesarEliminacion(solicitud: any) {
    this.solicitudTrabajoService.eliminarSolicitud(solicitud.id).subscribe({
      next: () => {
        this.cargarDatosReales();
        this.cerrarAlerta();
        this.mostrarAlerta('Solicitud eliminada', 'success');
      },
      error: () => {
        this.cerrarAlerta();
        this.mostrarAlerta('No se pudo eliminar la solicitud.', 'error');
      }
    });
  }
}
