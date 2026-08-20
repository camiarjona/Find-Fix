import { CommonModule } from '@angular/common';
import { Component, EventEmitter, inject, Input, Output, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { LoginCredentials } from '../../../models/user/user.model';
import { UI_ICONS } from '../../../models/general/ui-icons';
import { GoogleLoginButton } from '../../utils/google-login-button/google-login-button';
import { Router, RouterModule } from '@angular/router';
import { AuthService } from '../../../services/auth/auth.service';

@Component({
  selector: 'app-login-form',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, GoogleLoginButton, RouterModule],
  templateUrl: './login-form.html',
  styleUrl: './login-form.css',
})
export class LoginForm {

  private fb = inject(FormBuilder);
  private authService = inject(AuthService);
  private router = inject(Router);

  public showPassword = signal(false);
  public cargandoReenvio = signal(false);

  @Input() loginError: string | null = null;
  @Input() cuentaInactiva: boolean = false;

  @Output() loginSubmit = new EventEmitter<LoginCredentials>();
  @Output() toggleView = new EventEmitter<void>();
  @Output() showFeedback = new EventEmitter<{ titulo: string; mensaje: string; tipo: 'success' | 'error' }>();

  public icons = UI_ICONS;

  public loginForm = this.fb.nonNullable.group({
    email: ['', [Validators.required, Validators.email]],
    password: ['', [Validators.required]]
  });

  onSubmit(): void {
    if (this.loginForm.invalid) {
      this.loginForm.markAllAsTouched();
      return;
    }
    this.loginSubmit.emit(this.loginForm.getRawValue());
  }

  onToggle(): void {
    this.toggleView.emit();
  }

  togglePassword(): void {
    this.showPassword.update(val => !val);
  }

  solicitarReenvio(): void {
    const email = this.loginForm.get('email')?.value;
    if (!email) return;

    this.cargandoReenvio.set(true);

    this.authService.reenviarTokenConfirmacion(email).subscribe({
      next: (mensaje) => {
        this.cargandoReenvio.set(false);
        this.showFeedback.emit({
          titulo: '¡Correo enviado!',
          mensaje: mensaje || 'Revisá tu casilla de correo para activar la cuenta.',
          tipo: 'success'
        });
      },
      error: (err) => {
        this.cargandoReenvio.set(false);
        this.showFeedback.emit({
          titulo: 'Error al reenviar',
          mensaje: err.error?.message || 'Ocurrió un error al intentar reenviar el enlace.',
          tipo: 'error'
        });
      }
    });
  }

  onGoogleLogin(event: any): void {
    const credential = typeof event === 'string' ? event : event?.credential || event?.token;
    if (!credential) return;

    const googleEmail = this.extraerEmailDeGoogleToken(credential);

    this.authService.loginWithGoogle(credential).subscribe({
      next: () => {
        const user = this.authService.currentUser();
        if (user?.roles.includes('ADMIN')) {
          this.authService.setInitialRole('admin');
          this.router.navigateByUrl('/admin/dashboard');
        } else if (user?.roles.includes('CLIENTE') && user?.roles.includes('ESPECIALISTA')) {
          this.router.navigateByUrl('/seleccionar-rol');
        } else {
          this.authService.setInitialRole('cliente');
          this.router.navigateByUrl('/cliente/dashboard');
        }
      },
      error: (err) => {
        const status = err.status || err.error?.status;
        const mensajeBackend = err.error?.message || (typeof err.error === 'string' ? err.error : '');

        // CASO 1: Registrada pero no verificada (HTTP 403)
        if (status === 403) {
          if (googleEmail) {
            this.loginForm.patchValue({ email: googleEmail });
          }
          this.cuentaInactiva = true;
          this.loginError = 'Tu cuenta aún no ha sido verificada. Revisa tu casilla de correo.';

          this.showFeedback.emit({
            titulo: 'Cuenta no verificada',
            mensaje: mensajeBackend || 'Tu cuenta de Google existe pero no está activada. Te cargamos el email en el formulario para que solicites el reenvío.',
            tipo: 'error'
          });
        }
        // CASO 2: No registrada (HTTP 404)
        else if (status === 404) {
          this.showFeedback.emit({
            titulo: 'Usuario no registrado',
            mensaje: mensajeBackend || 'No encontramos una cuenta asociada a este correo de Google. Por favor, regístrate primero.',
            tipo: 'error'
          });
        }
        // CASO 3: Errores imprevistos
        else {
          this.showFeedback.emit({
            titulo: 'Error de inicio de sesión',
            mensaje: mensajeBackend || 'No se pudo iniciar sesión con Google en este momento.',
            tipo: 'error'
          });
        }
      }
    });
  }

  private extraerEmailDeGoogleToken(credential: string): string | null {
    try {
      const base64Url = credential.split('.')[1];
      const base64 = base64Url.replace(/-/g, '+').replace(/_/g, '/');
      const jsonPayload = decodeURIComponent(
        atob(base64)
          .split('')
          .map(c => '%' + ('00' + c.charCodeAt(0).toString(16)).slice(-2))
          .join('')
      );
      return JSON.parse(jsonPayload).email || null;
    } catch {
      return null;
    }
  }

}
