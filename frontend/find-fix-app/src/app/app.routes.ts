import { Routes } from '@angular/router';
import { AuthPage } from './pages/auth/auth.page';
import { Home } from './pages/home/home.page';
import { SeleccionarRolPage } from './pages/seleccionar-rol/seleccionar-rol.page';

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
  }
];
