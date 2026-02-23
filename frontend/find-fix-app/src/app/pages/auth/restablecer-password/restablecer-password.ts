import { Component, inject, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { AuthService } from '../../../services/auth/auth.service';
import { UI_ICONS } from '../../../models/general/ui-icons';


@Component({
  selector: 'app-restablecer-password',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './restablecer-password.html',
  styleUrls: ['./restablecer-password.css']
})
export class RestablecerPassword implements OnInit {
  private fb = inject(FormBuilder);
  private authService = inject(AuthService);
  private route = inject(ActivatedRoute);
  private router = inject(Router);

  public token = signal<string | null>(null);
  public enviando = signal(false);
  public mensajeExito = signal<string | null>(null);
  public mensajeError = signal<string | null>(null);

  public showPassword = signal(false);
  public icons = UI_ICONS;

  public resetForm = this.fb.nonNullable.group({
    nuevaPassword: ['', [Validators.required, Validators.minLength(6)]]
  });

  ngOnInit(): void {
    const tokenUrl = this.route.snapshot.queryParamMap.get('token');

    if (tokenUrl) {
      this.token.set(tokenUrl);
    } else {
      this.router.navigate(['/auth']);
    }
  }

  togglePassword() {
    this.showPassword.update(val => !val);
  }

  onSubmit() {
    if (this.resetForm.invalid || !this.token()) {
      this.resetForm.markAllAsTouched();
      return;
    }

    this.enviando.set(true);
    this.mensajeError.set(null);

    const nuevaPassword = this.resetForm.getRawValue().nuevaPassword;

    this.authService.restablecerPassword(this.token()!, nuevaPassword).subscribe({
      next: (respuesta) => {
        this.enviando.set(false);
        this.mensajeExito.set(respuesta || 'Contraseña actualizada exitosamente.');
      },
      error: (err) => {
        this.enviando.set(false);
        this.mensajeError.set(err.error || 'El enlace es inválido o ha expirado.');
      }
    });
  }

  irAlLogin() {
    this.router.navigate(['/auth']);
  }
}
