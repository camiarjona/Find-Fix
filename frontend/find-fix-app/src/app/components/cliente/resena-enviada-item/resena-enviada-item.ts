// import { Component, Input } from '@angular/core';
// import { CommonModule } from '@angular/common';
// import { MostrarResenaClienteDTO } from '../../../models/reseña/reseña.model';
// import { MatCardModule } from '@angular/material/card';
// import { MatIconModule } from '@angular/material/icon';

// @Component({
//   selector: 'app-resena-enviada-item',
//   standalone: true,
//   imports: [
//     CommonModule,
//     MatCardModule,
//     MatIconModule
//   ],
//   templateUrl: './resena-enviada-item.html',
//   styleUrls: ['./resena-enviada-item.css']
// })
// export class ResenaEnviadaItemComponent {
//   @Input() resena!: MostrarResenaClienteDTO;

//   /**

//     @param rating
//     @returns
//    */
//   getStars(rating: number): number[] {
//     const fullStars = Math.round(rating);
//     return Array.from({ length: 5 }, (_, i) => i < fullStars ? 1 : 0);
//   }
// }
