import { CommonModule } from '@angular/common';
import { Component, inject } from '@angular/core';
import { Router, RouterLink } from '@angular/router';

@Component({
  selector: 'app-nav-bar-landing',
  imports: [CommonModule, RouterLink],
  templateUrl: './nav-bar-landing.html',
  styleUrl: './nav-bar-landing.css',
})
export class NavBarLanding {

  public router = inject(Router);

}
