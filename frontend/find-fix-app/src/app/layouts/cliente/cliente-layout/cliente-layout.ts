import { Component, inject, signal } from '@angular/core';
import { Router, RouterLink, RouterLinkActive, RouterOutlet } from "@angular/router";
import { AuthService } from '../../../services/auth/auth.service';
import { UserService } from '../../../services/user/user.service';
import { UserProfile } from '../../../models/user/user.model';
import { FooterComponent } from "../../../components/general/footer-component/footer-component";
import { UI_ICONS } from '../../../models/general/ui-icons';
import { ThemeService } from '../../../services/tema/theme.service';
import { NotificacionComponent } from "../../../components/notificacion/notificacion-component/notificacion-component";

@Component({
  selector: 'app-cliente-layout',
  standalone: true,
  imports: [RouterOutlet, RouterLink, RouterLinkActive, NotificacionComponent],
  templateUrl: './cliente-layout.html',
  styleUrl: './cliente-layout.css',
})
export class ClienteLayout {
  isSidebarOpen = signal(true);
  isEspecialistaMenuOpen = signal(false);
  isMobileMenuOpen = signal(false);

  isInvitationModalOpen = signal(false);

  public icons = UI_ICONS;

  isEspecialistaMode = signal(false);

  private authService = inject(AuthService);
  private router = inject(Router);
  private userService = inject(UserService);
  public themeService = inject(ThemeService);


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

  }

toggleTheme() {
    this.themeService.toggleTheme();
  }

 /** Cambia el rol de cliente a especialista y viceversa */
  toggleRole(event: Event) {
    const user = this.authService.currentUser();
    const input = event.target as HTMLInputElement;

    if (user?.roles?.includes('ESPECIALISTA') || user?.roles?.includes('ADMIN')) {
        this.isEspecialistaMode.set(true);
        this.router.navigateByUrl('/especialista/dashboard');
    } else {
        input.checked = false;
        this.isEspecialistaMode.set(false);
        this.isInvitationModalOpen.set(true);
    }
  }


  irASolicitud() {
    this.isInvitationModalOpen.set(false);
    this.router.navigateByUrl('/cliente/solicitar-especialista/nueva');
  }

  cerrarModalInvitacion() {
    this.isInvitationModalOpen.set(false);
  }
}
