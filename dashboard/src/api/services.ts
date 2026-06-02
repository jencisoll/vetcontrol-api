import apiClient from './apiClient'
import type {
  LoginRequest, LoginResponse,
  DashboardStats,
  ClienteResponse, ClienteRequest, PageResponse,
  MascotaResponse, MascotaRequest,
} from '../types'

// ── Auth ──────────────────────────────────────────────────
export const authApi = {
  login: (data: LoginRequest) =>
    apiClient.post<LoginResponse>('/auth/login', data).then(r => r.data),
}

// ── Dashboard ─────────────────────────────────────────────
export const dashboardApi = {
  getStats: () =>
    apiClient.get<DashboardStats>('/dashboard/stats').then(r => r.data),
}

// ── Clientes ──────────────────────────────────────────────
export const clientesApi = {
  getAll: (page = 0, size = 10) =>
    apiClient
      .get<PageResponse<ClienteResponse>>('/clients', { params: { page, size } })
      .then(r => r.data),

  getById: (id: number) =>
    apiClient.get<ClienteResponse>(`/clients/${id}`).then(r => r.data),

  create: (data: ClienteRequest) =>
    apiClient.post<ClienteResponse>('/clients', data).then(r => r.data),

  update: (id: number, data: ClienteRequest) =>
    apiClient.put<ClienteResponse>(`/clients/${id}`, data).then(r => r.data),

  delete: (id: number) =>
    apiClient.delete(`/clients/${id}`).then(r => r.data),
}

// ── Mascotas ──────────────────────────────────────────────
export const mascotasApi = {
  getAll: (page = 0, size = 10) =>
    apiClient
      .get<PageResponse<MascotaResponse>>('/pets', { params: { page, size } })
      .then(r => r.data),

  getById: (id: number) =>
    apiClient.get<MascotaResponse>(`/pets/${id}`).then(r => r.data),

  create: (data: MascotaRequest) =>
    apiClient.post<MascotaResponse>('/pets', data).then(r => r.data),

  update: (id: number, data: MascotaRequest) =>
    apiClient.put<MascotaResponse>(`/pets/${id}`, data).then(r => r.data),

  delete: (id: number) =>
    apiClient.delete(`/pets/${id}`).then(r => r.data),
}
