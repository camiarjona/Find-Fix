import { CommonModule } from '@angular/common';
import { Component, inject, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { AuthService } from '../../services/auth/auth.service';

@Component({
  selector: 'app-login',
  imports: [CommonModule, RouterLink, ReactiveFormsModule],
  templateUrl: './login.html',
  styleUrl: './login.css',
})
export class LoginPage {

  private fb = inject(FormBuilder);
  private authService = inject(AuthService);
  private router = inject(Router);

  public loginError = signal<string | null>(null);

  public loginForm = this.fb.nonNullable.group({
    email: ['', [Validators.required, Validators.email]],
    password: ['', [Validators.required]]
  })

  onSubmit(): void {

    this.loginError.set(null);

    if (this.loginForm.invalid) {
      this.loginForm.markAllAsTouched();
      return;
    }

    this.authService.login(this.loginForm.value as any)
      .subscribe({
        next: () => {
          const user = this.authService.currentUser();

          if (user?.roles.includes('ADMIN')) {
            this.authService.setInitialRole('admin');
            this.router.navigateByUrl('');

          } else if (user?.roles.includes('CLIENTE') && user?.roles.includes('ESPECIALISTA')) {
            this.router.navigateByUrl('/seleccionar-rol');

          } else {
            this.authService.setInitialRole('cliente');
            this.router.navigateByUrl('/app/dashboard');
          }
        },
        error: (err) => {
          console.error(err);
          this.loginError.set('Email o contraseña incorrectos.');
        }
      });
  }
}
