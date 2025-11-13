import { Component, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { AuthService } from '../../services/auth/auth.service';
import { LoginCredentials, RegisterCredentials } from '../../models/user/user.model';
import { RegisterForm } from "../../components/auth/register-form/register-form";
import { LoginForm } from "../../components/auth/login-form/login-form";

@Component({
  selector: 'app-auth-page',
  standalone: true,
  imports: [
    CommonModule,
    RegisterForm,
    LoginForm
],
  templateUrl: './auth.page.html',
  styleUrl: './auth.page.css'
})
export class AuthPage {

  // --- Inyecciones ---
  private authService = inject(AuthService);
  private router = inject(Router);

  // --- Estado de la Página ---
  public isLoginView = signal<boolean>(true);
  public authError = signal<string | null>(null);

  // --- Métodos de Transición ---

  showLoginView(): void {
    this.isLoginView.set(true);
    this.authError.set(null);
  }

  showRegisterView(): void {
    this.isLoginView.set(false);
    this.authError.set(null);
  }

  // --- Métodos de Lógica (los "oyentes" de los @Output) ---
  onLogin(credentials: LoginCredentials): void {
    this.authError.set(null);

    this.authService.login(credentials)
      .subscribe({
        next: () => {
          const user = this.authService.currentUser();
          if (user?.roles.includes('ADMIN')) {
            this.authService.setInitialRole('admin');
            this.router.navigateByUrl('/admin/dashboard');

          } else if (user?.roles.includes('CLIENTE') && user?.roles.includes('ESPECIALISTA')) {
            this.router.navigateByUrl('/seleccionar-rol');

          } else {
            this.authService.setInitialRole('cliente');
            this.router.navigateByUrl('/app/dashboard');
          }
        },
        error: (err) => {
          this.authError.set('Email o contraseña incorrectos.');
        }
      });
  }

  onRegister(credentials: RegisterCredentials): void {
    this.authError.set(null);

    // --- ¡AQUÍ IRÁ TU LÓGICA DE REGISTRO! ---
    // 1. Llama a un método 'authService.register(credentials)' (que aún no existe).
    // 2. En el 'next:', muéstrale un mensaje de éxito y pon 'this.isLoginView.set(true)'
    // 3. En el 'error:', usa 'this.authError.set(err.message)'

    console.log('Datos de registro:', credentials);
    alert('¡Registro exitoso! (Aún no conectado al backend). Ahora inicia sesión.');
    this.showLoginView();
  }
}
