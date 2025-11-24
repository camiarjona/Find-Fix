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

  filtroEstado: 'TODAS' | 'PENDIENTE' | 'FINALIZADA' = 'PENDIENTE';
  filtroTexto = '';

  ngOnInit() {
    // this.cargarDatosReales();
    this.cargarDatosFalsos();
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

  // (Datos Falsos)
  cargarDatosFalsos() {
    this.estaCargando.set(true);
    setTimeout(() => {
      this.todasLasSolicitudes = [
        {
          id: 1,
          cliente: 'María Rodriguez',
          descripcion: 'Tengo una fuga debajo de la bacha de la cocina, necesito urgente cambiar el flexible.',
          fechaSolicitud: new Date('2023-11-24'),
          estado: 'PENDIENTE'
        },
        {
          id: 2,
          cliente: 'Pedro Pascal',
          descripcion: 'Necesito instalar dos ventiladores de techo en las habitaciones.',
          fechaSolicitud: new Date('2023-11-22'),
          estado: 'PENDIENTE'
        },
        {
          id: 3,
          cliente: 'Lionel Messi',
          descripcion: 'Se trabó la persiana del living y no baja.',
          fechaSolicitud: new Date('2023-11-20'),
          estado: 'ACEPTADO'
        },
        {
          id: 4,
          titulo: 'Pintura habitación',
          cliente: 'Dibu Martinez',
          descripcion: 'Pintar una habitación de 3x3 color blanco.',
          fechaSolicitud: new Date('2023-11-15'),
          estado: 'RECHAZADO'
        }
      ];
      this.aplicarFiltros();
      this.estaCargando.set(false);
    }, 800);
  }

  /*
  // --- BACKEND REAL (Para el futuro) ---
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
  */

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
    // Lógica Local (Simulación)
    solicitud.estado = 'ACEPTADO';
    this.aplicarFiltros();

    this.esModalNavegacion.set(true);

    this.mostrarAlerta(
      `Has aceptado el trabajo de ${solicitud.cliente}. \n\nAhora puedes visualizarlo en "Mis Trabajos".`,
      'success'
    );

    /*
    // --- BACKEND REAL ---
    this.servicioEspecialista.responderSolicitud(solicitud.id, 'ACEPTADO').subscribe({
      next: () => {
         this.cargarDatosReales(); // Recargar datos del servidor
         this.esModalNavegacion.set(true);
         this.mostrarAlerta('Solicitud aceptada correctamente', 'success');
      },
      error: () => this.mostrarAlerta('Error al aceptar la solicitud', 'error')
    });
    */
  }

  rechazarSolicitud(solicitud: any) {
    // Lógica Local (Simulación)
    solicitud.estado = 'RECHAZADO';
    this.aplicarFiltros();

    this.esModalNavegacion.set(false);
    this.mostrarAlerta(`Has rechazado la solicitud de ${solicitud.cliente}.`, 'success');

    /*
    // --- BACKEND REAL ---
    this.servicioEspecialista.responderSolicitud(solicitud.id, 'RECHAZADO').subscribe({
      next: () => {
         this.cargarDatosReales();
         this.esModalNavegacion.set(false);
         this.mostrarAlerta('Solicitud rechazada', 'success');
      },
      error: () => this.mostrarAlerta('Error al rechazar', 'error')
    });
    */
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
