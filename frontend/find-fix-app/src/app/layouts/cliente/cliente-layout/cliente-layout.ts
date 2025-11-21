import { Component, inject, signal } from '@angular/core';
import { Router, RouterLink, RouterLinkActive, RouterOutlet } from "@angular/router";
import { AuthService } from '../../../services/auth/auth.service';
import { UserService } from '../../../services/user/user.service';
import { UserProfile } from '../../../models/user/user.model';
import { ModalMiPerfil } from "../../../components/user/modal-mi-perfil/modal-mi-perfil";

@Component({
  selector: 'app-cliente-layout',
  standalone: true,
  imports: [RouterOutlet, RouterLink, RouterLinkActive, ModalMiPerfil],
  templateUrl: './cliente-layout.html',
  styleUrl: './cliente-layout.css',
})
export class ClienteLayout {

  //Estados de la Barra Lateral
  isSidebarOpen = signal(true);
  isEspecialistaMenuOpen = signal(false);
  isMobileMenuOpen = signal(false);

  //ESTADOS PARA LOS CONTROLES DEL HEADER

  /** Controla el modo de color (true = oscuro, false = claro) */
  isDarkMode = signal(true); // 'true' para que la luna se muestre por defecto

  /** Controla el modo de vista (false = cliente, true = especialista) */
  isEspecialistaMode = signal(false); // 'false' para que el cliente esté activo por defecto

  private authService = inject(AuthService);
  private router = inject(Router);
  private userService = inject(UserService);

  // NUEVAS SEÑALES PARA EL PERFIL
  showProfileModal = signal(false);
  currentUserProfile = signal<UserProfile | null>(null);

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
    this.isDarkMode.update(prev => !prev);
    // lógica para cambiar
    console.log("Modo oscuro:", this.isDarkMode());
  }

  /** Cambia el rol de cliente a especialista y viceversa */
  toggleRole() {
    this.isEspecialistaMode.update(prev => !prev);
    // lógica para redirigir al dashboard de especialista
    console.log("Modo especialista:", this.isEspecialistaMode());
  }

  // --- FUNCIONES PERFIL ---
  openProfile() {
    console.log("1. Botón presionado");
    this.handleLinkClick();

    this.userService.getProfile().subscribe({
      next: (res) => {
        console.log("2. Datos recibidos:", res);
        this.currentUserProfile.set(res.data);
        this.showProfileModal.set(true);
      },
      error: (err) => {
        console.error("3. ERROR al cargar perfil:", err);
        console.error('Error al cargar perfil', err);
      }
    });
  }

  // 6. FUNCIÓN PARA CERRAR
  closeProfile() {
    this.showProfileModal.set(false);
  }
}
