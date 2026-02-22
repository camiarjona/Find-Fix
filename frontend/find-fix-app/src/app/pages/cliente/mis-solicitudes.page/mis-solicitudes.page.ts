import { Component, HostListener, inject, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { ModalConfirmacionComponent } from '../../../components/cliente/modal-confirmacion.component/modal-confirmacion.component';
import { SolicitudTrabajoService } from '../../../services/cliente/solicitud-trabajo.service';
import { ordenarDinamicamente } from '../../../utils/sort-utils'; // Importamos tu helper

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
  dropdownOpen: string | null = null;

  // Estado del Modal
  alertaVisible = signal(false);
  mensajeAlerta = signal('');
  tipoAlerta = signal<'success' | 'error' | 'pregunta'>('success');

  solicitudAEliminar: any = null;

  // --- Filtros y Ordenamiento ---
  filtroEstado: 'TODAS' | 'PENDIENTE' | 'FINALIZADA' = 'PENDIENTE';
  filtroTexto = '';
  public criterioOrden = 'fecha';
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

  cambiarOrden(event: any) {
  this.criterioOrden = event.target.value;
    this.aplicarFiltros();
  }

  aplicarFiltros() {
    let resultado = [...this.todasLasSolicitudes];

    // 1. Filtro Estado
    if (this.filtroEstado === 'PENDIENTE') {
      resultado = resultado.filter(s => s.estado === 'PENDIENTE');
    } else if (this.filtroEstado === 'FINALIZADA') {
      resultado = resultado.filter(s => s.estado === 'ACEPTADO' || s.estado === 'RECHAZADO');
    }

    // 2. Filtro Texto
    if (this.filtroTexto) {
      const texto = this.filtroTexto.toLowerCase();
      resultado = resultado.filter(s =>
        (s.especialista && s.especialista.toLowerCase().includes(texto)) ||
        (s.descripcion && s.descripcion.toLowerCase().includes(texto))
      );
    }

    // ORDENAMIENTO
  if (this.criterioOrden === 'especialista') {
    resultado = ordenarDinamicamente(resultado, 'especialista', 'asc');
  } else {
    // Por defecto ordenamos por FECHA (más reciente primero)
    resultado = ordenarDinamicamente(resultado, 'fechaSolicitud', 'desc');
  }

  this.solicitudesVisibles.set(resultado);
  }

  cambiarFiltroEstado(nuevoEstado: 'TODAS' | 'PENDIENTE' | 'FINALIZADA') {
    this.filtroEstado = nuevoEstado;
    this.aplicarFiltros();
  }

  prepararEliminacion(solicitud: any) {
    this.solicitudAEliminar = solicitud;
    this.mostrarAlerta(
      `¿Estás seguro de que deseas cancelar la solicitud enviada a ${solicitud.especialista}? Esta acción no se puede deshacer.`,
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
      error: (err) => {
        console.error(err);
        this.cerrarAlerta();
        this.mostrarAlerta('No se pudo eliminar la solicitud.', 'error');
      }
    });
  }

  toggleDropdown(menu: string, event: Event) {
  event.stopPropagation();
  this.dropdownOpen = this.dropdownOpen === menu ? null : menu;
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
  this.dropdownOpen = null;
  this.aplicarFiltros();
}

@HostListener('document:click', ['$event'])
onDocumentClick(event: MouseEvent) {
  const target = event.target as HTMLElement;
  if (!target.closest('.custom-select-wrapper')) {
    this.dropdownOpen = null;
  }
}
}
