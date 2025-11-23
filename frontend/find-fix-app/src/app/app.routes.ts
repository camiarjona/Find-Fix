import { AuthPage } from './pages/auth/auth.page';
import { Home } from './pages/home/home.page';
import { SeleccionarRolPage } from './pages/seleccionar-rol/seleccionar-rol.page';
import { AdminDahboardComponent } from './components/admin-components/admin-dahboard-component/admin-dahboard-component';
import { OficiosListAdminComponent } from './components/admin-components/oficios-list-admin-component/oficios-list-admin-component';
import { Component } from '@angular/core';
import { RolesListAdminComponent } from './components/admin-components/roles-list-admin-component/roles-list-admin-component';
import { Routes } from '@angular/router';
import { ClienteLayout } from './layouts/cliente/cliente-layout/cliente-layout';
import { DashboardPage } from './pages/cliente/dashboard.page/dashboard.page';
import { MisSolicitudesPage } from './pages/cliente/mis-solicitudes.page/mis-solicitudes.page';
import { BuscarEspecialistas } from './pages/cliente/buscar-especialistas.page/buscar-especialistas.page';
import { MisResenas } from './pages/cliente/mis-resenas/mis-resenas';
import { MisTrabajos } from './pages/cliente/mis-trabajos.page/mis-trabajos.page';
import { MisFavoritos } from './pages/cliente/mis-favoritos.page/mis-favoritos.page';
import { NuevaSolicitudEspecialistaPage } from './pages/cliente/nueva-solicitud-especialista.page/nueva-solicitud-especialista.page';
import { HistorialSolicitudesEspecialistaPages } from './pages/cliente/historial-solicitudes-especialista.pages/historial-solicitudes-especialista.pages';
import { EspecialistaLayout } from './layouts/especialista/especialista-layout/especialista-layout';
import { DashboardEspecialistaPage } from './pages/especialista/dashboard-especialista.page/dashboard-especialista.page';
import { SolicitudesPage } from './pages/especialista/solicitudes.page/solicitudes.page';
import { MisTrabajosPage } from './pages/especialista/mis-trabajos.page/mis-trabajos.page';
import { MisResenasPage } from './pages/especialista/mis-resenas.page/mis-resenas.page';

export const routes: Routes = [
  {
    path: '',
    component: Home
  },
  {
    path: 'auth',
    component: AuthPage
  },
  {
    path: 'seleccionar-rol',
    component: SeleccionarRolPage
  },
  {
    path: 'admin/dashboard',
    component: AdminDahboardComponent,
  },
  {
    path: 'admin/oficios',
    component: OficiosListAdminComponent,
  },

  // Rutas Privadas del Cliente
  {
    path: 'cliente',
    component: ClienteLayout,

    children: [
      {
        path: 'dashboard',
        component: DashboardPage
      },
      {
        path: 'mis-solicitudes',
        component: MisSolicitudesPage
      },
      { path: 'buscar-especialistas', component: BuscarEspecialistas},
      { path: 'mis-resenas', component: MisResenas},
      { path: 'mis-trabajos', component: MisTrabajos },
      { path: 'mis-favoritos', component: MisFavoritos },
      {
        path: 'solicitar-especialista/nueva',
        component: NuevaSolicitudEspecialistaPage
      },
      {
        path: 'solicitar-especialista/historial',
        component: HistorialSolicitudesEspecialistaPages
      },
      {
        path: '',
        redirectTo: 'dashboard',
        pathMatch: 'full'
      }
    ]
  },
  // Rutas Privadas del Especialista
{
    path: 'especialista',
    component: EspecialistaLayout, 
    children: [
      {
        path: 'dashboard',
        component: DashboardEspecialistaPage
      },

      {
        path: 'solicitudes',
        component: SolicitudesPage
      },
      {
        path: 'mis-trabajos',
        component: MisTrabajosPage
      },
      {
        path: 'mis-resenas',
        component: MisResenasPage
      },

      {
        path: '',
        redirectTo: 'dashboard',
        pathMatch: 'full'
      }
    ]
  },

];
