import { CommonModule } from '@angular/common';
import { Component, EventEmitter, inject, Input, OnInit, Output, signal, ChangeDetectorRef, HostListener } from '@angular/core';
import { AbstractControl, FormBuilder, ReactiveFormsModule, ValidationErrors, Validators } from '@angular/forms';
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

  // Validador personalizado para evitar letras repetidas y secuencias absurdas ("aaaa", "hrtegrw")
  private textoEstrictoValidator(control: AbstractControl): ValidationErrors | null {
    const val = control.value;
    if (!val) return null;
    
    // Solo letras y espacios
    if (!/^[A-Za-zÁÉÍÓÚáéíóúÑñ\s]+$/.test(val)) {
      return { formatoInvalido: true };
    }
    // Evita 3 o más letras idénticas seguidas ("aaa")
    if (/(.)\1{2,}/i.test(val)) {
      return { letrasRepetidas: true };
    }
    // Evita cadenas sin sentido lógico (más de 5 consonantes seguidas sin espacios)
    const sinEspacios = val.replace(/\s/g, '');
    if (/[bcdfghjklmnpqrstvwxyz]{6,}/i.test(sinEspacios)) {
      return { secuenciaInvalida: true };
    }
    return null;
  }

  // Validador estricto de barrio perteneciente a la lista oficial
  private barrioOficialValidator = (control: AbstractControl): ValidationErrors | null => {
    const val = control.value;
    if (!val || this.allBarrios.length === 0) return null;

    const existe = this.allBarrios.some(
      b => b.nombre.toLowerCase() === val.trim().toLowerCase()
    );
    return existe ? null : { barrioNoOficial: true };
  };
  

  public registerForm = this.fb.nonNullable.group({
    nombre: ['', [Validators.required, Validators.minLength(2), this.textoEstrictoValidator]],
    apellido: ['', [Validators.required, Validators.minLength(2), this.textoEstrictoValidator]],
    email: ['', [Validators.required, Validators.email]],
    password: ['', [Validators.required, Validators.minLength(6), Validators.maxLength(12)]],
    ciudad: ['', [Validators.required, this.barrioOficialValidator]]
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
        next: (data) => { this.allBarrios = data; this.registerForm.get('ciudad')?.updateValueAndValidity(); },
        error: (err) => console.error('Error cargando barrios', err)
      });
  }

  onInputManual(event: any) {
    const termino = event.target.value.toLowerCase();
    this.registerForm.get('ciudad')?.updateValueAndValidity();

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

    // Actualizamos el valor del formulario
    this.registerForm.patchValue({ ciudad: nombre });
    this.registerForm.get('ciudad')?.markAsTouched();
    this.registerForm.get('ciudad')?.updateValueAndValidity();

    // 🛑 Forzamos el cierre inmediato limpiando las señales de resultados
    this.mostrandoResultados.set(false);
    this.resultadosBusqueda.set([]);
    
    this.cdr.detectChanges();
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
