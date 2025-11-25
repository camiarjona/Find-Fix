import { Component, inject, signal } from '@angular/core';
import { Router, RouterLink, RouterLinkActive, RouterOutlet } from '@angular/router';
import { AuthService } from '../../../services/auth/auth.service';
import { ThemeService } from '../../../services/tema/theme.service';

@Component({
  selector: 'app-especialista-layout',
  standalone: true,
  imports: [RouterOutlet, RouterLink, RouterLinkActive],
  templateUrl: './especialista-layout.html',
  styleUrl: './especialista-layout.css',
})
export class EspecialistaLayout {

  // Estados de la Barra Lateral
  isSidebarOpen = signal(true);
  isMobileMenuOpen = signal(false);

  // ESTADOS PARA LOS CONTROLES DEL HEADER

  isEspecialistaMode = signal(true);

  private authService = inject(AuthService);
  private router = inject(Router);
  public themeService = inject(ThemeService);

  // --- Funciones de la Barra Lateral ---
  toggleSidebar() {
    this.isSidebarOpen.update(isOpen => !isOpen);
  }

  toggleMobileMenu() {
    this.isMobileMenuOpen.update(isOpen => !isOpen);
  }

  handleLinkClick() {
    this.isMobileMenuOpen.set(false);
  }

  logout() {
    this.authService.logout();
  }

 toggleTheme() {
    this.themeService.toggleTheme();
  }

  /** Cambia el rol de especialista a cliente */
  toggleRole() {
    this.isEspecialistaMode.set(false);
    this.router.navigateByUrl('/cliente/dashboard');
  }
}

