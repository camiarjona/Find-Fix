import { Component } from '@angular/core';
import { RouterLink } from '@angular/router';

@Component({
  selector: 'app-admin-dahboard-component',
  imports: [RouterLink],
  templateUrl: './admin-dahboard-component.html',
  styleUrl: './admin-dahboard-component.css',
})
export class AdminDahboardComponent {
  public adminPanels = [
    {
      title: 'Gestión de Usuarios',
      description: 'Ver, filtrar, y gestionar cuentas de clientes y especialistas.',
      icon: '🧑​',
     //  routerLink: '/admin/usuarios',
      color: '#F58634'
    },
    {
      title: 'Solicitudes de Especialista',
      description: 'Revisar y aprobar o rechazar solicitudes para ser especialista.',
      icon: '👷🏻​',
      routerLink: '/admin/solicitudes',
      color: '#F58634'
    },
    {
      title: 'Gestión de Oficios',
      description: 'Crear, modificar o eliminar los oficios disponibles en la app.',
      icon: '💼​',
      routerLink: '/admin/oficios',
      color: '#F58634'
    },
    {
      title: 'Gestión de Roles',
      description: 'Ver la lista de roles del sistema y gestionarlos (Solo ADMIN).',
      icon: '📋​',
      routerLink: '/admin/roles',
      color: '#F58634'
    },
  ];
}
