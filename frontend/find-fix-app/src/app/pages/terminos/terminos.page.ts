import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';

@Component({
  selector: 'app-terminos',
  standalone: true,
  imports: [CommonModule, RouterLink],
  templateUrl: './terminos.page.html',
  styleUrls: ['./terminos.page.css']
})
export class TerminosPage {}