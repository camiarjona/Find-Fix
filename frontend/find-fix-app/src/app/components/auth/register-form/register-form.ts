import { CommonModule } from '@angular/common';
import { Component, EventEmitter, inject, Input, Output } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { RegisterCredentials } from '../../../models/user/user.model';

@Component({
  selector: 'app-register-form',
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './register-form.html',
  styleUrl: './register-form.css',
})
export class RegisterForm {

  private fb = inject(FormBuilder);

  @Input() registerError: string | null = null;

  @Output() registerSubmit = new EventEmitter<RegisterCredentials>();
  @Output() toggleView = new EventEmitter<void>();
  

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
}
