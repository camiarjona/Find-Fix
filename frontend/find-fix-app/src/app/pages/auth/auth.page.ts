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

  private authService = inject(AuthService);
  private router = inject(Router);

  public isLoginView = signal<boolean>(true);
  public authError = signal<string | null>(null);

  showLoginView(): void {
    this.isLoginView.set(true);
    this.authError.set(null);
  }

  showRegisterView(): void {
    this.isLoginView.set(false);
    this.authError.set(null);
  }

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
            this.router.navigateByUrl('/cliente/dashboard');
          }
        },
        error: (err) => {
          this.authError.set('Email o contraseña incorrectos.');
        }
      });
  }

  onRegister(credentials: RegisterCredentials): void {
    this.authError.set(null);
    this.authService.register(credentials).subscribe();
    console.log('Datos de registro:', credentials);
    alert('¡Registro exitoso!. Ahora inicia sesión.');
    this.showLoginView();
  }

  onToggleView(): void {
    this.isLoginView.set(!this.isLoginView());
    this.authError.set(null);
  }
}
