
export interface UserProfile {
  nombre: string;
  apellido: string;
  email: string;
  ciudad?: string;
  telefono?: string;
  roles: string[];
  activo: boolean;
}

export interface LoginCredentials {
  email: string;
  password: string;
}

export interface RegisterCredentials {
  nombre: string;
  apellido: string;
  email: string;
  password: string;
}

export interface UpdateUserRequest {
  nombre?: string;
  apellido?: string;
  telefono?: string;
  ciudad?: string;
}

export interface UpdatePasswordRequest {
  passwordActual: string;
  passwordNuevo: string;
}

export interface UserSearchFilters {
  email?: string;
  id?: number;
  rol?: string;
  roles?: string[];
}
