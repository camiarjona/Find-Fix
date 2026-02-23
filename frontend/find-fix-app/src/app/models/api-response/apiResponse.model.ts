export interface ApiResponse<T> {
  mensaje: string;
  data: T;
}
export interface PageResponse<T> {
  content: T[];
  totalPages: number;
  totalElements: number;
  size: number;
  number: number;
}
