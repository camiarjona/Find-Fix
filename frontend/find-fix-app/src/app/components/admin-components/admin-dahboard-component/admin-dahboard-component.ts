import { Component } from '@angular/core';
import { RouterLink } from '@angular/router';

@Component({
  selector: 'app-admin-dahboard-component',
  imports: [RouterLink],
  templateUrl: './admin-dahboard-component.html',
  styleUrl: './admin-dahboard-component.css',
})
export class AdminDahboardComponent {
// Definición de las tarjetas/paneles del administrador
  public adminPanels = [
    {
      title: 'Gestión de Usuarios',
      description: 'Ver, filtrar, y gestionar cuentas de clientes y especialistas.',
      icon: '🧑​', // Usarás un ícono relevante
     //  routerLink: '/admin/usuarios', // Ruta propuesta para la gestión de usuarios
      color: '#F58634'
    },
    {
      title: 'Solicitudes de Especialista',
      description: 'Revisar y aprobar o rechazar solicitudes para ser especialista.',
      icon: '👷🏻​',
     //  routerLink: '/admin/solicitudes-especialista', // Ruta propuesta
      color: '#F58634'
    },
    {
      title: 'Gestión de Oficios',
      description: 'Crear, modificar o eliminar los oficios disponibles en la app.',
      icon: '💼​',
      routerLink: '/admin/oficios', // Ruta propuesta
      color: '#F58634'
    },
    {
      title: 'Gestión de Roles',
      description: 'Ver la lista de roles del sistema y gestionarlos (Solo ADMIN).',
      icon: '📋​',
     // routerLink: '/admin/roles', // Ruta propuesta
      color: '#F58634'
    },
  ];
}
