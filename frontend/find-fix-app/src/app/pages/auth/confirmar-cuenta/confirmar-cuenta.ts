import { Component, inject, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, Router } from '@angular/router';
import { AuthService } from '../../../services/auth/auth.service';

@Component({
  selector: 'app-confirmar-cuenta',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './confirmar-cuenta.html',
  styleUrls: ['./confirmar-cuenta.css']
})
export class ConfirmarCuenta implements OnInit {
  private route = inject(ActivatedRoute);
  private router = inject(Router);
  private authService = inject(AuthService);

  public estado = signal<'cargando' | 'exito' | 'error'>('cargando');
  public mensaje = signal<string>('Verificando tu cuenta de FindFix...');

  ngOnInit(): void {
    const token = this.route.snapshot.queryParamMap.get('token');

    if (!token) {
      this.router.navigate(['/auth']);
      return;
    }

    this.authService.confirmarCuenta(token).subscribe({
      next: (respuestaServidor) => {
        this.estado.set('exito');
        this.mensaje.set(respuestaServidor || '¡Tu cuenta ha sido activada exitosamente!');
      },
      error: (err) => {
        this.estado.set('error');
        this.mensaje.set(err.error || 'El enlace de confirmación es inválido o ha expirado.');
      }
    });
  }

  irAlLogin() {
    this.router.navigate(['/auth']);
  }
}
