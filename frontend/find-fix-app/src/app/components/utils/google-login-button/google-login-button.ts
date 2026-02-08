import { Router } from '@angular/router';
import { AuthService } from './../../../services/auth/auth.service';
import { AfterViewInit, Component, inject, NgZone } from '@angular/core';
import { environment } from '../../../environments/environment';

declare var google: any;

@Component({
  selector: 'app-google-login-button',
  imports: [],
  templateUrl: './google-login-button.html',
  styleUrl: './google-login-button.css',
})
export class GoogleLoginButton implements AfterViewInit {

  private AuthService = inject(AuthService);
  private router = inject(Router);
  private ngZone = inject(NgZone);

  ngAfterViewInit(): void {
    if (typeof google === 'undefined') {
      console.error('Google API not loaded');
      return;
    }

    console.log('🔍 REVISIÓN DE CREDENCIALES:');
    console.log('👉 ID configurado:', environment.googleClientId);
    console.log('👉 Origen actual:', window.location.origin);

    google.accounts.id.initialize({
      client_id: environment.googleClientId,
      callback: (response: any) => this.handleGoogleCredential(response),
    });

    const btnContainer = document.getElementById('google-btn');

    if (btnContainer) {
      google.accounts.id.renderButton(btnContainer, {
        theme: 'outline',
        size: 'large',
        width: '350',
        text: 'continue_with',
        shape: 'rectangular',
        logo_alignment: 'left',
      });
    }
  }

  handleGoogleCredential(response: any) {
    this.ngZone.run(() => {
      if (response.credential) {
        console.log('Token de google recibido');

        this.AuthService.loginWithGoogle(response.credential).subscribe({
          next: (res) => {
            console.log('Login exitoso con Google');

            const user = this.AuthService.currentUser();

            if (user) {
              this.redirigirSegunRol(user);
            }
          },
          error: (err) => {
            console.error('Error en login con Google:', err);
            alert('Error al iniciar sesión con Google. Por favor, inténtalo de nuevo.');
          }
        });
      }
    });
  }


  private redirigirSegunRol(user: any) {
    if (user.roles.includes('ADMIN')) {
      this.router.navigateByUrl('/admin/dashboard');
    } else if (user.roles.includes('CLIENTE') && user.roles.includes('ESPECIALISTA')) {
      this.router.navigateByUrl('/seleccionar-rol');
    } else {
      this.router.navigateByUrl('/cliente/dashboard');
    }
  }
}
