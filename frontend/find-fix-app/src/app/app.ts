import { Component, signal } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { Hero } from "./components/landing-page/hero/hero";
import { Home } from "./pages/home/home.page";

@Component({
  selector: 'app-root',
  imports: [RouterOutlet],
  templateUrl: './app.html',
  styleUrl: './app.css'
})
export class App {
  protected readonly title = signal('find-fix-app');
}
