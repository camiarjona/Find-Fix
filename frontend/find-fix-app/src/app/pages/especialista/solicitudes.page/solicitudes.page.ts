import { Component, inject, OnInit, signal, computed } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { EspecialistaService } from '../../../services/especialista/especialista.service';
import { ModalConfirmacionComponent } from '../../../components/cliente/modal-confirmacion.component/modal-confirmacion.component';
import { Router } from '@angular/router';
import { DireccionOrden } from '../../../models/enums/enums.model';
import { ordenarDinamicamente } from '../../../utils/sort-utils';

@Component({
  selector: 'app-solicitudes.page',
  standalone: true,
  imports: [CommonModule, FormsModule, ModalConfirmacionComponent],
  templateUrl: './solicitudes.page.html',
  styleUrl: './solicitudes.page.css',
})
export class SolicitudesPage implements OnInit {
  private servicioEspecialista = inject(EspecialistaService);
  private router = inject(Router);

  todasLasSolicitudes: any[] = [];      // Datos brutos del back
  solicitudesFiltradas: any[] = [];    // Datos tras filtros de estado/texto
  solicitudesVisibles = signal<any[]>([]); // Lo que se ve en la página actual
  estaCargando = signal(true);

  currentPage = signal(0);
  pageSize = 6;
  totalPages = signal(0);

  alertaVisible = signal(false);
  mensajeAlerta = signal('');
  tipoAlerta = signal<'success' | 'error'>('success');

  esModalNavegacion = signal(false);
  mostrarModal = signal(false);
  datosModal = signal({
    titulo: '',
    mensaje: '',
    tipo: 'pregunta' as 'pregunta' | 'exito',
    textoConfirmar: 'Confirmar'
  });

  // --- Filtros ---
  filtroEstado: 'TODAS' | 'PENDIENTE' | 'FINALIZADA' = 'PENDIENTE';
  filtroTexto = '';

  // variables para el orden
  criterioOrden = 'fechaSolicitud';
  direccionOrden: DireccionOrden = 'desc';
  dropdownOpen: string | null = null;

  ngOnInit() {
    this.cargarDatosReales();
  }

  cargarDatosReales() {
    this.estaCargando.set(true);
    this.servicioEspecialista.getSolicitudesRecibidas().subscribe({
      next: (data) => {
        this.todasLasSolicitudes = data;
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
    let resultado = this.todasLasSolicitudes;

    if (this.filtroEstado === 'PENDIENTE') {
      resultado = resultado.filter(s => s.estado === 'PENDIENTE');
    } else if (this.filtroEstado === 'FINALIZADA') {
      resultado = resultado.filter(s => s.estado === 'ACEPTADO' || s.estado === 'RECHAZADO');
    }

    if (this.filtroTexto) {
      const texto = this.filtroTexto.toLowerCase();
      resultado = resultado.filter(s =>
        (s.titulo && s.titulo.toLowerCase().includes(texto)) ||
        (s.cliente && s.cliente.toLowerCase().includes(texto))
      );
    }

  this.solicitudesFiltradas = ordenarDinamicamente(
    resultado,
    this.criterioOrden,
    this.direccionOrden
  );

    this.solicitudesFiltradas = resultado;
    this.totalPages.set(Math.ceil(this.solicitudesFiltradas.length / this.pageSize));

    this.currentPage.set(0);
    this.actualizarVistaPaginada();
  }


  actualizarVistaPaginada() {
    const inicio = this.currentPage() * this.pageSize;
    const fin = inicio + this.pageSize;
    this.solicitudesVisibles.set(this.solicitudesFiltradas.slice(inicio, fin));
  }

cambiarOrden(columna: string) {
  if (this.criterioOrden === columna) {
    this.direccionOrden = this.direccionOrden === 'asc' ? 'desc' : 'asc';
  } else {
    this.criterioOrden = columna;
    this.direccionOrden = 'asc';
  }
  this.aplicarFiltros();
}

toggleDropdown(menu: string, event: Event) {
  event.stopPropagation();
  this.dropdownOpen = this.dropdownOpen === menu ? null : menu;
}

seleccionarOrden(criterio: string) {
  this.criterioOrden = criterio;
  this.direccionOrden = criterio === 'fechaSolicitud' ? 'desc' : 'asc';
  this.dropdownOpen = null;
  this.aplicarFiltros();
}

  cambiarFiltroEstado(nuevoEstado: 'TODAS' | 'PENDIENTE' | 'FINALIZADA') {
    this.filtroEstado = nuevoEstado;
    this.aplicarFiltros();
  }

  limpiarFiltros() {
  this.filtroTexto = '';
  this.filtroEstado = 'PENDIENTE';
  this.criterioOrden = 'fechaSolicitud';
  this.aplicarFiltros();
}

  cambiarPagina(nuevaPagina: number) {
    this.currentPage.set(nuevaPagina);
    this.actualizarVistaPaginada();
    window.scrollTo({ top: 0, behavior: 'smooth' });
  }

  aceptarSolicitud(solicitud: any) {
    this.servicioEspecialista.responderSolicitud(solicitud.id, 'ACEPTADO').subscribe({
      next: () => {
        this.cargarDatosReales();
        this.esModalNavegacion.set(true);
        this.mostrarAlerta('Solicitud aceptada correctamente', 'success');
      },
      error: () => this.mostrarAlerta('Error al aceptar la solicitud', 'error')
    });
  }

  rechazarSolicitud(solicitud: any) {
    this.servicioEspecialista.responderSolicitud(solicitud.id, 'RECHAZADO').subscribe({
      next: () => {
        this.cargarDatosReales();
        this.esModalNavegacion.set(false);
        this.mostrarAlerta('Solicitud rechazada', 'success');
      },
      error: () => this.mostrarAlerta('Error al rechazar', 'error')
    });
  }

  // --- Helpers UI y Navegación ---
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

  mostrarAlerta(mensaje: string, tipo: 'success' | 'error' = 'success') {
    this.mensajeAlerta.set(mensaje);
    this.tipoAlerta.set(tipo);
    this.alertaVisible.set(true);
  }

  cerrarAlerta() {
    this.alertaVisible.set(false);
    this.esModalNavegacion.set(false);
  }

  irAMisTrabajos() {
    this.cerrarAlerta();
    this.router.navigate(['/especialista/mis-trabajos']);
  }

  irAlInicio() {
    this.cerrarAlerta();
    this.router.navigate(['/especialista/dashboard']);
  }
}


