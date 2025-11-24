import { Component, inject, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { SolicitudEspecialistaService } from '../../../services/cliente/solicitud-especialista.service';
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

  private solicitudService = inject(SolicitudEspecialistaService);
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
    // this.cargarDatosReales();
    this.cargarDatosFalsos();
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

  // --- Carga de Datos (Simulación) ---
  cargarDatosFalsos() {
    this.estaCargando.set(true);
    setTimeout(() => {
      this.todasLasSolicitudes = [
        {
          especialista: 'Juan Perez (Plomero)',
          titulo: 'Arreglo de cañería',
          descripcion: 'Hola Juan, tengo una urgencia con un caño roto en la cocina.',
          fechaSolicitud: new Date('2023-11-25'),
          estado: 'PENDIENTE'
        },
        {
          especialista: 'Electricidad Total S.A.',
          titulo: 'Instalación Aire Acondicionado',
          descripcion: 'Necesito presupuesto para instalar un split de 3000 frigorías.',
          fechaSolicitud: new Date('2023-11-20'),
          estado: 'ACEPTADO'
        },
        {
          especialista: 'Carpintería El Roble',
          titulo: 'Mesa a medida',
          descripcion: 'Quiero una mesa de comedor de 2x1 metros en madera maciza.',
          fechaSolicitud: new Date('2023-11-15'),
          estado: 'RECHAZADO'
         },
        {
          especialista: 'Servicio Técnico PC',
          titulo: 'Formateo Notebook',
          descripcion: 'Mi laptop anda muy lenta, necesito limpieza y formateo.',
          fechaSolicitud: new Date('2023-11-24'),
          estado: 'PENDIENTE'
        }
      ];
      this.aplicarFiltros();
      this.estaCargando.set(false);
    }, 800);
  }

  /* //  INTEGRACIÓN CON BACKEND
cargarDatosReales() {
    this.estaCargando.set(true);
    this.solicitudTrabajoService.obtenerMisSolicitudesEnviadas().subscribe({
      next: (response) => {
        this.todasLasSolicitudes = response.data.map(s => ({
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
    */

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
    // Simulación Local
    this.todasLasSolicitudes = this.todasLasSolicitudes.filter(s => s.id !== solicitud.id);
    this.aplicarFiltros();

    this.cerrarAlerta();
    this.mostrarAlerta('Solicitud cancelada correctamente.', 'success');

    /* // BACKEND REAL
    this.solicitudService.eliminarSolicitud(solicitud.id).subscribe({
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
    */
  }
}
