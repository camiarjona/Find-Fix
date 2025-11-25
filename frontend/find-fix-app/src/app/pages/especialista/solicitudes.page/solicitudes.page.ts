import { Component, inject, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { EspecialistaService } from '../../../services/especialista/especialista.service';
import { ModalConfirmacionComponent } from '../../../components/cliente/modal-confirmacion.component/modal-confirmacion.component';
import { Router } from '@angular/router';

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

  solicitudesVisibles = signal<any[]>([]);
  todasLasSolicitudes: any[] = [];
  estaCargando = signal(true);

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

  filtroEstado: 'TODAS' | 'PENDIENTE' | 'FINALIZADA' = 'PENDIENTE';
  filtroTexto = '';

  ngOnInit() {
    this.cargarDatosReales();
  }

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

  // Filtros
  aplicarFiltros() {
    let resultado = this.todasLasSolicitudes;

    // Filtro por Estado
    if (this.filtroEstado === 'PENDIENTE') {
      resultado = resultado.filter(s => s.estado === 'PENDIENTE');
    } else if (this.filtroEstado === 'FINALIZADA') {
      resultado = resultado.filter(s => s.estado === 'ACEPTADO' || s.estado === 'RECHAZADO');
    }


    // Filtro por Texto
    if (this.filtroTexto) {
      const texto = this.filtroTexto.toLowerCase();
      resultado = resultado.filter(s =>
        s.titulo.toLowerCase().includes(texto) ||
        s.cliente.toLowerCase().includes(texto)
      );
    }

    this.solicitudesVisibles.set(resultado);
  }

  cambiarFiltroEstado(nuevoEstado: 'TODAS' | 'PENDIENTE' | 'FINALIZADA') {
    this.filtroEstado = nuevoEstado;
    this.aplicarFiltros();
  }

  aceptarSolicitud(solicitud: any) {
    this.servicioEspecialista.responderSolicitud(solicitud.id, 'ACEPTADO').subscribe({
      next: () => {
        this.cargarDatosReales(); // Recargar datos del servidor
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

  irAMisTrabajos() {
    this.cerrarAlerta();
    this.router.navigate(['/especialista/mis-trabajos']);
  }

  irAlInicio() {
    this.cerrarAlerta();
    this.router.navigate(['/especialista/dashboard']);
  }
}
