import { CommonModule } from '@angular/common';
import { Component, HostListener, inject, OnInit, signal, WritableSignal } from '@angular/core';
import { TrabajoAppService } from '../../../services/trabajoApp-services/trabajo-app-service';
import { BuscarTrabajoApp, VisualizarTrabajoAppCliente } from '../../../models/trabajoApp-models/trabajo-app-model';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { ordenarDinamicamente } from '../../../utils/sort-utils';

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
  private router = inject(Router);

  // Filtros y Vistas
  public filtros: BuscarTrabajoApp = { titulo: '', estado: '', desde: '', hasta: '' };
  public modoVista: 'tarjetas' | 'lista' = 'tarjetas';
  public estadosPosibles = ['Creado', 'En proceso', 'En revision', 'Finalizado'];

  // Modal Detalle
  public trabajoSeleccionado: WritableSignal<VisualizarTrabajoAppCliente | null> = signal(null);

  //Propiedades para ordenamiento
 public criterioOrden = signal<string>('fecha');
  public dropdownOpen: string | null = null;

  irADejarResena(idTrabajo: number, event: Event) {
    event.stopPropagation();
    this.router.navigate(['/cliente/crear-resena', idTrabajo]);
  }

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

aplicarFiltros() {
    let lista = [...this.trabajos()];

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

    const criterio = this.criterioOrden();
    if (criterio === 'especialista') {
      lista = ordenarDinamicamente(lista, 'nombreEspecialista', 'asc');
    } else if (criterio === 'estado') {
      lista = ordenarDinamicamente(lista, 'estado', 'asc');
    } else {
      lista = ordenarDinamicamente(lista, 'id', 'desc');
    }

    this.trabajosVisibles.set(lista);
  }

  // --- MÉTODOS DEL DROPDOWN ---
  toggleDropdown(menu: string, event: Event) {
    event.stopPropagation();
    this.dropdownOpen = this.dropdownOpen === menu ? null : menu;
  }

  seleccionarOrden(valor: string) {
    this.criterioOrden.set(valor);
    this.dropdownOpen = null;
    this.aplicarFiltros();
  }

  seleccionarEstado(valor: string) {
    this.filtros.estado = valor;
    this.dropdownOpen = null;
    this.aplicarFiltros();
  }

limpiarFiltros() {
    this.filtros = { titulo: '', estado: '', desde: '', hasta: '' };
    this.criterioOrden.set('fecha');
    this.aplicarFiltros();
  }

  @HostListener('document:click')
  onDocumentClick() { this.dropdownOpen = null; }

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
