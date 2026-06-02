// ============================================================
//  TYPES — Match Spring Boot DTOs exactly
// ============================================================

// ── Auth ────────────────────────────────────────────────────
export interface LoginRequest {
  username: string
  password: string
}

export interface UserResponse {
  id:        number
  username:  string
  email:     string
  fullName:  string      // ← no es "nombre"
  phone:     string
  role:      string      // ← es "role" singular, no "roles[]"
  active:    boolean
  createdAt: string      // LocalDateTime llega como string ISO
}
export interface AuthUser {
  id:       number
  username: string
  email:    string
  fullName: string
  role:     string
}
export interface AuthResponse {
  token:     string
  tokenType: string
  expiresIn: number
  user:      UserResponse
}
// ── Dashboard ───────────────────────────────────────────────
// types.ts o donde tengas la interfaz
// types.ts o donde tengas la interfaz
export interface DashboardStats {
  totalClientes: number;
  totalMascotas: number;
  citasHoy: number;
  ingresosMes: number;
  clientesNuevosEsteMes: number;
  mascotasAtendidas: number;
}

// ── Cliente ─────────────────────────────────────────────────
export interface ClienteResponse {
  id:        number
  fullName:  string    // ← era nombre + apellido
  dni:       string    // ← nuevo campo
  phone:     string    // ← era telefono
  email:     string
  address:   string    // ← era direccion
  petsCount: number    // ← era mascotas
  createdAt: string
}
export interface ClienteRequest {
  fullName:  string
  dni:       string
  phone:     string
  email:     string
  address:   string
}

// ── Mascota ─────────────────────────────────────────────────
export type EspecieMascota = 'PERRO' | 'GATO' | 'AVE' | 'OTRO'

export interface MascotaResponse {
  id: number
  nombre: string
  especie: EspecieMascota
  raza: string
  edad: number
  peso: number
  clienteId: number
  clienteNombre: string
  activo: boolean
}

export interface MascotaRequest {
  nombre: string
  especie: EspecieMascota
  raza: string
  edad: number
  peso: number
  clienteId: number
}

// ── Paginación ──────────────────────────────────────────────
export interface PageResponse<T> {
  content:       T[]
  pageNumber:    number   // ← era "number" antes
  pageSize:      number   // ← era "size" antes
  totalElements: number
  totalPages:    number
  last:          boolean
  first:         boolean
  empty:         boolean
}

// ── Error API ───────────────────────────────────────────────
export interface ApiError {
  timestamp: string
  status: number
  error: string
  message: string
  path: string
}
