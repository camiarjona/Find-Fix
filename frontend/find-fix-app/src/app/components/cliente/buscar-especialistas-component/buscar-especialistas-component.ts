import { Component, inject, signal, AfterViewInit, effect, OnInit, HostListener } from '@angular/core';
import { FiltroEspecialistasDTO, EspecialistaDTO } from '../../../models/cliente/buscar-especialistas-models';
import { BuscarEspecialistaService } from '../../../services/cliente/buscar-especialista-service';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { PerfilEspecialista } from '../../../models/especialista/especialista.model';
import { FavoritoService } from '../../../services/favoritos/lista-favs.service';
import { ModalFeedbackComponent } from "../../general/modal-feedback.component/modal-feedback.component";
import * as L from 'leaflet';
import { HttpClient } from '@angular/common/http';
import { UserService } from '../../../services/user/user.service';
import { GeoUtils } from '../../general/geo/geo-utils/geo-utils';
import { ordenarDinamicamente } from '../../../utils/sort-utils';
import { RouterLink } from '@angular/router';

interface Barrio {
  nombre: string;
  lat: number;
  lon: number;
}

@Component({
  selector: 'app-buscar-especialistas-component',
  standalone: true,
  imports: [CommonModule, FormsModule, ModalFeedbackComponent, RouterLink],
  templateUrl: './buscar-especialistas-component.html',
  styleUrl: './buscar-especialistas-component.css',
})
export class BuscarEspecialistasComponent implements OnInit, AfterViewInit {

  private clienteService = inject(BuscarEspecialistaService);
  private favoritosService = inject(FavoritoService);
  private userService = inject(UserService);
  private http = inject(HttpClient);

  // Señales de datos
  public especialistas = this.clienteService.especialistas;
  public especialistasOrdenados = signal<EspecialistaDTO[]>([]);
  public especialistasVisibles = signal<EspecialistaDTO[]>([]);
  public ciudades = this.clienteService.ciudades;
  public oficiosDisponibles = this.clienteService.oficios;
  public favoritosSet = signal<Set<string>>(new Set());

  public allBarrios: Barrio[] = [];
  public cityFilterSuggestions = signal<Barrio[]>([]);

  public showMap = signal(true);
  private map: L.Map | undefined;
  private markersLayer: L.LayerGroup | undefined;
  private userMarker: L.Marker | undefined;
  private mapMarkers: Map<string, L.Marker> = new Map();

  private userLat: number | null = null;
  private userLon: number | null = null;

  // Paginación
  public currentPage = signal(0);
  public pageSize = 6;
  public totalPages = signal(0);

  public filtros: FiltroEspecialistasDTO = { ciudad: '', oficio: '', minCalificacion: 0 };
  public feedbackData = { visible: false, tipo: 'success' as 'success' | 'error', titulo: '', mensaje: '' };
  public showModalContratar = signal(false);
  public showModalDetalle = signal(false);
  public especialistaSeleccionado: EspecialistaDTO | any;
  public especialistaSeleccionaCompleto = signal<PerfilEspecialista | any>(null);
  public descripcionTrabajo = '';
  public isSubmitting = signal(false);
  public isLoadingDetalle = signal(false);
  public criterioOrden = signal<string>('distancia');

  dropdownOpen: string | null = null;

  constructor() {
    effect(() => {
      const base = this.especialistas();
      const criterio = this.criterioOrden();
      let lista = [...base];

      if (lista.length > 0) {
        switch (criterio) {
          case 'calificacion':
            lista = ordenarDinamicamente(lista, 'calificacionPromedio', 'desc');
            break;
          case 'nombre':
            lista = ordenarDinamicamente(lista, 'nombre', 'asc');
            break;
          case 'distancia':
          default:
            if (this.userLat !== null && this.userLon !== null) {
              lista.sort((a, b) => {
                const distA = (a.latitud != null && a.longitud != null)
                  ? GeoUtils.calcularDistancia(this.userLat!, this.userLon!, a.latitud, a.longitud)
                  : 9999;
                const distB = (b.latitud != null && b.longitud != null)
                  ? GeoUtils.calcularDistancia(this.userLat!, this.userLon!, b.latitud, b.longitud)
                  : 9999;
                return distA - distB;
              });
            }
            break;
        }
      }

      this.especialistasOrdenados.set(lista);

      // Lógica de Paginación Integrada
      this.totalPages.set(Math.ceil(lista.length / this.pageSize));
      if (this.currentPage() >= this.totalPages() && this.totalPages() > 0) {
        this.currentPage.set(0);
      }

      this.actualizarVistaPaginada();

      if (this.map) { this.actualizarMarcadoresAzules(lista); }
    });
  }

  ngOnInit() {
    this.obtenerUbicacionGPS();
    this.clienteService.obtenerEspecialistas();
    this.clienteService.cargarDatosFiltros();
    this.cargarFavoritosDelUsuario();
    this.cargarBarriosDelBackend();
    this.cargarDatosUsuario();
  }

  ngAfterViewInit() { this.inicializarMapa(); }

  actualizarVistaPaginada() {
    const inicio = this.currentPage() * this.pageSize;
    const fin = inicio + this.pageSize;
    this.especialistasVisibles.set(this.especialistasOrdenados().slice(inicio, fin));
  }

  cambiarPagina(nuevaPagina: number) {
    this.currentPage.set(nuevaPagina);
    this.actualizarVistaPaginada();
    window.scrollTo({ top: 0, behavior: 'smooth' });
  }

  // --- MÉTODOS DE ACCIÓN ---
  public irAEspecialistaEnMapa(esp: EspecialistaDTO) {
    if (this.map && esp.latitud && esp.longitud) {
      this.map.flyTo([esp.latitud, esp.longitud], 15);
      const m = this.mapMarkers.get(esp.email);
      if (m) m.openPopup();
    }
  }

  abrirModalDetalle(esp: EspecialistaDTO) {
    this.especialistaSeleccionaCompleto.set(null);
    this.isLoadingDetalle.set(true);
    this.showModalDetalle.set(true);
    this.clienteService.obtenerPerfilCompleto(esp.email).subscribe({
      next: (res) => {
        this.especialistaSeleccionaCompleto.set(res.data);
        this.isLoadingDetalle.set(false);
      },
      error: () => this.isLoadingDetalle.set(false)
    });
  }

  cerrarModalDetalle() { this.showModalDetalle.set(false); }

  abrirModalContratar(esp: EspecialistaDTO) {
    this.especialistaSeleccionado = esp;
    this.descripcionTrabajo = '';
    this.showModalContratar.set(true);
  }

  cerrarModalContratar() { this.showModalContratar.set(false); }

  enviarSolicitud() {
    if (!this.especialistaSeleccionado || !this.descripcionTrabajo.trim()) return;
    this.isSubmitting.set(true);
    this.clienteService.contratarEspecialista({
      emailEspecialista: this.especialistaSeleccionado.email,
      descripcion: this.descripcionTrabajo
    }).subscribe({
      next: () => {
        this.feedbackData = { visible: true, titulo: 'Éxito', mensaje: 'Solicitud enviada', tipo: 'success' };
        this.isSubmitting.set(false);
        this.cerrarModalContratar();
      },
      error: () => this.isSubmitting.set(false)
    });
  }

  toggleFavorito(esp: any, event?: Event) {
    if (event) event.stopPropagation();
    const email = esp.email;
    if (this.esFavorito(email)) {
      this.favoritosService.eliminarFavorito(email).subscribe({
        next: () => this.favoritosSet.update(s => { const n = new Set(s); n.delete(email); return n; })
      });
    } else {
      this.favoritosService.agregarFavorito({ especialistaEmail: email }).subscribe({
        next: () => this.favoritosSet.update(s => { const n = new Set(s); n.add(email); return n; })
      });
    }
  }

  esFavorito(email: string): boolean { return this.favoritosSet().has(email); }

  // --- LÓGICA DE MAPA Y UBICACIÓN ---
  private actualizarMarcadoresAzules(lista: EspecialistaDTO[]) {
    if (!this.map || !this.markersLayer) return;
    this.markersLayer.clearLayers();
    this.mapMarkers.clear();

    lista.forEach(esp => {
      if (esp.latitud != null && esp.longitud != null) {
        const marker = L.marker([esp.latitud, esp.longitud]);
        const popupContent = `
          <div id="p-${esp.email}" style="cursor: pointer; text-align: center;">
            <strong>${esp.nombre} ${esp.apellido}</strong><br>
            <span>${esp.oficios?.[0] || 'Especialista'}</span><br>
            <div style="color: #f59e0b;">⭐ ${esp.calificacionPromedio || 0}</div>
          </div>`;
        marker.bindPopup(popupContent);
        marker.on('popupopen', () => {
          const div = document.getElementById(`p-${esp.email}`);
          if (div) {
            div.onclick = () => this.abrirModalDetalle(esp);
            div.ontouchend = () => this.abrirModalDetalle(esp);
          }
        });
        marker.addTo(this.markersLayer!);
        this.mapMarkers.set(esp.email, marker);
      }
    });
  }

  private inicializarMapa(): void {
    const container = document.getElementById('map');
    if (!container) return;
    if (this.map) { this.map.remove(); }
    this.map = L.map('map').setView([this.userLat || -38.0055, this.userLon || -57.5426], 13);
    L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', { maxZoom: 19, attribution: '© OpenStreetMap' }).addTo(this.map);
    this.markersLayer = L.layerGroup().addTo(this.map);
    setTimeout(() => { this.map?.invalidateSize(); }, 200);
  }

  obtenerUbicacionGPS() {
    if (!navigator.geolocation) return;
    navigator.geolocation.getCurrentPosition((pos) => {
      this.userLat = pos.coords.latitude;
      this.userLon = pos.coords.longitude;
      if (this.map) {
        this.map.setView([this.userLat, this.userLon], 15);
        this.colocarPinRojoUsuario(this.userLat, this.userLon, "Estás aquí");
      }
      this.aplicarFiltros();
    });
  }

  cargarDatosUsuario() {
    this.userService.getProfile().subscribe({
      next: (res) => {
        const u = res.data;
        if (u && typeof u.latitud === 'number' && typeof u.longitud === 'number' && this.userLat === null) {
          this.userLat = u.latitud;
          this.userLon = u.longitud;
          if (this.map) {
            this.map.setView([this.userLat, this.userLon], 14);
            this.colocarPinRojoUsuario(this.userLat, this.userLon, "Ubicación Guardada");
          }
        }
        this.aplicarFiltros();
      }
    });
  }

  private colocarPinRojoUsuario(lat: number, lon: number, n: string) {
    const redIcon = L.icon({
      iconUrl: 'https://raw.githubusercontent.com/pointhi/leaflet-color-markers/master/img/marker-icon-2x-red.png',
      shadowUrl: 'https://cdnjs.cloudflare.com/ajax/libs/leaflet/1.7.1/images/marker-shadow.png',
      iconSize: [25, 41], iconAnchor: [12, 41], popupAnchor: [1, -34], shadowSize: [41, 41]
    });
    if (this.userMarker && this.map) { this.map.removeLayer(this.userMarker); }
    this.userMarker = L.marker([lat, lon], { icon: redIcon }).addTo(this.map!);
    this.userMarker.bindPopup(`<b>${n}</b>`).openPopup();
  }

  seleccionarCiudadFiltroManual(barrio: Barrio) {
    this.filtros.ciudad = barrio.nombre;
    this.cityFilterSuggestions.set([]);
    if (this.map) {
      this.map.flyTo([barrio.lat, barrio.lon], 15);
      this.colocarPinRojoUsuario(barrio.lat, barrio.lon, barrio.nombre);
    }
    this.userLat = barrio.lat;
    this.userLon = barrio.lon;
    this.aplicarFiltros();
  }

  aplicarFiltros() {
    this.currentPage.set(0);
    const f: FiltroEspecialistasDTO = {};
    if (this.filtros.ciudad) f.ciudad = this.filtros.ciudad;
    if (this.filtros.oficio) f.oficio = this.filtros.oficio;
    if (this.filtros.minCalificacion! > 0) f.minCalificacion = this.filtros.minCalificacion;
    if (this.userLat && this.userLon) { f.latitud = this.userLat; f.longitud = this.userLon; }
    this.clienteService.filtrarEspecialistas(f);
  }

  limpiarFiltros() {
    this.filtros = { ciudad: '', oficio: '', minCalificacion: 0 };
    this.criterioOrden.set('distancia');
    this.currentPage.set(0);
    this.clienteService.obtenerEspecialistas();
  }

  toggleMap() { this.showMap.update(v => !v); if (this.showMap()) { setTimeout(() => this.inicializarMapa(), 200); } }
  cerrarFeedback() { this.feedbackData.visible = false; }
  cargarFavoritosDelUsuario() { this.favoritosService.obtenerFavoritosPorCliente().subscribe({ next: (resp) => this.favoritosSet.set(new Set((resp.data || []).map(f => f.email))) }); }
  cargarBarriosDelBackend() { this.http.get<Barrio[]>('http://localhost:8080/api/barrios?ciudad=mdp').subscribe({ next: (data) => this.allBarrios = data }); }
  buscarCiudades(event: Event) {
    const input = event.target as HTMLInputElement; this.filtros.ciudad = input.value; const t = input.value.toLowerCase();
    if (t.length < 1) { this.cityFilterSuggestions.set([]); return; }
    this.cityFilterSuggestions.set(this.allBarrios.filter(b => b.nombre.toLowerCase().includes(t)).slice(0, 5));
  }

  toggleDropdown(menu: string, event: Event) {
    event.stopPropagation();
    this.dropdownOpen = this.dropdownOpen === menu ? null : menu;
  }

  seleccionarOficio(valor: string) {
    this.filtros.oficio = valor;
    this.dropdownOpen = null;
    this.aplicarFiltros();
  }

  seleccionarOrden(valor: string) {
    this.criterioOrden.set(valor || 'distancia');
    this.dropdownOpen = null;
  }

  @HostListener('document:click', ['$event'])
  onDocumentClick(event: MouseEvent) {
    const target = event.target as HTMLElement;
    if (!target.closest('.custom-select-wrapper')) {
      this.dropdownOpen = null;
    }
  }
}
