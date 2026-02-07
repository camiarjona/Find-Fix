import { CommonModule } from '@angular/common';
import { Component, EventEmitter, inject, Input, OnInit, Output, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { RegisterCredentials } from '../../../models/user/user.model';
import { UI_ICONS } from '../../../models/general/ui-icons';
import { HttpClient } from '@angular/common/http';

interface Barrio {
  nombre: string;
  lat: number;
  lon: number;
}

@Component({
  selector: 'app-register-form',
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './register-form.html',
  styleUrl: './register-form.css',
})
export class RegisterForm implements OnInit {

  private fb = inject(FormBuilder);
  private http = inject(HttpClient);

  public showPassword = signal(false);

  public allBarrios: Barrio[] = [];
  public resultadosBusqueda = signal<Barrio[]>([]);
  public mostrandoResultados = signal(false);

  private latitudSeleccionada: number | null = null;
  private longitudSeleccionada: number | null = null;

  @Input() registerError: string | null = null;

  @Output() registerSubmit = new EventEmitter<RegisterCredentials>();
  @Output() toggleView = new EventEmitter<void>();

  public icons = UI_ICONS;

  public registerForm = this.fb.nonNullable.group({
    nombre: ['', [Validators.required]],
    apellido: ['', [Validators.required]],
    email: ['', [Validators.required, Validators.email]],
    password: ['', [Validators.required, Validators.minLength(6)]],
    ciudad: ['', [Validators.required]]
  });

  ngOnInit() {
    this.cargarBarriosDelBackend();
    this.setupBuscador();
  }

  cargarBarriosDelBackend() {
    this.http.get<Barrio[]>('http://localhost:8080/api/barrios?ciudad=mdp')
      .subscribe({
        next: (data) => {
          this.allBarrios = data;
          console.log('Barrios cargados en registro:', data.length);
        },
        error: (err) => console.error('Error cargando barrios (Revisa que el Backend esté corriendo)', err)
      });
  }

  setupBuscador() {
    this.registerForm.controls.ciudad.valueChanges.subscribe(valor => {
      const termino = valor.toLowerCase();

      if (termino.length < 1) {
        this.resultadosBusqueda.set([]);
        this.mostrandoResultados.set(false);
        return;
      }

      const filtrados = this.allBarrios
        .filter(b => b.nombre.toLowerCase().includes(termino))
        .slice(0, 5);

      if (filtrados.length > 0) {
        this.resultadosBusqueda.set(filtrados);
        this.mostrandoResultados.set(true);
      } else {
        this.resultadosBusqueda.set([]);
        this.mostrandoResultados.set(false);
      }
    });
  }

  seleccionarLocalidad(barrio: Barrio) {
    this.registerForm.controls.ciudad.setValue(barrio.nombre, { emitEvent: false });
    this.latitudSeleccionada = barrio.lat;
    this.longitudSeleccionada = barrio.lon;

    this.mostrandoResultados.set(false);
    this.resultadosBusqueda.set([]);
  }

  onSubmit(): void {
    if (this.registerForm.invalid) {
      this.registerForm.markAllAsTouched();
      return;
    }

    const datosRegistro: RegisterCredentials = {
      ...this.registerForm.getRawValue(),
      latitud: this.latitudSeleccionada || undefined,
      longitud: this.longitudSeleccionada || undefined
    };

    console.log('Enviando registro con coordenadas:', datosRegistro);
    this.registerSubmit.emit(datosRegistro);
  }

  onToggle(): void {
    this.toggleView.emit();
  }

  togglePassword() {
    this.showPassword.update(val => !val);
  }
}
