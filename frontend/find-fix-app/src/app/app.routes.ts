import { Routes } from '@angular/router';
import { AuthPage } from './pages/auth/auth.page';
import { Home } from './pages/home/home';

export const routes: Routes = [
  {
    path: '',
    component: Home
  },
  {
    path: 'auth',
    component: AuthPage
  }
];
