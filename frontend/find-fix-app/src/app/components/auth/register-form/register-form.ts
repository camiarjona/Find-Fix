import { CommonModule } from '@angular/common';
import { Component, EventEmitter, inject, Input, Output, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { RegisterCredentials } from '../../../models/user/user.model';
import { UI_ICONS } from '../../../models/general/ui-icons';

@Component({
  selector: 'app-register-form',
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './register-form.html',
  styleUrl: './register-form.css',
})
export class RegisterForm {

  private fb = inject(FormBuilder);

  public showPassword = signal(false);

  @Input() registerError: string | null = null;

  @Output() registerSubmit = new EventEmitter<RegisterCredentials>();
  @Output() toggleView = new EventEmitter<void>();

  public icons = UI_ICONS;

  public registerForm = this.fb.nonNullable.group({
    nombre: ['', [Validators.required]],
    apellido: ['', [Validators.required]],
    email: ['', [Validators.required, Validators.email]],
    password: ['', [Validators.required, Validators.minLength(6)]]
  });

  onSubmit(): void {
    if (this.registerForm.invalid) {
      this.registerForm.markAllAsTouched();
      return;
    }

    this.registerSubmit.emit(this.registerForm.getRawValue());
  }

  onToggle(): void {
    this.toggleView.emit(); // "Toca el timbre"
  }

  togglePassword() {
    this.showPassword.update(val => !val);
  }
}
