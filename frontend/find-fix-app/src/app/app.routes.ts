import { AuthPage } from './pages/auth/auth.page';
import { Home } from './pages/home/home.page';
import { SeleccionarRolPage } from './pages/seleccionar-rol/seleccionar-rol.page';
import { AdminDahboardComponent } from './components/admin-components/admin-dahboard-component/admin-dahboard-component';
import { Routes } from '@angular/router';
import { ClienteLayout } from './layouts/cliente/cliente-layout/cliente-layout';
import { DashboardPage } from './pages/cliente/dashboard.page/dashboard.page';
import { MisSolicitudesPage } from './pages/cliente/mis-solicitudes.page/mis-solicitudes.page';
import { BuscarEspecialistas } from './pages/cliente/buscar-especialistas.page/buscar-especialistas.page';
import { MisTrabajos } from './pages/cliente/mis-trabajos.page/mis-trabajos.page';
import { MisFavoritosPage } from './pages/cliente/mis-favoritos.page/mis-favoritos.page';
import { NuevaSolicitudEspecialistaPage } from './pages/cliente/nueva-solicitud-especialista.page/nueva-solicitud-especialista.page';
import { HistorialSolicitudesEspecialistaPages } from './pages/cliente/historial-solicitudes-especialista.pages/historial-solicitudes-especialista.pages';
import { EspecialistaLayout } from './layouts/especialista/especialista-layout/especialista-layout';
import { DashboardEspecialistaPage } from './pages/especialista/dashboard-especialista.page/dashboard-especialista.page';
import { SolicitudesPage } from './pages/especialista/solicitudes.page/solicitudes.page';
import { MisTrabajosPage } from './pages/especialista/mis-trabajos.page/mis-trabajos.page';
import { MisResenasEspecialista } from './pages/especialista/mis-resenas-especialista/mis-resenas-especialista';
import { AdminLayout } from './layouts/admin/admin-layout/admin-layout';
import { OficiosListPage } from './pages/admin-pages/oficios-list.page/oficios-list.page';
import { RolesListPage } from './pages/admin-pages/roles-list.page/roles-list.page';
import { GestionUsers } from './pages/admin-pages/gestion-users/gestion-users';
import { SolicitudDetalleAdminComponent } from './components/admin-components/solicitud-detalle-admin/solicitud-detalle-admin';
import { SolicitudesEspecialistaAdminComponent } from './components/admin-components/solicitudes-especialista-admin-component/solicitudes-especialista-admin-component';
import { PerfilPage } from './pages/cliente/perfil/perfil.page';
import { MiPerfilEspecialista } from './pages/especialista/mi-perfil/mi-perfil';
import { LandingListEspecialistasComponent } from './pages/landing-page/landing-list-especialistas-component/landing-list-especialistas-component';
import { authGuard } from './guards/auth.guard';
import { roleGuard } from './guards/role.guard';
import { AccessDeniedPage } from './pages/access-denied.page/access-denied.page';
import { MisResenasEnviadasCliente } from './pages/cliente/mis-resenas-enviadas-cliente/mis-resenas-enviadas-cliente';


export const routes: Routes = [
  {
    path: '',
    component: Home
  },
  {
    path: 'auth',
    component: AuthPage,
  },
  {
    path: 'seleccionar-rol',
    component: SeleccionarRolPage,
    canActivate: [authGuard]
  },
  {
    path: 'admin/dashboard',
    component: AdminDahboardComponent
  },
  {
    path: 'acceso-denegado', component: AccessDeniedPage
  },
  {
    path: 'buscar-especialistas',
    component: LandingListEspecialistasComponent
  },
  {
    path: 'admin',
    component: AdminLayout,
    canActivate: [authGuard, roleGuard],
    data: { role: 'ADMIN' },
    children: [
      {
        path: 'oficios',
        component: OficiosListPage
      },
      {
        path: 'roles',
        component: RolesListPage
      },
      {
        path: 'usuarios',
        component: GestionUsers
      },
      {
        path: 'solicitudes/detalle/:id',
        component: SolicitudDetalleAdminComponent,
      },
      {
        path: 'solicitudes',
        component: SolicitudesEspecialistaAdminComponent,
      }
    ]
  },
  {
    path: 'cliente',
    component: ClienteLayout,
    canActivate: [authGuard, roleGuard],
    data: { role: 'CLIENTE' },
    children: [
      {
        path: 'dashboard',
        component: DashboardPage
      },
      {
        path: 'mis-solicitudes',
        component: MisSolicitudesPage
      },
      { path: 'buscar-especialistas', component: BuscarEspecialistas },
      { path: 'mis-trabajos', component: MisTrabajos },
      { path: 'mis-favoritos', component: MisFavoritosPage },
      { path: 'mis-resenas', component: MisResenasEnviadasCliente},
      {
        path: 'solicitar-especialista/nueva',
        component: NuevaSolicitudEspecialistaPage
      },
      {
        path: 'solicitar-especialista/historial',
        component: HistorialSolicitudesEspecialistaPages
      },
      {
        path: 'mi-perfil',
        component: PerfilPage
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
    canActivate: [authGuard, roleGuard],
    data: { role: 'ESPECIALISTA' },
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
        component: MisResenasEspecialista
      },
      {
        path: 'mi-perfil',
        component: MiPerfilEspecialista
      },
      {
        path: '',
        redirectTo: 'dashboard',
        pathMatch: 'full'
      }
    ]
  },

];
