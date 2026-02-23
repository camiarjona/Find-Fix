import { Component, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { AuthService } from '../../../services/auth/auth.service';

@Component({
  selector: 'app-solicitar-recuperacion',
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './solicitar-recuperacion.html',
  styleUrls: ['./solicitar-recuperacion.css']
})
export class SolicitarRecuperacion {
  private fb = inject(FormBuilder);
  private authService = inject(AuthService);
  private router = inject(Router);

  public enviando = signal(false);
  public mensajeExito = signal<string | null>(null);
  public mensajeError = signal<string | null>(null);

  public recuperacionForm = this.fb.nonNullable.group({
    email: ['', [Validators.required, Validators.email]]
  });

  onSubmit() {
    if (this.recuperacionForm.invalid) {
      this.recuperacionForm.markAllAsTouched();
      return;
    }

    this.enviando.set(true);
    this.mensajeExito.set(null);
    this.mensajeError.set(null);

    const email = this.recuperacionForm.getRawValue().email;

    this.authService.solicitarRecuperacionPassword(email).subscribe({
      next: (respuesta) => {
        this.enviando.set(false);
        this.mensajeExito.set(respuesta || 'Te enviamos un correo con las instrucciones.');
        this.recuperacionForm.reset();
      },
      error: (err) => {
        this.enviando.set(false);
        this.mensajeError.set(err.error || 'Ocurrió un error al intentar enviar el correo.');
      }
    });
  }

  volverAlLogin() {
    this.router.navigate(['/auth']);
  }
}
