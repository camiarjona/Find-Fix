import { Component, inject } from '@angular/core';
import { AuthService } from '../../services/auth/auth.service';
import { Router } from '@angular/router';

@Component({
  selector: 'app-seleccionar-rol',
  imports: [],
  templateUrl: './seleccionar-rol.page.html',
  styleUrl: './seleccionar-rol.page.css',
})
export class SeleccionarRolPage {

  public authService = inject(AuthService);
  private router = inject(Router);

  constructor() {
    if(!this.authService.isLoggedIn()) {
      this.router.navigateByUrl('/auth')
    }
  }

  seleccionar(rol: 'cliente' | 'especialista'){
    this.authService.setInitialRole(rol);

    if(rol === 'cliente') {
      this.router.navigateByUrl('/cliente/dashboard');
      return;
    } else {
      this.router.navigateByUrl('/especialista/dashboard');
      return;
    }
  }
}
