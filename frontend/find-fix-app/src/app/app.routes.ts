import { Routes } from '@angular/router';
import { ClienteLayout } from './layouts/cliente/cliente-layout/cliente-layout';
import { DashboardPage } from './pages/cliente/dashboard.page/dashboard.page';
import { MisSolicitudesPage } from './pages/cliente/mis-solicitudes.page/mis-solicitudes.page';
import { NuevaSolicitudEspecialistaPage } from './pages/cliente/nueva-solicitud-especialista.page/nueva-solicitud-especialista.page';
import { HistorialSolicitudesEspecialistaPages } from './pages/cliente/historial-solicitudes-especialista.pages/historial-solicitudes-especialista.pages';
import { MisTrabajos } from './pages/cliente/mis-trabajos.page/mis-trabajos.page';
import { MisFavoritos } from './pages/cliente/mis-favoritos.page/mis-favoritos.page';
import { BuscarEspecialistas } from './pages/cliente/buscar-especialistas.page/buscar-especialistas.page';
import { MisResenas } from './pages/cliente/mis-resenas/mis-resenas';
// Importa tus otras páginas aquí (Dashboard, MisSolicitudes, etc.)

export const routes: Routes = [
  // --- Rutas Públicas (Login, Registro, Landing Page) ---
  // {
  //   path: 'login',
  //   component: LoginPage, // (Tu página de login)
  // },
  // {
  //   path: '',
  //   component: Home, // (La landing page que tenías)
  //   pathMatch: 'full'
  // },

  // --- Rutas Privadas del Cliente (¡Aquí está lo nuevo!) ---
  {
    path: 'app', // Prefijo para todas las rutas de cliente (ej: /app/dashboard)
    component: ClienteLayout, // Usa el Layout como "cáscara"
    // canActivate: [authGuard], // <-- Agrega tu guardia de autenticación aquí
    children: [
      {
        path: 'dashboard',
        component: DashboardPage // (Un componente de dashboard)
      },
      {
        path: 'mis-solicitudes',
        component: MisSolicitudesPage // (Otra página de cliente)
      },
      { path: 'buscar-especialistas', component: BuscarEspecialistas},
      { path: 'mis-resenas', component: MisResenas},
      { path: 'mis-trabajos', component: MisTrabajos },
      { path: 'mis-favoritos', component: MisFavoritos },
      {
        path: 'solicitar-especialista/nueva', // La del formulario
        component: NuevaSolicitudEspecialistaPage
      },
      {
        path: 'solicitar-especialista/historial', // La de la tabla
        component: HistorialSolicitudesEspecialistaPages
      },
      // ... otras rutas del cliente
      {
        path: '', // Redirige /app a /app/dashboard
        redirectTo: 'dashboard',
        pathMatch: 'full'
      }
    ]
  },

  // Redirección por defecto si no encuentra la ruta
  { path: '**', redirectTo: '' }
];
