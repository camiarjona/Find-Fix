import { EstadoSolicitud } from './../../../models/enums/enums.model';
import { CommonModule } from '@angular/common';
import { Component, computed, inject, signal, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { AuthService } from '../../../services/auth/auth.service';
// import { ResenaService } from '../../../services/resena/resena.service';
import { toSignal } from '@angular/core/rxjs-interop';
import { TrabajoAppService } from '../../../services/trabajoApp-services/trabajo-app-service';
import { TrabajoExternoService } from '../../../services/trabajoExterno-services/trabajo-externo-service';
import { FavoritoService } from '../../../services/favoritos/lista-favs.service';
import { SolicitudTrabajoService } from '../../../services/cliente/solicitud-trabajo.service';
@Component({
  selector: 'app-dashboard.page',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './dashboard.page.html',
  styleUrl: './dashboard.page.css',
})
export class DashboardPage implements OnInit {
  private router = inject(Router);
  private authService = inject(AuthService);
  private trabajoAppService = inject(TrabajoAppService);
  private trabajoExternoService = inject(TrabajoExternoService);

  private solicitudTrabajoService = inject(SolicitudTrabajoService);
  private favoritoService = inject(FavoritoService);
  // private resenaService = inject(ResenaService);

  nombreUsuario = computed(() => {
    return this.authService.currentUser()?.nombre || 'Cliente';
  });

  solicitudesPendientes = signal<number>(0);
  favoritosGuardados = signal<number>(0);

  // señal que guarda el conteo combinado de trabajos en proceso
  trabajosEnProcesoCount = signal<number>(0);
  trabajosEnProceso = computed(() => this.trabajosEnProcesoCount());

  ngOnInit(): void {
    this.cargarTrabajosEnProceso();
    this.cargarFavoritos();
    this.cargarSolicitudesPendientes();
  }

  private cargarTrabajosEnProceso() {
    this.trabajosEnProcesoCount.set(0);
    this.trabajoAppService.obtenerTrabajosCliente().subscribe({
      next: (respApp) => {
        const listaApp = (respApp?.data || []) as any[];
        const countApp = listaApp.filter(t => (t?.estado || '').toString() === 'En proceso').length;

        this.trabajoExternoService.obtenerMisTrabajos().subscribe({
          next: (respExt) => {
            const listaExt = (respExt?.data || []) as any[];
            const countExt = listaExt.filter(t => (t?.estado || '').toString() === 'En proceso').length;
            this.trabajosEnProcesoCount.set(countApp + countExt);
          },
          error: (errExt) => {
            console.error('Error cargando trabajos externos para contador', errExt);
            this.trabajosEnProcesoCount.set(countApp);
          }
        });
      },
      error: (errApp) => {
        console.error('Error cargando trabajos APP para contador', errApp);
        this.trabajoExternoService.obtenerMisTrabajos().subscribe({
          next: (respExt) => {
            const listaExt = (respExt?.data || []) as any[];
            const countExt = listaExt.filter(t => (t?.estado || '').toString() === 'EN_PROCESO').length;
            this.trabajosEnProcesoCount.set(countExt);
          },
          error: (errExt) => {
            console.error('Error cargando trabajos externos para contador (fallback)', errExt);
            this.trabajosEnProcesoCount.set(0);
          }
        });
      }
    });
  }

  private cargarFavoritos() {
    this.favoritosGuardados.set(0);
    this.favoritoService.obtenerFavoritosPorCliente().subscribe({
      next: (resp) => {
        const listaFavs = (resp?.data || []) as any[];
        const conuntFavs = listaFavs.length;
        this.favoritosGuardados.set(conuntFavs);
      },
      error: (err) => {
        console.error('Error cargando favoritos');
      }
    })
  }

  private cargarSolicitudesPendientes() {
    this.solicitudesPendientes.set(0);
    this.solicitudTrabajoService.obtenerMisSolicitudesEnviadas().subscribe({
      next: (resp) => {
        const listaSolis = (resp?.data || []) as any[];
        const countPend = listaSolis.filter(s => (s?.estado || '').toString() === 'PENDIENTE').length;
        this.solicitudesPendientes.set(countPend);
      },
      error: (err) => {
        console.log('Error cargando solicitudes');
      }
    })
  }

  // resenasPendientes = computed(
  //   () => this.resenaService.resenasPendientesCliente().length
  // );

  // funciones para navegar
  irABuscarEspecialistas() {
    this.router.navigate(['/cliente/buscar-especialistas']);
  }

  irASolicitudes() {
    this.router.navigate(['/cliente/mis-solicitudes']);
  }

  irATrabajos() {
    this.router.navigate(['/cliente/mis-trabajos']);
  }

  irAFavoritos() {
    this.router.navigate(['/cliente/mis-favoritos']);
  }

  irASerEspecialista() {
    this.router.navigate(['/cliente/solicitar-especialista/nueva']);
  }

  irAResenas() {
    this.router.navigate(['/cliente/mis-resenas']);
  }
}
