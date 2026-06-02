import { NavLink, useLocation } from 'react-router-dom'
import { useAuth } from '../../auth/useAuth'

interface NavItem {
  to: string
  label: string
  icon: string
}

const NAV_ITEMS: NavItem[] = [
  { to: '/dashboard',          label: 'Dashboard',  icon: '▦'  },
  { to: '/dashboard/clientes', label: 'Clientes',   icon: '👤' },
  { to: '/dashboard/mascotas', label: 'Mascotas',   icon: '🐾' },
]

export default function Sidebar() {
  const { user, logout } = useAuth()
  const location = useLocation()

  return (
      <aside className="fixed inset-y-0 left-0 z-30 flex w-60 flex-col
                       bg-slate-900 border-r border-slate-800">

        {/* Logo */}
        <div className="flex h-16 items-center gap-3 border-b border-slate-800 px-5">
          <div className="flex h-8 w-8 items-center justify-center rounded-lg
                        bg-blue-600 text-white font-bold text-sm select-none">V</div>
          <div>
            <p className="text-sm font-semibold text-white leading-tight">VetAdmin</p>
            <p className="text-xs text-slate-500">Spring Boot 3</p>
          </div>
        </div>

        {/* Nav */}
        <nav className="flex-1 overflow-y-auto px-3 py-4 space-y-1">
          <p className="mb-2 px-2 text-xs font-semibold text-slate-600 uppercase tracking-widest">
            Menú
          </p>
          {NAV_ITEMS.map(({ to, label, icon }) => {
            const isActive =
                to === '/dashboard'
                    ? location.pathname === '/dashboard'
                    : location.pathname.startsWith(to)

            return (
                <NavLink
                    key={to}
                    to={to}
                    end={to === '/dashboard'}
                    className={`flex items-center gap-3 rounded-lg px-3 py-2.5
                         text-sm font-medium transition-colors
                         ${isActive
                        ? 'bg-blue-600/15 text-blue-400 border border-blue-500/20'
                        : 'text-slate-400 hover:text-white hover:bg-slate-800'
                    }`}
                >
                  <span className="text-base leading-none">{icon}</span>
                  {label}
                  {isActive && (
                      <span className="ml-auto h-1.5 w-1.5 rounded-full bg-blue-400" />
                  )}
                </NavLink>
            )
          })}
        </nav>

        {/* User footer */}
        <div className="border-t border-slate-800 p-4">
          <div className="mb-3 flex items-center gap-3">
            <div className="flex h-8 w-8 items-center justify-center rounded-full
                          bg-gradient-to-br from-blue-600 to-violet-600
                          text-white text-xs font-bold uppercase">
              {/* ✅ fullName en vez de nombre */}
              {user?.fullName?.charAt(0).toUpperCase() ?? 'U'}
            </div>
            <div className="min-w-0">
              {/* ✅ fullName en vez de nombre */}
              <p className="truncate text-sm font-medium text-white">
                {user?.fullName ?? user?.username ?? 'Usuario'}
              </p>
              <p className="truncate text-xs text-slate-500">{user?.email ?? ''}</p>
            </div>
          </div>
          <button
              onClick={logout}
              className="w-full rounded-lg px-3 py-2 text-left text-xs
                     font-medium text-slate-500 transition-colors
                     hover:bg-slate-800 hover:text-red-400"
          >
            ⬡ Cerrar sesión
          </button>
        </div>

      </aside>
  )
}