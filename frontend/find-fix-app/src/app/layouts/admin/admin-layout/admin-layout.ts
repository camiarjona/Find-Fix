import { Component, inject, signal } from '@angular/core';
import { AuthService } from '../../../services/auth/auth.service';
import { Router, RouterOutlet, RouterLink, RouterLinkActive, RouterLinkWithHref } from '@angular/router';
import { FooterComponent } from "../../../components/general/footer-component/footer-component";
import { ThemeService } from '../../../services/tema/theme.service';
import { NotificacionComponent } from "../../../components/notificacion/notificacion-component/notificacion-component";

@Component({
  selector: 'app-admin-layout',
  imports: [RouterOutlet, RouterLinkWithHref, NotificacionComponent],
  templateUrl: './admin-layout.html',
  styleUrl: './admin-layout.css',
})
export class AdminLayout {
  isSidebarOpen = signal(true);
  isEspecialistaMenuOpen = signal(false);
  isMobileMenuOpen = signal(false);

  //ESTADOS PARA LOS CONTROLES DEL HEADER

  /** Controla el modo de vista (false = cliente, true = especialista) */
  isEspecialistaMode = signal(false); // 'false' para que el cliente esté activo por defecto

  private authService = inject(AuthService);
  private router = inject(Router);
    public themeService = inject(ThemeService);


  // --- Funciones de la Barra Lateral ---
  toggleSidebar() {
    this.isSidebarOpen.update(isOpen => !isOpen);
    if (!this.isSidebarOpen()) {
      this.isEspecialistaMenuOpen.set(false);
    }
  }

  toggleEspecialistaMenu(event: MouseEvent) {
    event.preventDefault();
    if (!this.isSidebarOpen()) {
      this.isSidebarOpen.set(true);
    }
    this.isEspecialistaMenuOpen.update(isOpen => !isOpen);
  }

  toggleMobileMenu() {
    this.isMobileMenuOpen.update(isOpen => !isOpen);
  }

  handleLinkClick() {
    this.isMobileMenuOpen.set(false);
    this.isEspecialistaMenuOpen.set(false);
  }

  logout() {
    console.log("Cerrar sesión");
    this.authService.logout();
    this.handleLinkClick();
    this.router.navigateByUrl('/auth');
  }

  /** Cambia el tema de oscuro a claro y viceversa */
toggleTheme() {
    this.themeService.toggleTheme();
  }


  /** Cambia el rol de cliente a especialista y viceversa */
  toggleRole() {
    this.isEspecialistaMode.update(prev => !prev);
    // lógica para redirigir al dashboard de especialista
    console.log("Modo especialista:", this.isEspecialistaMode());
  }
}

