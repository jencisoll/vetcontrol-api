import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom'
import { QueryClient, QueryClientProvider } from '@tanstack/react-query'

import { AuthProvider }     from './auth/AuthProvider'
import { ProtectedRoute }   from './router/ProtectedRoute'
import DashboardLayout      from './components/layout/DashboardLayout'

import LoginPage      from './pages/LoginPage'
import DashboardPage  from './pages/DashboardPage'
import ClientesPage   from './pages/ClientesPage'
import MascotasPage   from './pages/MascotasPage'

const queryClient = new QueryClient({
  defaultOptions: {
    queries: {
      staleTime: 30_000,       // 30s before refetch
      retry: 1,
      refetchOnWindowFocus: false,
    },
  },
})

export default function App() {
  return (
    <QueryClientProvider client={queryClient}>
      <AuthProvider>
        <BrowserRouter>
          <Routes>

            {/* Public */}
            <Route path="/login" element={<LoginPage />} />

            {/* Protected — requires JWT */}
            <Route element={<ProtectedRoute />}>
              <Route element={<DashboardLayout />}>
                <Route path="/dashboard"            element={<DashboardPage />} />
                <Route path="/dashboard/clientes"   element={<ClientesPage />} />
                <Route path="/dashboard/mascotas"   element={<MascotasPage />} />
              </Route>
            </Route>

            {/* Fallback */}
            <Route path="*" element={<Navigate to="/login" replace />} />

          </Routes>
        </BrowserRouter>
      </AuthProvider>
    </QueryClientProvider>
  )
}
