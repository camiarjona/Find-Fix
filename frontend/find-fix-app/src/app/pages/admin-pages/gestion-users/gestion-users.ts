import { Component, inject, OnInit, signal } from '@angular/core';
import { UserService } from '../../../services/user/user.service';
import { UserProfile } from '../../../models/user/user.model';
import { CommonModule } from '@angular/common';
import { ModalDetalleUsuario } from '../../../components/admin-components/modal-detalle-usuario/modal-detalle-usuario';

@Component({
  selector: 'app-gestion-users',
  imports: [CommonModule, ModalDetalleUsuario],
  templateUrl: './gestion-users.html',
  styleUrl: './gestion-users.css',
})
export class GestionUsers implements OnInit{

  private userService = inject(UserService);

  public usuarios = signal<UserProfile[]>([]);
  public isLoading = signal(true);

  public usuarioSeleccionado = signal<UserProfile | null>(null);

  ngOnInit(): void {
    this.cargarUsuarios();
  }

  cargarUsuarios() {
    this.isLoading.set(true);
    this.userService.getUsers(true).subscribe({
      next: (res) => {
        const listaUsuarios = 'data' in res ? res.data : res;
        this.usuarios.set(listaUsuarios);
        this.isLoading.set(false);
      },
      error: (err) => {
        console.error('Error cargando usuarios:', err);
        this.isLoading.set(false);
      }
    });
  }

  confirmarDesactivacion(user: UserProfile) {
    if (!user.activo) return;

    const confirmar = confirm(`¿Estás seguro de que deseas desactivar a ${user.nombre}? El usuario perderá acceso al sistema.`);

    if (confirmar) {
      this.userService.desactivarUsuario(user.email).subscribe({
        next: () => {
          alert('Usuario desactivado correctamente.');
          this.usuarios.update(lista =>
            lista.map(u => u.email === user.email ? { ...u, activo: false } : u)
          );
        },
        error: (err) => alert('Ocurrió un error al desactivar.')
      });
    }
  }

  // Abre el modal con el usuario clickeado
  verDetalle(user: UserProfile) {
    this.usuarioSeleccionado.set(user);
  }

  // Cierra el modal
  cerrarDetalle() {
    this.usuarioSeleccionado.set(null);
  }

  onUsuarioEditado() {
    this.cerrarDetalle();
    this.cargarUsuarios();
  }

  toggleEstadoUsuario(user: UserProfile, event: Event){
    const checkbox = event.target as HTMLInputElement;
    const nuevoEstado = checkbox.checked;

    const accion = nuevoEstado ? this.userService.activarUsuario(user.email) : this.userService.desactivarUsuario(user.email);

    accion.subscribe(
      {
        next: () => {
          this.usuarios.update(lista =>
            lista.map(u => u.email === user.email ? {...u, activo: nuevoEstado} : u)
          )
        },
        error: (err) => {
        checkbox.checked = !nuevoEstado;
        alert('Error al cambiar el estado del usuario');
        console.error(err);
      }
      }
    )
  }
}
