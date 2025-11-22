import { CommonModule } from '@angular/common';
import { Component } from '@angular/core';
import { RouterLink, RouterLinkActive } from '@angular/router';

@Component({
  selector: 'app-landing-footer-component',
  imports: [RouterLink,CommonModule],
  templateUrl: './landing-footer-component.html',
  styleUrl: './landing-footer-component.css',
})
export class LandingFooterComponent {
currentYear = new Date().getFullYear();
}
