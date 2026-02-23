import { Component, inject, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, Router } from '@angular/router';
import { AuthService } from '../../../services/auth/auth.service';

@Component({
  selector: 'app-revisa-tu-correo',
  imports: [CommonModule],
  templateUrl: './revisa-tu-correo.html',
  styleUrls: ['./revisa-tu-correo.css']
})
export class RevisaTuCorreo implements OnInit {
  private route = inject(ActivatedRoute);
  private router = inject(Router);
  private authService = inject(AuthService);

  public email = signal<string>('');
  public enviando = signal<boolean>(false);
  public mensajeExito = signal<string | null>(null);
  public mensajeError = signal<string | null>(null);

  ngOnInit(): void {
    this.route.queryParams.subscribe(params => {
      if (params['email']) {
        this.email.set(params['email']);
      } else {
        this.router.navigate(['/auth']);
      }
    });
  }

  reenviarCorreo() {
    if (!this.email()) return;

    this.enviando.set(true);
    this.mensajeExito.set(null);
    this.mensajeError.set(null);

    this.authService.reenviarCorreoActivacion(this.email()).subscribe({
      next: (respuestaServidor) => {
        this.enviando.set(false);
        this.mensajeExito.set('¡Te hemos enviado un nuevo enlace! Revisa tu bandeja de entrada o Spam.');
      },
      error: (err) => {
        this.enviando.set(false);
        this.mensajeError.set(err.error || 'Ocurrió un error al intentar reenviar el correo.');
      }
    });
  }

  irAlLogin() {
    this.router.navigate(['/auth']);
  }
}
