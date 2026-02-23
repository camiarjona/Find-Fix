import { Component, inject, OnInit, signal, WritableSignal } from '@angular/core';
import { CommonModule, DatePipe } from '@angular/common';
import { Router, RouterLink } from '@angular/router';
import { FormsModule } from '@angular/forms';

import { SolicitudEspecialistaAdminService } from '../../../services/admin-services/solicitud-especialista-admin';
import { MostrarSolicitud } from '../../../models/cliente/solicitud-especialista.model';
import { DireccionOrden } from '../../../models/enums/enums.model';
import { ordenarDinamicamente } from '../../../utils/sort-utils';

@Component({
  selector: 'app-solicitudes-especialista-admin',
  standalone: true,
  imports: [CommonModule, RouterLink, DatePipe, FormsModule],
  templateUrl: './solicitudes-especialista-admin-component.html',
  styleUrl: './solicitudes-especialista-admin-component.css',
})
export class SolicitudesEspecialistaAdminComponent implements OnInit {

  private solicitudService = inject(SolicitudEspecialistaAdminService);

  public solicitudes: WritableSignal<MostrarSolicitud[] | null> = signal(null);
  public loading = signal(false);
  public error = signal<string | null>(null);

  private router = inject(Router);
  // Paginación
  public currentPage = signal(0);
  public totalPages = signal(0);
  public pageSize = 5;


  // Variables para filtro y orden (Locales)
  public criterioOrden = 'fechaSolicitud';
  public direccionOrden: DireccionOrden = 'desc';
  public dropdownOpen: string | null = null;
  public filtroTexto = '';

  private todasLasSolicitudes: MostrarSolicitud[] = [];

  ngOnInit(): void {
    this.cargarSolicitudes();
  }

  cargarSolicitudes() {
    this.loading.set(true);
    this.solicitudService.obtenerSolicitudesEspecialista(this.currentPage(), this.pageSize).subscribe({
      next: (res) => {
        this.todasLasSolicitudes = res.content;
        this.aplicarFiltrosYOrden();
        this.totalPages.set(res.totalPages);
        this.loading.set(false);
      },
      error: (err) => {
        console.error(err);
        this.solicitudes.set([]);
        this.loading.set(false);
      }
    });
  }

  aplicarFiltrosYOrden() {
    let resultado = [...this.todasLasSolicitudes];

    if (this.filtroTexto) {
      const busqueda = this.filtroTexto.toLowerCase();
      resultado = resultado.filter(s => s.email.toLowerCase().includes(busqueda));
    }

    const listaOrdenada = ordenarDinamicamente(
      resultado,
      this.criterioOrden,
      this.direccionOrden
    );

    this.solicitudes.set(listaOrdenada);
  }

  seleccionarOrden(criterio: string) {
    this.criterioOrden = criterio;
    this.direccionOrden = criterio === 'fechaSolicitud' ? 'desc' : 'asc';
    this.dropdownOpen = null;
    this.aplicarFiltrosYOrden();
  }

  toggleDropdown(menu: string, event: Event) {
    event.stopPropagation();
    this.dropdownOpen = this.dropdownOpen === menu ? null : menu;
  }

  limpiarFiltros() {
    this.filtroTexto = '';
    this.criterioOrden = 'fechaSolicitud';
    this.direccionOrden = 'desc';
    this.aplicarFiltrosYOrden();
  }

  cambiarPagina(delta: number) {
    const nuevaPagina = this.currentPage() + delta;
    if (nuevaPagina >= 0 && nuevaPagina < this.totalPages()) {
      this.currentPage.set(nuevaPagina);
      this.cargarSolicitudes();
    }
  }

  verDetalle(id: number): void {
    console.log(`Ver detalle de la solicitud: ${id}`);
    this.router.navigate(['/admin/solicitudes/ficha', id]);
  }
}
