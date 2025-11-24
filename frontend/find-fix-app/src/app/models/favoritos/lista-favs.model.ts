export interface AgregarFavoritoDTO {
  especialistaEmail: string;
}

export interface FavoritoModel {
  id: number;
  clienteMail: String;
  especialistaEmail: string;
  especialistaId: number;
  fechaCreacion: Date;
  especialistaNombre: string;
  especialistaOficio: string;
}
