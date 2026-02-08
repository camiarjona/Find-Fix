import { Component, inject, signal, AfterViewInit, effect, Injector, OnInit } from '@angular/core';

import { FiltroEspecialistasDTO, EspecialistaDTO } from '../../../models/cliente/buscar-especialistas-models';

import { BuscarEspecialistaService } from '../../../services/cliente/buscar-especialista-service';

import { CommonModule } from '@angular/common';

import { FormsModule } from '@angular/forms';

import { PerfilEspecialista } from '../../../models/especialista/especialista.model';

import { FavoritoService } from '../../../services/favoritos/lista-favs.service';

import { AgregarFavoritoDTO } from '../../../models/favoritos/lista-favs.model';

import { ModalFeedbackComponent } from "../../general/modal-feedback.component/modal-feedback.component";

import * as L from 'leaflet';

import { HttpClient } from '@angular/common/http';

import { UserService } from '../../../services/user/user.service';



interface Barrio {

  nombre: string;

  lat: number;

  lon: number;

}



@Component({

  selector: 'app-buscar-especialistas-component',

  imports: [CommonModule, FormsModule, ModalFeedbackComponent],

  templateUrl: './buscar-especialistas-component.html',

  styleUrl: './buscar-especialistas-component.css',

})

export class BuscarEspecialistasComponent implements OnInit, AfterViewInit {



  private clienteService = inject(BuscarEspecialistaService);

  private favoritosService = inject(FavoritoService);

  private userService = inject(UserService);

  private http = inject(HttpClient);



  // --- DATOS ---

  public especialistas = this.clienteService.especialistas;

  public ciudades = this.clienteService.ciudades;

  public oficiosDisponibles = this.clienteService.oficios;

  public favoritosSet = signal<Set<string>>(new Set());



  // --- VARIABLES PARA BARRIOS (JSON) ---

  public allBarrios: Barrio[] = [];

  public cityFilterSuggestions = signal<Barrio[]>([]);



  // --- ESTADO MAPA ---

  public showMap = signal(true);

  private map: L.Map | undefined;

  private markersLayer: L.LayerGroup | undefined;

  private userMarker: L.Marker | undefined;



  // Variables para la ubicación del cliente logueado

  private userLat: number | null = null;

  private userLon: number | null = null;



  public filtros: FiltroEspecialistasDTO = { ciudad: '', oficio: '', minCalificacion: 0 };

  public showConfirmPass = signal(false);

  public feedbackData = { visible: false, tipo: 'success' as 'success' | 'error', titulo: '', mensaje: '' };

  public showModalContratar = signal(false);

  public showModalDetalle = signal(false);

  public especialistaSeleccionado: EspecialistaDTO | any;

  public especialistaSeleccionaCompleto = signal<PerfilEspecialista | any>(null);

  public descripcionTrabajo = '';

  public isSubmitting = signal(false);

  public isLoadingDetalle = signal(false);



  constructor() {

    effect(() => {

      const lista = this.especialistas();

      if (this.map) {

        this.actualizarMarcadoresAzules(lista);

      }

    });

  }



  ngOnInit() {

    this.clienteService.obtenerEspecialistas();

    this.clienteService.cargarDatosFiltros();

    this.cargarFavoritosDelUsuario();

    this.cargarBarriosDelBackend();

    // Intenta obtener la ubicación real primero.
    // Si el usuario rechaza, usará la del perfil (cargarDatosUsuario) como plan B.
    this.obtenerUbicacionGPS();

    this.cargarDatosUsuario();

  }



  // Obtiene perfil del cliente y centra el mapa ---

  cargarDatosUsuario() {

    this.userService.getProfile().subscribe({

      next: (res) => {

        const usuario = res.data;

        if (usuario && usuario.latitud && usuario.longitud) {

          this.userLat = usuario.latitud;

          this.userLon = usuario.longitud;

          if (this.map) {

            this.map.setView([this.userLat, this.userLon], 14);

            this.colocarPinRojoUsuario(this.userLat, this.userLon, "Tu ubicación");

          }

          this.aplicarFiltros();

        }

      },

      error: (err) => console.error('Error al obtener ubicación del usuario', err)

    });

  }



  cargarBarriosDelBackend() {

    this.http.get<Barrio[]>('http://localhost:8080/api/barrios?ciudad=mdp')

      .subscribe({

        next: (data) => {

          this.allBarrios = data;

        },

        error: (err) => console.error('Error cargando barrios', err)

      });

  }



  ngAfterViewInit() {

    this.inicializarMapa();

  }



  buscarCiudades(event: Event) {

    const input = event.target as HTMLInputElement;

    const termino = input.value.toLowerCase();

    this.filtros.ciudad = input.value;



    if (termino.length < 1) {

      this.cityFilterSuggestions.set([]);

      return;

    }



    const filtrados = this.allBarrios

      .filter(b => b.nombre.toLowerCase().includes(termino))

      .slice(0, 5);



    this.cityFilterSuggestions.set(filtrados);

  }



  seleccionarCiudadFiltro(barrio: Barrio) {

    this.filtros.ciudad = barrio.nombre;

    this.cityFilterSuggestions.set([]);



    if (this.map) {

      this.map.flyTo([barrio.lat, barrio.lon], 15);

      this.colocarPinRojoUsuario(barrio.lat, barrio.lon, barrio.nombre);

    }

  }



  private colocarPinRojoUsuario(lat: number, lon: number, nombre: string) {

    const redIcon = L.icon({

      iconUrl: 'https://raw.githubusercontent.com/pointhi/leaflet-color-markers/master/img/marker-icon-2x-red.png',

      shadowUrl: 'https://cdnjs.cloudflare.com/ajax/libs/leaflet/1.7.1/images/marker-shadow.png',

      iconSize: [25, 41],

      iconAnchor: [12, 41],

      popupAnchor: [1, -34],

      shadowSize: [41, 41]

    });



    if (this.userMarker && this.map) {

      this.map.removeLayer(this.userMarker);

    }



    this.userMarker = L.marker([lat, lon], { icon: redIcon }).addTo(this.map!);

    this.userMarker.bindPopup(`<b>${nombre}</b>`).openPopup();

  }



  private inicializarMapa(): void {

    const container = document.getElementById('map');

    if (!container) return;



    // Coordenadas por defecto (Centro MDP) si el usuario no tiene ubicación

    let latCenter = -38.0055;

    let lonCenter = -57.5426;

    let zoomLevel = 13;



    // Si ya cargaron los datos del usuario, usamos su ubicación como centro

    if (this.userLat && this.userLon) {

      latCenter = this.userLat;

      lonCenter = this.userLon;

      zoomLevel = 14;

    }



    if (this.map) {

      this.map.remove();

    }



    const blueIcon = L.icon({

      iconRetinaUrl: 'https://cdnjs.cloudflare.com/ajax/libs/leaflet/1.7.1/images/marker-icon-2x.png',

      iconUrl: 'https://cdnjs.cloudflare.com/ajax/libs/leaflet/1.7.1/images/marker-icon.png',

      shadowUrl: 'https://cdnjs.cloudflare.com/ajax/libs/leaflet/1.7.1/images/marker-shadow.png',

      iconSize: [25, 41],

      iconAnchor: [12, 41],

      popupAnchor: [1, -34],

      tooltipAnchor: [16, -28],

      shadowSize: [41, 41]

    });

    L.Marker.prototype.options.icon = blueIcon;



    this.map = L.map('map').setView([latCenter, lonCenter], zoomLevel);



    L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {

      maxZoom: 19,

      attribution: '© OpenStreetMap'

    }).addTo(this.map);



    this.markersLayer = L.layerGroup().addTo(this.map);


    if (this.userLat && this.userLon) {

      this.colocarPinRojoUsuario(this.userLat, this.userLon, "Tu ubicación");

    }



    setTimeout(() => {

      this.map?.invalidateSize();

      this.actualizarMarcadoresAzules(this.especialistas());

    }, 200);

  }



  private actualizarMarcadoresAzules(lista: EspecialistaDTO[]) {

    if (!this.map || !this.markersLayer) return;



    this.markersLayer.clearLayers();



    lista.forEach(esp => {

      if (esp.latitud && esp.longitud) {

        const marker = L.marker([esp.latitud, esp.longitud]);



        const popupContent = `

          <div style="text-align: center;">

            <strong style="display:block; margin-bottom:4px;">${esp.nombre} ${esp.apellido}</strong>

            <span style="background: #eee; padding: 2px 6px; border-radius: 4px; font-size: 0.9em;">

              ${esp.oficios?.[0] || 'Especialista'}

            </span>

            <div style="margin-top: 5px; color: #f59e0b; font-weight: bold;">⭐ ${esp.calificacionPromedio}</div>

          </div>

        `;



        marker.bindPopup(popupContent);

        marker.addTo(this.markersLayer!);

      }

    });

  }



  toggleMap() {

    this.showMap.update(v => !v);

    if (this.showMap()) {

      setTimeout(() => {

        this.inicializarMapa();

      }, 200);

    }

  }



  cargarFavoritosDelUsuario() {

    this.favoritosService.obtenerFavoritosPorCliente().subscribe({

      next: (resp) => {

        const lista = resp.data || [];

        const emailsFavoritos = new Set(lista.map(fav => fav.email));

        this.favoritosSet.set(emailsFavoritos);

      },

      error: (err) => console.error('Error cargando favoritos', err)

    });

  }



  esFavorito(email: string): boolean {

    return this.favoritosSet().has(email);

  }



  toggleFavorito(esp: EspecialistaDTO | PerfilEspecialista, event?: Event) {

    if (event) event.stopPropagation();

    const email = esp.email;

    const yaEsFavorito = this.esFavorito(email);



    if (yaEsFavorito) {

      this.favoritosService.eliminarFavorito(email).subscribe({

        next: () => {

          this.favoritosSet.update(set => {

            const nuevoSet = new Set(set);

            nuevoSet.delete(email);

            return nuevoSet;

          });

        },

        error: (err) => alert('Error al quitar de favoritos')

      });

    } else {

      const nuevoFavorito: AgregarFavoritoDTO = { especialistaEmail: email };

      this.favoritosService.agregarFavorito(nuevoFavorito).subscribe({

        next: () => {

          this.favoritosSet.update(set => {

            const nuevoSet = new Set(set);

            nuevoSet.add(email);

            return nuevoSet;

          });

        },

        error: (err) => alert('Error al agregar a favoritos')

      });

    }

  }



  aplicarFiltros() {

    const filtrosEnviar: FiltroEspecialistasDTO = {};

    if (this.filtros.ciudad) filtrosEnviar.ciudad = this.filtros.ciudad;

    if (this.filtros.oficio) filtrosEnviar.oficio = this.filtros.oficio;

    if (this.filtros.minCalificacion && this.filtros.minCalificacion > 0) {

      filtrosEnviar.minCalificacion = this.filtros.minCalificacion;

    }

    if (this.userLat && this.userLon) {
      filtrosEnviar.latitud = this.userLat;
      filtrosEnviar.longitud = this.userLon;
    }


    if (Object.keys(filtrosEnviar).length === 0) {

      this.clienteService.obtenerEspecialistas();

    } else {

      this.clienteService.filtrarEspecialistas(filtrosEnviar);

    }

  }



  limpiarFiltros() {

    this.filtros = { ciudad: '', oficio: '', minCalificacion: 0 };



    if (this.userMarker && this.map) {

      this.map.removeLayer(this.userMarker);

      this.userMarker = undefined;

      const lat = this.userLat || -38.0055;

      const lon = this.userLon || -57.5426;

      this.map.setView([lat, lon], this.userLat ? 14 : 13);


      if (this.userLat && this.userLon) {

         this.colocarPinRojoUsuario(this.userLat, this.userLon, "Tu ubicación");

      }

    }



    this.clienteService.obtenerEspecialistas();

  }



  mostrarFeedback(titulo: string, mensaje: string, tipo: 'success' | 'error' = 'success') {

    this.feedbackData = { visible: true, titulo, mensaje, tipo };

  }



  cerrarFeedback() {

    this.feedbackData = { ...this.feedbackData, visible: false };

  }



  abrirModalContratar(especialista: EspecialistaDTO) {

    this.especialistaSeleccionado = especialista;

    this.descripcionTrabajo = '';

    this.showModalContratar.set(true);

  }



  cerrarModalContratar() {

    this.showModalContratar.set(false);

    this.especialistaSeleccionado = null;

  }



  enviarSolicitud() {

    if (!this.especialistaSeleccionado || !this.descripcionTrabajo.trim()) return;

    this.isSubmitting.set(true);

    this.clienteService.contratarEspecialista({

      emailEspecialista: this.especialistaSeleccionado.email,

      descripcion: this.descripcionTrabajo

    }).subscribe({

      next: () => {

        this.mostrarFeedback('Enhorabuena', 'Solicitud enviada con éxito');

        this.isSubmitting.set(false);

        this.cerrarModalContratar();

      },

      error: (err) => {

        this.mostrarFeedback('Error al enviar la solicitud', 'Intente nuevamente');

        this.isSubmitting.set(false);

      }

    });

  }



  abrirModalDetalle(espListado: EspecialistaDTO) {

    this.especialistaSeleccionaCompleto.set(null);

    this.isLoadingDetalle.set(true);

    this.showModalDetalle.set(true);



    this.clienteService.obtenerPerfilCompleto(espListado.email).subscribe({

      next: (res) => {

        this.especialistaSeleccionaCompleto.set(res.data);

        this.isLoadingDetalle.set(false);

      },

      error: (err) => {

        console.error(err);

        this.isLoadingDetalle.set(false);

        this.cerrarModalDetalle();

        alert("No se pudo cargar el detalle.");

      }

    });

  }



  cerrarModalDetalle() {

    this.showModalDetalle.set(false);

  }


  obtenerUbicacionGPS() {
    if (!navigator.geolocation) {
      this.mostrarFeedback('Error', 'Tu navegador no soporta geolocalización', 'error');
      return;
    }

    // Mostramos feedback visual de que estamos buscando
    this.mostrarFeedback('Ubicando...', 'Obteniendo tu posición actual...', 'success');

    navigator.geolocation.getCurrentPosition(
      (position) => {
        // 1. Guardamos las coordenadas exactas
        this.userLat = position.coords.latitude;
        this.userLon = position.coords.longitude;

        console.log("📍 GPS Detectado:", this.userLat, this.userLon);

        // 2. Actualizamos el mapa visualmente
        if (this.map) {
          this.map.setView([this.userLat, this.userLon], 15);
          this.colocarPinRojoUsuario(this.userLat, this.userLon, "Estás aquí (GPS)");
        }

        // 3. AUTOMÁTICAMENTE ACTUALIZAMOS LA LISTA ORDENADA
        this.aplicarFiltros();

        // Cerramos el mensaje de carga
        this.cerrarFeedback();
      },
      (error) => {
        console.error(error);
        let mensaje = 'No pudimos obtener tu ubicación.';
        if (error.code === 1) mensaje = 'Necesitamos permiso para ver tu ubicación.';
        this.mostrarFeedback('Error', mensaje, 'error');
      },
      { enableHighAccuracy: true, timeout: 5000, maximumAge: 0 }
    );
  }
}

