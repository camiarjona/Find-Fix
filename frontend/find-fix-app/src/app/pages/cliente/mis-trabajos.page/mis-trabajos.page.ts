import { CommonModule } from '@angular/common';
import { Component, inject, OnInit, signal, WritableSignal, computed } from '@angular/core';
import { TrabajoAppService } from '../../../services/trabajoApp-services/trabajo-app-service';
import { BuscarTrabajoApp, VisualizarTrabajoAppCliente } from '../../../models/trabajoApp-models/trabajo-app-model';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';

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

  public trabajos = signal<VisualizarTrabajoAppCliente[]>([]); // Data cruda del back
  public trabajosFiltrados = signal<VisualizarTrabajoAppCliente[]>([]); // Resultado de los filtros
  public trabajosVisibles = signal<VisualizarTrabajoAppCliente[]>([]); // Lo que se ve en la página actual
  public estaCargando = signal(true);

  public currentPage = signal(0);
  public pageSize = 6;
  public totalPages = signal(0);

  public filtros: BuscarTrabajoApp = { titulo: '', estado: '', desde: '', hasta: '' };
  public modoVista: 'tarjetas' | 'lista' = 'tarjetas';
  public estadosPosibles = ['Creado', 'En proceso', 'En revision', 'Finalizado'];

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

  aplicarFiltros() {
    let lista = this.trabajos();

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

    // 3. Guardar resultado filtrado y calcular páginas
    this.trabajosFiltrados.set(lista);
    this.totalPages.set(Math.ceil(lista.length / this.pageSize));

    // 4. Volver a la primera página al filtrar y actualizar vista
    this.currentPage.set(0);
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

  limpiarFiltros() {
    this.filtros = { titulo: '', estado: '', desde: '', hasta: '' };
    this.aplicarFiltros();
  }

  establecerModoVista(modo: 'tarjetas' | 'lista') {
    this.modoVista = modo;
  }

  // --- Navegación ---
  irADejarResena(idTrabajo: number, event: Event) {
    event.stopPropagation();
    this.router.navigate(['/cliente/crear-resena', idTrabajo]);
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
