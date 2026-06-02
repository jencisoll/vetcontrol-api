import { useQuery } from '@tanstack/react-query'
import { dashboardApi } from '../api/services'
import type { DashboardStats } from '../types'
import { useAuth } from '../auth/useAuth'

interface StatCardProps {
  title: string
  value: string | number
  sub: string
  color: string
}

function StatCard({ title, value, sub, color }: StatCardProps) {
  return (
    <div className="rounded-xl bg-slate-900 border border-slate-800 p-6
                    hover:border-slate-700 transition-colors">
      <p className="mb-4 text-xs font-semibold uppercase tracking-widest text-slate-500">
        {title}
      </p>
      <p className={`text-3xl font-bold tracking-tight ${color}`}>
        {value}
      </p>
      <p className="mt-1 text-xs text-slate-600">{sub}</p>
    </div>
  )
}

function SkeletonCard() {
  return (
    <div className="rounded-xl bg-slate-900 border border-slate-800 p-6 animate-pulse">
      <div className="mb-4 h-3 w-24 rounded bg-slate-800" />
      <div className="h-8 w-16 rounded bg-slate-800" />
      <div className="mt-2 h-3 w-32 rounded bg-slate-800" />
    </div>
  )
}

export default function DashboardPage() {
  const { user } = useAuth()

  const { data: stats, isLoading, isError } = useQuery<DashboardStats>({
    queryKey: ['dashboard-stats'],
    queryFn: dashboardApi.getStats,
    refetchInterval: 60_000,
  })

  const cards = stats
    ? [
        {
          title: 'Total Clientes',
          value: stats.totalClientes.toLocaleString(),
          sub: `+${stats.clientesNuevosEsteMes} este mes`,
          color: 'text-blue-400',
        },
        {
          title: 'Total Mascotas',
          value: stats.totalMascotas.toLocaleString(),
          sub: `${stats.mascotasAtendidas} atendidas`,
          color: 'text-violet-400',
        },
        {
          title: 'Citas Hoy',
          value: stats.citasHoy,
          sub: 'Programadas para hoy',
          color: 'text-emerald-400',
        },
        {
          title: 'Ingresos del Mes',
          value: `S/ ${stats.ingresosMes.toLocaleString()}`,
          sub: 'Mes en curso',
          color: 'text-amber-400',
        },
      ]
    : []

  return (
    <div className="max-w-6xl">

      {/* Header */}
      <div className="mb-8">
        <h1 className="text-2xl font-bold text-white tracking-tight">
          Bienvenido, {user?.fullName?.split(' ')[0] ?? user?.username} 👋
        </h1>
        <p className="mt-1 text-sm text-slate-500">
          Resumen general del sistema — actualización cada 60s
        </p>
      </div>

      {/* Error */}
      {isError && (
        <div className="mb-6 rounded-lg bg-red-950/50 border border-red-900
                        px-4 py-3 text-sm text-red-400">
          ⚠ No se pudieron cargar las estadísticas. Verifica que el backend esté activo
          en <code className="font-mono">localhost:8081</code>.
        </div>
      )}

      {/* Stat cards */}
      <div className="grid grid-cols-1 gap-4 sm:grid-cols-2 xl:grid-cols-4 mb-8">
        {isLoading
          ? Array.from({ length: 4 }).map((_, i) => <SkeletonCard key={i} />)
          : cards.map(c => <StatCard key={c.title} {...c} />)
        }
      </div>

      {/* Quick links */}
      <div className="rounded-xl bg-slate-900 border border-slate-800 p-6">
        <h2 className="mb-4 text-sm font-semibold text-white">Accesos rápidos</h2>
        <div className="grid grid-cols-2 gap-3 sm:grid-cols-3">
          {[
            { label: 'Nuevo Cliente',  href: '/dashboard/clientes', emoji: '👤' },
            { label: 'Nueva Mascota',  href: '/dashboard/mascotas', emoji: '🐾' },
            { label: 'Ver Swagger',    href: 'http://localhost:8080/swagger-ui.html', emoji: '📋', external: true },
          ].map(({ label, href, emoji, external }) => (
            <a
              key={label}
              href={href}
              target={external ? '_blank' : undefined}
              rel={external ? 'noreferrer' : undefined}
              className="flex items-center gap-3 rounded-lg border border-slate-800
                         bg-slate-950 px-4 py-3 text-sm text-slate-400
                         transition-colors hover:border-slate-700 hover:text-white"
            >
              <span>{emoji}</span>
              {label}
              {external && <span className="ml-auto text-slate-700">↗</span>}
            </a>
          ))}
        </div>
      </div>

    </div>
  )
}
