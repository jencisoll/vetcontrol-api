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
export interface DashboardStats {
  totalClientes: number
  totalMascotas: number
  citasHoy: number
  ingresosMes: number
  clientesNuevosEsteMes: number
  mascotasAtendidas: number
}

// ── Cliente ─────────────────────────────────────────────────
export interface ClienteResponse {
  id: number
  nombre: string
  apellido: string
  email: string
  telefono: string
  direccion: string
  fechaRegistro: string   // ISO date string
  mascotas: number        // count
  activo: boolean
}

export interface ClienteRequest {
  nombre: string
  apellido: string
  email: string
  telefono: string
  direccion: string
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
  content: T[]
  totalElements: number
  totalPages: number
  size: number
  number: number      // current page (0-indexed)
  first: boolean
  last: boolean
}

// ── Error API ───────────────────────────────────────────────
export interface ApiError {
  timestamp: string
  status: number
  error: string
  message: string
  path: string
}
