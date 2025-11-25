import { Component, inject } from '@angular/core';
import { Router, RouterLink } from '@angular/router';
import { AuthService } from '../../../services/auth/auth.service';
import { CommonModule } from '@angular/common';
import { ThemeService } from '../../../services/tema/theme.service';

@Component({
  selector: 'app-admin-dahboard-component',
  standalone: true,
  imports: [RouterLink, CommonModule],
  templateUrl: './admin-dahboard-component.html',
  styleUrl: './admin-dahboard-component.css',
})
export class AdminDahboardComponent {
  private authService = inject(AuthService);
  private router = inject(Router);
  public themeService = inject(ThemeService);


  logout() {
    this.authService.logout();
  }

  public adminPanels = [
    {
      title: 'Gestión de Usuarios',
      description: 'Ver, filtrar y gestionar cuentas de clientes y especialistas.',
      icon: 'm10,13h-2c-2.76,0-5,2.24-5,5v1c0,.55.45,1,1,1h10c.55,0,1-.45,1-1v-1c0-2.76-2.24-5-5-5Zm-5,5c0-1.65,1.35-3,3-3h2c1.65,0,3,1.35,3,3H5Zm7.73-11.49c-.08-.22-.19-.42-.3-.62,0,0,0,0,0-.01-.69-1.14-1.93-1.89-3.42-1.89-2.28,0-4,1.72-4,4s1.72,4,4,4c1.49,0,2.73-.74,3.42-1.89,0,0,0,0,0-.01.12-.2.22-.4.3-.62.02-.06.03-.12.05-.18.06-.17.11-.34.15-.52.05-.25.07-.51.07-.78s-.03-.53-.07-.78c-.03-.18-.09-.35-.15-.52-.02-.06-.03-.12-.05-.18Zm-3.73,3.49c-1.18,0-2-.82-2-2s.82-2,2-2,2,.82,2,2-.82,2-2,2Zm6,1.49c-.11,0-.22-.01-.33-.03-.22.66-.56,1.27-.98,1.81.41.13.84.22,1.31.22,2.28,0,4-1.72,4-4s-1.72-4-4-4c-.47,0-.9.09-1.31.22.43.53.76,1.14.98,1.81.11-.01.21-.03.33-.03,1.18,0,2,.82,2,2s-.82,2-2,2Zm1,3h-1.11c.6.58,1.08,1.27,1.44,2.03,1.5.17,2.67,1.43,2.67,2.97h-2v1c0,.35-.07.69-.18,1h3.18c.55,0,1-.45,1-1v-1c0-2.76-2.24-5-5-5Z',
      routerLink: '/admin/usuarios',
      color: '#F58634' // Puedes cambiar colores aquí si quieres variedad luego
    },
    {
      title: 'Solicitudes de Especialista',
      description: 'Revisar y aprobar o rechazar solicitudes para ser especialista.',
      routerLink: '/admin/solicitudes',
      icon: 'M19 3h-4.18C14.4 1.84 13.3 1 12 1c-1.3 0-2.4.84-2.82 2H5c-1.1 0-2 .9-2 2v14c0 1.1.9 2 2 2h14c1.1 0 2-.9 2-2V5c0-1.1-.9-2-2-2zm-7 0c.55 0 1 .45 1 1s-.45 1-1 1-1-.45-1-1 .45-1 1-1zm2 14H7v-2h7v2zm3-4H7v-2h10v2zm0-4H7V7h10v2z',
      color: '#F58634'
    },
    {
      title: 'Gestión de Oficios',
      description: 'Crear, modificar o eliminar los oficios disponibles en la app.',
      icon: 'm21,15c0-.61-.06-1.22-.18-1.81-.12-.58-.29-1.15-.52-1.69-.23-.53-.51-1.05-.83-1.53-.32-.48-.69-.93-1.1-1.33-.41-.41-.86-.78-1.33-1.1-.48-.32-1-.6-1.53-.83-.16-.07-.34-.12-.5-.18v-1.52c0-.55-.45-1-1-1h-4c-.55,0-1,.45-1,1v1.52c-.17.06-.34.11-.5.18-.53.23-1.05.51-1.53.83-.48.32-.92.69-1.33,1.1-.41.41-.78.86-1.1,1.33-.32.48-.6,1-.83,1.53-.23.54-.41,1.11-.53,1.69-.12.59-.18,1.2-.18,1.81v3h-1v2h20v-2h-1v-3Zm-16,0c0-.47.05-.95.14-1.41.09-.45.23-.89.41-1.31.18-.42.39-.81.64-1.19.25-.37.54-.72.86-1.04s.67-.6,1.04-.86c.29-.2.6-.36.91-.51v6.32h2V6h2v9h2v-6.32c.32.15.62.32.91.51.37.25.72.54,1.04.86s.6.66.85,1.04c.25.37.47.77.65,1.19.18.42.32.86.41,1.31.09.46.14.94.14,1.41v3H5v-3Z',
      routerLink: '/admin/oficios',
      color: '#F58634'
    },
    {
      title: 'Gestión de Roles',
      description: 'Ver la lista de roles del sistema y gestionarlos.',
      icon: 'M12 1L3 5v6c0 5.55 3.84 10.74 9 12 5.16-1.26 9-6.45 9-12V5l-9-4zm0 10.99h7c-.53 4.12-3.28 7.79-7 8.94V12H5V6.3l7-3.11v8.8z',
      routerLink: '/admin/roles',
      color: '#F58634'
    },
  ];

  toggleTheme() {
    this.themeService.toggleTheme();
  }
}
