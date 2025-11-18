import { CommonModule } from '@angular/common';
import { Component, computed, inject, signal } from '@angular/core';
import { Router } from '@angular/router';
import { AuthService } from '../../../services/auth/auth.service';

@Component({
  selector: 'app-dashboard.page',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './dashboard.page.html',
  styleUrl: './dashboard.page.css',
})
export class DashboardPage {
  private router = inject(Router);
  private authService = inject(AuthService);

  nombreUsuario = computed(() => {
    return this.authService.currentUser()?.nombre || 'Cliente';
  });

  // (Estos podés dejarlos falsos por ahora, o cargarlos desde sus servicios)
  solicitudesPendientes = signal(2);
  trabajosEnProceso = signal(1);
  favoritosGuardados = signal(4);

  // funciones para navegar

  irABuscarEspecialistas() {
    this.router.navigate(['/app/buscar-especialistas']);
  }

  irASolicitudes() {
    this.router.navigate(['/app/mis-solicitudes']);
  }

  irATrabajos() {
    this.router.navigate(['/app/mis-trabajos']);
  }

  irAFavoritos() {
    this.router.navigate(['/app/mis-favoritos']);
  }

  irASerEspecialista() {
    this.router.navigate(['/app/solicitar-especialista/nueva']);
  }
}
