import { CommonModule } from '@angular/common';
import { Component, inject, OnInit, signal, WritableSignal } from '@angular/core';
import { TrabajoAppService } from '../../../services/trabajoApp-services/trabajo-app-service';
import { BuscarTrabajoApp, VisualizarTrabajoAppCliente } from '../../../models/trabajoApp-models/trabajo-app-model';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-mis-trabajos.page',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './mis-trabajos.page.html',
  styleUrl: './mis-trabajos.page.css',
})
export class MisTrabajos implements OnInit {
  private trabajoService = inject(TrabajoAppService);

  // Estado Principal
  public trabajos = signal<VisualizarTrabajoAppCliente[]>([]);
  public trabajosVisibles = signal<VisualizarTrabajoAppCliente[]>([]);
  public estaCargando = signal(true);

  // Filtros y Vistas
  public filtros: BuscarTrabajoApp = { titulo: '', estado: '', desde: '', hasta: '' };
  public modoVista: 'tarjetas' | 'lista' = 'tarjetas';
  public estadosPosibles = ['Creado', 'En proceso', 'En revision', 'Finalizado'];

  // Modal Detalle
  public trabajoSeleccionado: WritableSignal<VisualizarTrabajoAppCliente | null> = signal(null);

  ngOnInit() {
    this.cargarTrabajos();
  }

  cargarTrabajos() {
    this.estaCargando.set(true);
    this.trabajoService.obtenerTrabajosCliente().subscribe({
      next: (res) => {
        this.trabajos.set(res.data);
        this.aplicarFiltros();
        this.estaCargando.set(false);
      },
      error: (err) => {
        console.error(err);
        this.trabajos.set([]);
        this.estaCargando.set(false);
      }
    });
  }

  // Lógica de Filtrado Local
  aplicarFiltros() {
    let lista = this.trabajos();

    if (this.filtros.titulo) {
      const term = this.filtros.titulo.toLowerCase();
      lista = lista.filter(t =>
        t.nombreEspecialista.toLowerCase().includes(term) ||
        t.descripcion.toLowerCase().includes(term)
      );
    }

    if (this.filtros.estado) {
      lista = lista.filter(t => t.estado === this.filtros.estado);
    }

    this.trabajosVisibles.set(lista);
  }

  limpiarFiltros() {
    this.filtros = { titulo: '', estado: '', desde: '', hasta: '' };
    this.aplicarFiltros();
  }

  establecerModoVista(modo: 'tarjetas' | 'lista') {
    this.modoVista = modo;
  }

  // --- MODAL ---
  abrirModalDetalle(trabajo: VisualizarTrabajoAppCliente) {
    this.trabajoSeleccionado.set(trabajo);
  }

  cerrarModal() {
    this.trabajoSeleccionado.set(null);
  }

  // --- HELPERS VISUALES ---
  formatearTextoEstado(estado: string): string {
    return estado;
  }

  obtenerClaseEstado(estado: string): string {
    if (!estado) return '';
    const claseLimpia = estado.toLowerCase().replace(/\s+/g, '-').replace(/_/g, '-');

    return `status-${claseLimpia}`;
  }
}
