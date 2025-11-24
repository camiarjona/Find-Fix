import { Component, inject, signal } from '@angular/core';
import { Router, RouterLink } from '@angular/router';
import { sign } from 'chart.js/helpers';
import { EspecialistaService } from '../../../services/especialista/especialista.service';
import { EspecialistaDTO } from '../../../models/cliente/buscar-especialistas-models';
import { BuscarEspecialistaService } from '../../../services/cliente/buscar-especialista-service';
import { BuscarEspecialistas } from '../../cliente/buscar-especialistas.page/buscar-especialistas.page';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { FooterComponent } from '../../../components/general/footer-component/footer-component';
import { LandingFooterComponent } from "../../../components/general/landing-footer-component/landing-footer-component";
import { NavBarLanding } from "../../../components/landing-page/nav-bar-landing/nav-bar-landing";

@Component({
  selector: 'app-landing-list-especialistas-component',
  imports: [CommonModule, FormsModule, RouterLink, LandingFooterComponent, NavBarLanding],
  templateUrl: './landing-list-especialistas-component.html',
  styleUrl: './landing-list-especialistas-component.css',
})
export class LandingListEspecialistasComponent {
private buscarEspecialistasService = inject(BuscarEspecialistaService);
  private router = inject(Router);
public especialistas = signal<EspecialistaDTO[]>([]);

 ngOnInit() {
    this.cargarEspecialistas();
  }

  cargarEspecialistas() {
    this.buscarEspecialistasService.obtenerDisponiblesPublico().subscribe({
      next: (res) => this.especialistas.set(res.data),
      error: (err) => console.error(err)
    });
  }
  // Acción al intentar contratar
  irALogin() {
    this.router.navigate(['/auth']);
  }
}
