import { Routes } from '@angular/router';
import { AuthPage } from './pages/auth/auth.page';
import { Home } from './pages/home/home.page';
import { SeleccionarRolPage } from './pages/seleccionar-rol/seleccionar-rol.page';
import { AdminDahboardComponent } from './components/admin-components/admin-dahboard-component/admin-dahboard-component';
import { OficiosListAdminComponent } from './components/admin-components/oficios-list-admin-component/oficios-list-admin-component';

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
  }
];
