import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { ApiResponse } from '../../models/api-response/apiResponse.model'; // Asegúrate que la ruta a ApiResponse sea correcta


export interface CrearResenaDTO {
  clienteId: number;
  especialistaId: number;
  puntuacion: number;
  comentario: string;
  trabajoId?: number; // Opcional, si la reseña se vincula a un trabajo específico
}

/**
 * DTO para mostrar la reseña en la interfaz.
 */
export interface MostrarResenaDTO {
  id: number;
  puntuacion: number;
  comentario: string;
  fechaCreacion: Date;
  clienteNombre: string;
  especialistaNombre: string;
}
