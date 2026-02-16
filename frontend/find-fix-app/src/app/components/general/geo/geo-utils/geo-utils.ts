import { Component } from '@angular/core';

@Component({
  selector: 'app-geo-utils',
  imports: [],
  templateUrl: './geo-utils.html',
  styleUrl: './geo-utils.css',
})
export class GeoUtils {

  private static readonly RADIO_TIERRA_KM = 6371;

  public static calcularDistancia(lat1: number, lon1: number, lat2: number, lon2: number): number {
    const toRad = (value: number) => (value * Math.PI) / 180;

    const dLat = toRad(lat2 - lat1);
    const dLon = toRad(lon2 - lon1);

    const a =
      Math.sin(dLat / 2) * Math.sin(dLat / 2) +
      Math.cos(toRad(lat1)) * Math.cos(toRad(lat2)) *
      Math.sin(dLon / 2) * Math.sin(dLon / 2);

    const c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
    return this.RADIO_TIERRA_KM * c;
  }
}
