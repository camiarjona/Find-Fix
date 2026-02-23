import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { ModalConfirmacionComponent } from '../../../components/cliente/modal-confirmacion.component/modal-confirmacion.component';
import { SolicitudTrabajoService } from '../../../services/cliente/solicitud-trabajo.service';
import { ordenarDinamicamente } from '../../../utils/sort-utils';
import { Component, HostListener, inject, OnInit, signal } from '@angular/core';

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

  // Datos Originales y Filtrados
  todasLasSolicitudes: any[] = [];
  solicitudesFiltradas: any[] = [];

  // Señales de Estado y UI
  solicitudesVisibles = signal<any[]>([]);
  estaCargando = signal(true);
  dropdownOpen: string | null = null;
  solicitudAEliminar: any = null;

  // Paginación
  currentPage = signal(0);
  pageSize = 6;
  totalPages = signal(0);

  // Filtros y Ordenamiento
  filtroEstado: 'TODAS' | 'PENDIENTE' | 'FINALIZADA' = 'PENDIENTE';
  filtroTexto = '';
  criterioOrden = 'fecha';

  // Modales
  alertaVisible = signal(false);
  mensajeAlerta = signal('');
  tipoAlerta = signal<'success' | 'error' | 'pregunta'>('success');

  ngOnInit() {
    this.cargarDatosReales();
  }

  cargarDatosReales() {
    this.estaCargando.set(true);
    this.solicitudTrabajoService.obtenerMisSolicitudesEnviadas().subscribe({
      next: (response) => {
        this.todasLasSolicitudes = response.data.map((s: any) => ({
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
    let resultado = [...this.todasLasSolicitudes];

    // Filtro Estado
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
        (s.descripcion && s.descripcion.toLowerCase().includes(texto))
      );
    }

    // Ordenamiento
    if (this.criterioOrden === 'especialista') {
      resultado = ordenarDinamicamente(resultado, 'especialista', 'asc');
    } else {
      resultado = ordenarDinamicamente(resultado, 'fechaSolicitud', 'desc');
    }

    this.solicitudesFiltradas = resultado;
    this.totalPages.set(Math.ceil(this.solicitudesFiltradas.length / this.pageSize));

    if (this.currentPage() >= this.totalPages() && this.totalPages() > 0) {
      this.currentPage.set(0);
    }

    this.actualizarVistaPaginada();
  }

  actualizarVistaPaginada() {
    const inicio = this.currentPage() * this.pageSize;
    const fin = inicio + this.pageSize;
    this.solicitudesVisibles.set(this.solicitudesFiltradas.slice(inicio, fin));
  }

  // --- Acciones de UI ---

  cambiarFiltroEstado(nuevoEstado: 'TODAS' | 'PENDIENTE' | 'FINALIZADA') {
    this.filtroEstado = nuevoEstado;
    this.currentPage.set(0);
    this.aplicarFiltros();
  }

  cambiarPagina(nuevaPagina: number) {
    this.currentPage.set(nuevaPagina);
    this.actualizarVistaPaginada();
    window.scrollTo({ top: 0, behavior: 'smooth' });
  }

  seleccionarOrden(valor: string) {
    this.criterioOrden = valor;
    this.dropdownOpen = null;
    this.aplicarFiltros();
  }

  limpiarFiltros() {
    this.filtroTexto = '';
    this.filtroEstado = 'PENDIENTE';
    this.criterioOrden = 'fecha';
    this.currentPage.set(0);
    this.dropdownOpen = null;
    this.aplicarFiltros();
  }

  toggleDropdown(menu: string, event: Event) {
    event.stopPropagation();
    this.dropdownOpen = this.dropdownOpen === menu ? null : menu;
  }

  @HostListener('document:click', ['$event'])
  onDocumentClick(event: MouseEvent) {
    const target = event.target as HTMLElement;
    if (!target.closest('.custom-select-wrapper')) {
      this.dropdownOpen = null;
    }
  }

  // --- Helpers de Formato y Modales ---

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

  // --- Acciones del Cliente (Eliminación) ---

  prepararEliminacion(solicitud: any) {
    this.solicitudAEliminar = solicitud;
    this.mostrarAlerta(
      `¿Estás seguro de que deseas cancelar la solicitud enviada a ${solicitud.especialista}?`,
      'pregunta'
    );
  }

  procesarEliminacion(solicitud: any) {
    if (!solicitud.id) {
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
      error: () => {
        this.cerrarAlerta();
        this.mostrarAlerta('No se pudo eliminar la solicitud.', 'error');
      }
    });
  }
}
