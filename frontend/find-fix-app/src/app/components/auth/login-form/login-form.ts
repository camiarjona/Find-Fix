import { CommonModule } from '@angular/common';
import { Component, EventEmitter, inject, Input, Output, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { LoginCredentials } from '../../../models/user/user.model';
import { UI_ICONS } from '../../../models/general/ui-icons';
import { GoogleLoginButton } from '../../utils/google-login-button/google-login-button';

@Component({
  selector: 'app-login-form',
  imports: [CommonModule, ReactiveFormsModule, GoogleLoginButton],
  templateUrl: './login-form.html',
  styleUrl: './login-form.css',
})
export class LoginForm {

  private fb = inject(FormBuilder);

  public showPassword = signal(false);

  @Input() loginError: string | null = null;

  @Output() loginSubmit = new EventEmitter<LoginCredentials>();

  @Output() toggleView = new EventEmitter<void>();

  public icons = UI_ICONS;

  public loginForm = this.fb.nonNullable.group({
    email: ['', [Validators.required, Validators.email]],
    password: ['', [Validators.required]]
  });

  onSubmit(): void {
    if (this.loginForm.invalid) {
      this.loginForm.markAllAsTouched();
      return;
    }

    this.loginSubmit.emit(this.loginForm.getRawValue());
  }

  onToggle(): void {
    this.toggleView.emit();
  }

  togglePassword() {
    this.showPassword.update(val => !val);
  }
}
