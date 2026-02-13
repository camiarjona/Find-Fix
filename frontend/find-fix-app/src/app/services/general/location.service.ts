import { HttpClient } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';

@Injectable({
  providedIn: 'root',
})
export class LocationService {

  private http = inject(HttpClient);

obtenerBarrioMasCercano(userLat: number, userLon: number, barrios: any[]): any {
    if (!barrios || barrios.length === 0) return null;

    return barrios.reduce((prev, curr) => {
      const distPrev = this.calcularDistanciaRapida(userLat, userLon, prev.lat, prev.lon);
      const distCurr = this.calcularDistanciaRapida(userLat, userLon, curr.lat, curr.lon);
      return distCurr < distPrev ? curr : prev;
    });
  }

  // Función matemática para calcular distancia entre dos puntos
  private calcularDistanciaRapida(lat1: number, lon1: number, lat2: number, lon2: number): number {
    return Math.sqrt(Math.pow(lat2 - lat1, 2) + Math.pow(lon2 - lon1, 2));
  }

  // función de GPS
  obtenerCoordenadasGPS(): Promise<{lat: number, lon: number}> {
    return new Promise((resolve, reject) => {
      navigator.geolocation.getCurrentPosition(
        (pos) => resolve({ lat: pos.coords.latitude, lon: pos.coords.longitude }),
        (err) => reject(err),
        { enableHighAccuracy: true }
      );
    });
  }
}
