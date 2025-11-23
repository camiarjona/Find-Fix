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

  ngOnInit(): void {
    this.loadSolicitudes();
  }

  loadSolicitudes(): void {
    this.loading.set(true);
    this.error.set(null);

    this.solicitudService.getSolicitudesAdmin().subscribe({
      next: (response) => {
        if (response.data) {
          this.solicitudes.set(response.data);
        } else {
          this.solicitudes.set([]);
        }
        this.loading.set(false);
      },
      error: (err) => {
        console.error('Error al cargar solicitudes:', err);
        this.error.set('Error al cargar las solicitudes. Verifique la conexión al backend.');
        this.loading.set(false);
      }
    });
  }

  verDetalle(id: number): void {
    console.log(`Ver detalle de la solicitud: ${id}`);
    this.router.navigate(['/admin/solicitudes/ficha', id]);
  }
}
