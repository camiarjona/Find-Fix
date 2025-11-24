import { CommonModule } from '@angular/common';
import { Component } from '@angular/core';
import { BuscarEspecialistasComponent } from "../../../components/cliente/buscar-especialistas-component/buscar-especialistas-component";

@Component({
  selector: 'app-buscar-especialistas.page',
  standalone: true,
  imports: [CommonModule, BuscarEspecialistasComponent],
  templateUrl: './buscar-especialistas.page.html',
  styleUrl: './buscar-especialistas.page.css',
})
export class BuscarEspecialistas {

}
