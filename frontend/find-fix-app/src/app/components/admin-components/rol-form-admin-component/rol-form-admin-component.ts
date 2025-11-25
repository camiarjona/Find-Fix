import { Component, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { HttpErrorResponse } from '@angular/common/http';
import { RolesService } from '../../../services/admin-services/roles-service';

@Component({
  selector: 'app-rol-form-admin-component',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './rol-form-admin-component.html',
  styleUrl: './rol-form-admin-component.css',
})
export class RolFormAdminComponent {
  private rolesService = inject(RolesService);

  public rolName: string = '';
  public isLoading = signal(false);
  public errorMessage = signal<string | null>(null);

  submitRol(): void {
    if (!this.rolName.trim()) return;

    this.isLoading.set(true);
    this.errorMessage.set(null);

    this.rolesService.addRol(this.rolName).subscribe({
      next: () => {
        this.isLoading.set(false);
        this.rolName = '';
      },
      error: (err: HttpErrorResponse) => {
        this.isLoading.set(false);
        this.errorMessage.set(err.error?.mensaje || 'No se pudo crear el rol.');
      }
    });
  }

  cancel(): void {
    this.rolName = '';
    this.errorMessage.set(null);
    this.rolesService.formStatus.set('hidden');
  }
}
