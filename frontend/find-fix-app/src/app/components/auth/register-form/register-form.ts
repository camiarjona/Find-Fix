import { CommonModule } from '@angular/common';
import { Component, EventEmitter, inject, Input, OnInit, Output, signal, ChangeDetectorRef, HostListener } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { RegisterCredentials } from '../../../models/user/user.model';
import { UI_ICONS } from '../../../models/general/ui-icons';
import { HttpClient } from '@angular/common/http';
import { LocationService } from '../../../services/general/location.service';

interface Barrio {
  nombre: string;
  lat: number;
  lon: number;
}

@Component({
  selector: 'app-register-form',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './register-form.html',
  styleUrl: './register-form.css',
})
export class RegisterForm implements OnInit {
  private fb = inject(FormBuilder);
  private http = inject(HttpClient);
  private cdr = inject(ChangeDetectorRef);
  private locationService = inject(LocationService);

  public showPassword = signal(false);
  public cargandoGps = signal(false);
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
  }

  @HostListener('document:mousedown', ['$event'])
  onDocumentClick(event: MouseEvent) {
    const target = event.target as HTMLElement;
    if (!target.closest('.barrio-container')) {
      this.limpiarSugerencias();
    }
  }

  cargarBarriosDelBackend() {
    this.http.get<Barrio[]>('http://localhost:8080/api/barrios?ciudad=mdp')
      .subscribe({
        next: (data) => { this.allBarrios = data; },
        error: (err) => console.error('Error cargando barrios', err)
      });
  }

  onInputManual(event: any) {
    const termino = event.target.value.toLowerCase();
    if (termino.length < 1) {
      this.limpiarSugerencias();
      return;
    }
    const filtrados = this.allBarrios
      .filter(b => b.nombre.toLowerCase().includes(termino))
      .slice(0, 5);
    this.resultadosBusqueda.set(filtrados);
    this.mostrandoResultados.set(filtrados.length > 0);
  }

  seleccionarLocalidad(barrio: Barrio) {
    this.limpiarSugerencias();
    this.aplicarValorCiudad(barrio.nombre, barrio.lat, barrio.lon);
  }

  async usarUbicacionActual() {
    this.cargandoGps.set(true);
    this.limpiarSugerencias();

    // Retraso artificial para que el usuario vea el mensaje "Obteniendo ubicación..."
    await new Promise(resolve => setTimeout(resolve, 800));

    try {
      const coords = await this.locationService.obtenerCoordenadasGPS();
      const barrioEncontrado = this.locationService.obtenerBarrioMasCercano(coords.lat, coords.lon, this.allBarrios);

      if (barrioEncontrado) {
        this.aplicarValorCiudad(barrioEncontrado.nombre, barrioEncontrado.lat, barrioEncontrado.lon);
      }
    } catch (err) {
      console.error('Error GPS:', err);
    } finally {
      this.cargandoGps.set(false);
      this.cdr.detectChanges();
    }
  }

  private aplicarValorCiudad(nombre: string, lat: number, lon: number) {
    this.latitudSeleccionada = lat;
    this.longitudSeleccionada = lon;

    this.registerForm.patchValue({ ciudad: nombre });
    this.registerForm.get('ciudad')?.markAsTouched();

    this.limpiarSugerencias();

    setTimeout(() => {
      const input = document.getElementById('reg-ciudad') as HTMLInputElement;
      if (input) {
        input.dispatchEvent(new Event('input', { bubbles: true }));
        input.blur();
      }
      this.cdr.detectChanges();
    }, 50);
  }

  limpiarSugerencias() {
    this.mostrandoResultados.set(false);
    this.resultadosBusqueda.set([]);
    this.cdr.detectChanges();
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
    this.registerSubmit.emit(datosRegistro);
  }

  onToggle(): void { this.toggleView.emit(); }
  togglePassword() { this.showPassword.update(val => !val); }
}
