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
  private router = inject(Router);

  // --- Señales de Datos ---
  public trabajos = signal<VisualizarTrabajoAppCliente[]>([]); // Datos brutos del backend
  public trabajosFiltrados = signal<VisualizarTrabajoAppCliente[]>([]); // Resultado de aplicar filtros y orden
  public trabajosVisibles = signal<VisualizarTrabajoAppCliente[]>([]); // Lo que se muestra en la página actual
  public estaCargando = signal(true);

  // --- Paginación ---
  public currentPage = signal(0);
  public pageSize = 6;
  public totalPages = signal(0);

  // --- Filtros y UI ---
  public filtros: BuscarTrabajoApp = { titulo: '', estado: '', desde: '', hasta: '' };
  public modoVista: 'tarjetas' | 'lista' = 'tarjetas';
  public estadosPosibles = ['Creado', 'En proceso', 'En revision', 'Finalizado'];
  public criterioOrden = signal<string>('fecha');
  public dropdownOpen: string | null = null;

  // --- Detalle ---
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
        this.estaCargando.set(false);
      }
    });
  }

  aplicarFiltros() {
    let lista = [...this.trabajos()];

    // 1. Filtro por Texto
    if (this.filtros.titulo) {
      const term = this.filtros.titulo.toLowerCase();
      lista = lista.filter(t =>
        t.nombreEspecialista.toLowerCase().includes(term) ||
        (t.descripcion && t.descripcion.toLowerCase().includes(term))
      );
    }

    // 2. Filtro por Estado
    if (this.filtros.estado) {
      lista = lista.filter(t => t.estado === this.filtros.estado);
    }

    // 3. Ordenamiento (HU-137)
    const criterio = this.criterioOrden();
    if (criterio === 'especialista') {
      lista = ordenarDinamicamente(lista, 'nombreEspecialista', 'asc');
    } else if (criterio === 'estado') {
      lista = ordenarDinamicamente(lista, 'estado', 'asc');
    } else {
      // Orden por ID/Fecha descendente por defecto
      lista = ordenarDinamicamente(lista, 'id', 'desc');
    }

    // 4. Actualizar estado de filtrados y paginación
    this.trabajosFiltrados.set(lista);
    this.totalPages.set(Math.ceil(lista.length / this.pageSize));
    this.currentPage.set(0); // Reiniciar a la primera página tras filtrar

    this.actualizarVistaPaginada();
  }

  actualizarVistaPaginada() {
    const inicio = this.currentPage() * this.pageSize;
    const fin = inicio + this.pageSize;
    this.trabajosVisibles.set(this.trabajosFiltrados().slice(inicio, fin));
  }

  cambiarPagina(nuevaPagina: number) {
    this.currentPage.set(nuevaPagina);
    this.actualizarVistaPaginada();
    window.scrollTo({ top: 0, behavior: 'smooth' });
  }

  // --- Métodos de UI / Dropdowns ---
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
  onDocumentClick() {
    this.dropdownOpen = null;
  }

  // --- Helpers y Modales ---
  establecerModoVista(modo: 'tarjetas' | 'lista') {
    this.modoVista = modo;
  }

  abrirModalDetalle(trabajo: VisualizarTrabajoAppCliente) {
    this.trabajoSeleccionado.set(trabajo);
  }

  cerrarModal() {
    this.trabajoSeleccionado.set(null);
  }

  irADejarResena(idTrabajo: number, event: Event) {
    event.stopPropagation();
    this.router.navigate(['/cliente/crear-resena', idTrabajo]);
  }

  formatearTextoEstado(estado: string): string {
    return estado;
  }

  obtenerClaseEstado(estado: string): string {
    if (!estado) return '';
    const claseLimpia = estado.toLowerCase().replace(/\s+/g, '-').replace(/_/g, '-');
    return `status-${claseLimpia}`;
  }
}
