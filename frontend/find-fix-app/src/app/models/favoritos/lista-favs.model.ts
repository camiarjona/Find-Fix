export interface FavoritoModel {
  nombre: string;
  apellido: string;
  ciudad: string;
  oficios: string[];
  calificacionPromedio: number;
  email: string;
}

export interface AgregarFavoritoDTO {
  especialistaEmail: string;
}
