import { Component, inject, OnInit, signal, WritableSignal } from '@angular/core';
import { CommonModule, DatePipe } from '@angular/common';
import { Router, RouterLink } from '@angular/router';

// Importaciones necesarias
import { SolicitudEspecialistaAdminService } from '../../../services/admin-services/solicitud-especialista-admin';
import { MostrarSolicitud } from '../../../models/cliente/solicitud-especialista.model';

@Component({
  selector: 'app-solicitudes-especialista-admin',
  standalone: true,
  imports: [CommonModule, RouterLink, DatePipe],
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

  ngOnInit(): void {
    this.cargarSolicitudes();
  }

  cargarSolicitudes() {
    this.loading.set(true);
    this.solicitudService.obtenerSolicitudesEspecialista(this.currentPage(), this.pageSize).subscribe({
      next: (res) => {
        this.solicitudes.set(res.content);
        this.totalPages.set(res.totalPages);
        this.loading.set(false);
      },
      error: (err) => {
        console.error('No se encontraron solicitudes:', err);
        this.solicitudes.set([]); // Limpiamos la lista si tira el NotFoundException
        this.totalPages.set(0);
        this.loading.set(false);
      }
    });
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
