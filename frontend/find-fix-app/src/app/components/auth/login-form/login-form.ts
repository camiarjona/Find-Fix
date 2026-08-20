import { CommonModule } from '@angular/common';
import { Component, EventEmitter, inject, Input, Output, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { LoginCredentials } from '../../../models/user/user.model';
import { UI_ICONS } from '../../../models/general/ui-icons';
import { GoogleLoginButton } from '../../utils/google-login-button/google-login-button';
import { RouterModule } from '@angular/router';
import { AuthService } from '../../../services/auth/auth.service';

@Component({
  selector: 'app-login-form',
  imports: [CommonModule, ReactiveFormsModule, GoogleLoginButton, RouterModule],
  templateUrl: './login-form.html',
  styleUrl: './login-form.css',
})
export class LoginForm {

  private fb = inject(FormBuilder);
  private authService = inject(AuthService);

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

  togglePassword() {
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
          mensaje: err.error || 'Ocurrió un error al intentar reenviar el enlace.',
          tipo: 'error'
        });
      }
    });
  }

}
