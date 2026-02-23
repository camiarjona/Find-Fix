import { Component, inject, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, Router } from '@angular/router';
import { AuthService } from '../../services/auth/auth.service';
import { LoginCredentials, RegisterCredentials } from '../../models/user/user.model';
import { RegisterForm } from "../../components/auth/register-form/register-form";
import { LoginForm } from "../../components/auth/login-form/login-form";
import { ModalFeedbackComponent } from "../../components/general/modal-feedback.component/modal-feedback.component";

@Component({
  selector: 'app-auth-page',
  standalone: true,
  imports: [
    CommonModule,
    RegisterForm,
    LoginForm,
    ModalFeedbackComponent
  ],
  templateUrl: './auth.page.html',
  styleUrl: './auth.page.css'
})
export class AuthPage implements OnInit {

  private authService = inject(AuthService);
  private router = inject(Router);
  private route = inject(ActivatedRoute);

  // Feedback modal state
public feedbackData = signal({
    visible: false,
    tipo: 'success' as 'success' | 'error',
    titulo: '',
    mensaje: ''
  });
  mostrarFeedback(titulo: string, mensaje: string, tipo: 'success' | 'error' = 'success') {
    this.feedbackData.set({ visible: true, titulo, mensaje, tipo });
  }

  cerrarFeedback() {
    this.feedbackData.update(current => ({ ...current, visible: false }));
  }

  public isLoginView = signal<boolean>(true);
  public authError = signal<string | null>(null);

  ngOnInit() {
    this.route.queryParams.subscribe(params => {
      const mode = params['mode'];
      if (mode === 'register') {
        this.showRegisterView();
      } else {
        this.showLoginView();
      }
    })
  }

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

    this.authService.register(credentials).subscribe({
      next: (response) => {
        console.log('Datos de registro:', credentials);

        this.mostrarFeedback('¡Casi listo!', 'Te estamos redirigiendo...', 'success');

        this.router.navigate(['/revisa-tu-correo'], {
          queryParams: { email: credentials.email }
        });
      },
      error: (err) => {
        console.error('Error en el registro:', err);

        const mensajeError = err.error?.message || 'El email ingresado ya se encuentra registrado.';

        this.mostrarFeedback('Error de Registro', mensajeError, 'error');
      }
    });
  }

  onToggleView(): void {
    this.isLoginView.set(!this.isLoginView());
    this.authError.set(null);
  }
}
