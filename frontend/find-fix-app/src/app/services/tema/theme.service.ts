import { DOCUMENT, effect, inject, Injectable, signal } from '@angular/core';

@Injectable({
  providedIn: 'root',
})
export class ThemeService {

  // Signal para saber el estado actual
  isDarkMode = signal<boolean>(false);

  private document = inject(DOCUMENT);

  constructor() {
    // 1. Cargar preferencia guardada (opcional)
    const savedTheme = localStorage.getItem('theme');
    if (savedTheme === 'dark') {
      this.isDarkMode.set(true);
    }

    // 2. Efecto: Cada vez que cambia la signal, actualizamos el <body>
    effect(() => {
      if (this.isDarkMode()) {
        this.document.body.classList.add('dark-mode');
        localStorage.setItem('theme', 'dark');
      } else {
        this.document.body.classList.remove('dark-mode');
        localStorage.setItem('theme', 'light');
      }
    });
  }

  toggleTheme() {
    this.isDarkMode.update(prev => !prev);
  }
}
