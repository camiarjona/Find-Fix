import { Component, inject, OnInit, signal } from '@angular/core';
import { UserService } from '../../../services/user/user.service';
import { UserProfile, UserSearchFilters } from '../../../models/user/user.model';
import { CommonModule } from '@angular/common';
import { ModalDetalleUsuario } from '../../../components/admin-components/modal-detalle-usuario/modal-detalle-usuario';
import { FormsModule } from '@angular/forms';
import { UI_ICONS } from '../../../models/general/ui-icons';
import { DireccionOrden } from '../../../models/enums/enums.model';
import { ordenarDinamicamente } from '../../../utils/sort-utils';

@Component({
  selector: 'app-gestion-users',
  imports: [CommonModule, ModalDetalleUsuario, FormsModule],
  templateUrl: './gestion-users.html',
  styleUrl: './gestion-users.css',
})

export class GestionUsers implements OnInit{


  private userService = inject(UserService);

  public usuarios = signal<UserProfile[]>([]);
  public isLoading = signal(true);

  public icons = UI_ICONS;

  public usuarioSeleccionado = signal<UserProfile | null>(null);

  public filtros: UserSearchFilters = {
    email: '',
    rol: ''
  };

  // Variables para el ordenamiento
  criterioOrden = 'email'; // Criterio inicial
  direccionOrden: DireccionOrden = 'asc';
  dropdownOpen: string | null = null;

  ngOnInit(): void {
    this.cargarUsuarios();
  }

  /// señales para paginacion
  public currentPage = signal(0);
  public totalPages = signal(0);
  public pageSize = 10;


  cargarUsuarios() {
  this.isLoading.set(true);


  this.userService.obtenerUsuarios(this.filtros.rol, this.currentPage(), this.pageSize).subscribe({
    next: (res) => {
      let listaUsuarios = res.content;

      const usuariosOrdenados = ordenarDinamicamente(
          listaUsuarios,
          this.criterioOrden,
          this.direccionOrden);

        this.usuarios.set(usuariosOrdenados);
        this.totalPages.set(res.totalPages);
        this.isLoading.set(false);
    },
    error: (err) => {
      console.error('Error al cargar usuarios:', err);
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

  cambiarPagina(delta: number) {
  this.currentPage.update(p => p + delta);
  this.cargarUsuarios();
}

  aplicarFiltros() {
    this.currentPage.set(0);
    this.cargarUsuarios();
  }

limpiarFiltros() {
    this.filtros = { email: '', rol: '' };
    this.criterioOrden = 'email';
    this.direccionOrden = 'asc';
    this.currentPage.set(0);
    this.cargarUsuarios();
  }

  toggleDropdown(menu: string, event: Event) {
    event.stopPropagation();
    this.dropdownOpen = this.dropdownOpen === menu ? null : menu;
  }

  seleccionarOrden(criterio: string) {
    this.criterioOrden = criterio;
    this.direccionOrden = criterio === 'activo' ? 'desc' : 'asc';

    this.dropdownOpen = null;
    this.cargarUsuarios();
  }
}

