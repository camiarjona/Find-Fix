import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { HttpClientModule } from '@angular/common/http';
import { FavoritoModel } from '../../../models/favoritos/lista-favs.model';
import { FavoritoService } from '../../../services/favoritos/lista-favs.service'


@Component({
  selector: 'app-mis-favoritos',
  standalone: true,
  imports: [CommonModule, RouterModule, HttpClientModule],
  templateUrl: './mis-favoritos.page.html',
  styleUrls: ['./mis-favoritos.page.css']
})

export class MisFavoritosPage implements OnInit {

  private favoritoService = inject(FavoritoService);

  favoritos: FavoritoModel[] = [];
  isLoading: boolean = true;
  errorMessage: string | null = null;
 
  constructor() { }

  ngOnInit(): void {
    this.cargarFavoritos();
  }

  /**
   * Carga la lista de especialistas favoritos.
   */
  cargarFavoritos(): void {
    this.isLoading = true;
    this.errorMessage = null;

   
    this.favoritoService.obtenerFavoritosPorCliente().subscribe({
      next: (response) => {
        this.favoritos = response.data || [];
        this.isLoading = false;
      },
      error: (err) => {
        console.error('Error al cargar favoritos:', err);
        this.errorMessage = 'No se pudo cargar la lista. Por favor, verifica tu sesión.';
        this.isLoading = false;
      }
    });
  }

  /**
   * Elimina un especialista de la lista de favoritos.
   * @param especialistaEmail
   */
  eliminarDeFavoritos(especialistaEmail: string): void {
    if (!confirm('¿Estás seguro de que quieres eliminar este especialista de tus favoritos?')) {
      return;
    }

    this.favoritoService.eliminarFavorito(especialistaEmail).subscribe({
      next: () => {
        // Filtramos la lista localmente usando el email del especialista
        this.favoritos = this.favoritos.filter(fav => fav.especialistaEmail !== especialistaEmail);
        alert('Especialista eliminado de favoritos.');
      },
      error: (err) => {
        console.error('Error al eliminar favorito:', err);
        alert('Error al eliminar de favoritos. Revisa la consola.');
      }
    });
  }

  /**
   * Navega al perfil del especialista.
   * @param especialistaEmail
   */
  verPerfilEspecialista(especialistaEmail: string): void {
    console.log(`Navegando al perfil del especialista con Email: ${especialistaEmail}`);
  }
}
