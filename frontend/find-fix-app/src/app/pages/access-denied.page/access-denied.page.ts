import { Component, inject } from '@angular/core';
import { Router } from '@angular/router';
import { Location as AngularLocation } from '@angular/common';

@Component({
  selector: 'app-access-denied.page',
  imports: [],
  templateUrl: './access-denied.page.html',
  styleUrl: './access-denied.page.css',
})
export class AccessDeniedPage {

  private router = inject(Router);
  private location = inject(AngularLocation);

  goBack() {
    this.location.back(); // Vuelve a la página anterior
  }

  goHome() {
    this.router.navigate(['/']); // Vuelve al home
  }
}
