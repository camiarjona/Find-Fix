import { CommonModule } from '@angular/common';
import { UpdateUserRequest, UserProfile } from '../../../models/user/user.model';
import { UserService } from './../../../services/user/user.service';
import { Component, EventEmitter, inject, Input, Output, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-modal-mi-perfil',
  imports: [CommonModule, FormsModule],
  templateUrl: './modal-mi-perfil.html',
  styleUrl: './modal-mi-perfil.css',
})
export class ModalMiPerfil {

  private userService = inject(UserService);

  // recibimos el user desde el padre (ventana anterior)
  @Input({required: true}) usuario!: UserProfile;

  @Output() cerrar = new EventEmitter<void>();
  @Output() perfilActualizado = new EventEmitter<UserProfile>();

  //variable para ver que campo se esta editando
  public editingField = signal<string | null>(null);

  public availableCities = signal<string[]>([]);

  // Variable temporal para guardar lo que escribes en el input
  public tempValue = '';
  public isLoading = signal(false);

  ngOnInit() {
    this.loadCities();
  }

  loadCities() {
    this.userService.getCities().subscribe({
      next: (res) => {
        this.availableCities.set(res.data); // Guardamos la lista: ["Mar del Plata", "Batán", etc.]
      },
      error: (err) => console.error('Error al cargar ciudades', err)
    });
  }

  // Inicia la edición de un campo específico
  startEdit(field: string, currentValue: string | undefined) {
    this.editingField.set(field);
    this.tempValue = currentValue || '';
  }

  // Cancela la edición
  cancelEdit() {
    this.editingField.set(null);
    this.tempValue = '';
  }

  // Guarda el cambio de ese campo específico
  saveEdit(field: string) {
    if (!this.tempValue.trim()) return; // Validación básica

    this.isLoading.set(true);

    // Construimos el objeto DTO solo con el campo que cambió (Patch)
    const updateData: UpdateUserRequest = {
      [field]: this.tempValue
    };

    this.userService.updateProfile(updateData).subscribe({
      next: (res) => {
        // Actualizamos el dato localmente para verlo reflejado al instante
        (this.usuario as any)[field] = this.tempValue;

        this.isLoading.set(false);
        this.editingField.set(null); // Salimos del modo edición
        this.perfilActualizado.emit(); // Avisamos por si acaso
        alert('Dato actualizado correctamente ✅');
      },
      error: (err) => {
        console.error(err);
        alert(err.error?.mensaje || 'Error al actualizar');
        this.isLoading.set(false);
      }
    });
  }

}
