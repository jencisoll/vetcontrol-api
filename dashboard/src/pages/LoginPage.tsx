import { useState, type FormEvent } from 'react'
import { useNavigate } from 'react-router-dom'
import { useAuth } from '../auth/useAuth'

export default function LoginPage() {
  const { login } = useAuth()
  const navigate  = useNavigate()

  const [username, setUsername] = useState('')
  const [password, setPassword] = useState('')
  const [error,    setError]    = useState<string | null>(null)
  const [loading,  setLoading]  = useState(false)

  async function handleSubmit(e: FormEvent) {
    e.preventDefault()
    setError(null)

    if (!username.trim() || !password.trim()) {
      setError('Completa todos los campos.')
      return
    }

    setLoading(true)
    try {
      await login({ username: username.trim(), password })
      navigate('/dashboard', { replace: true })
    } catch {
      setError('Credenciales incorrectas. Verifica tu usuario y contraseña.')
    } finally {
      setLoading(false)
    }
  }

  return (
      <div className="flex min-h-screen bg-slate-950">

        {/* Left panel */}
        <div className="hidden lg:flex lg:w-1/2 flex-col justify-between p-12
                      bg-gradient-to-br from-slate-900 via-blue-950/40 to-slate-900
                      border-r border-slate-800">
          <div className="flex items-center gap-3">
            <div className="flex h-9 w-9 items-center justify-center rounded-lg
                          bg-blue-600 text-white font-bold text-sm">V</div>
            <span className="text-white font-semibold tracking-tight">VetAdmin</span>
          </div>
          <div>
            <blockquote className="text-2xl font-light text-slate-300 leading-relaxed mb-6">
              "Gestión veterinaria inteligente,<br />
              <span className="text-blue-400 font-medium">al alcance de un clic.</span>"
            </blockquote>
            <div className="flex gap-4">
              {['Clientes', 'Mascotas', 'Citas', 'Reportes'].map(item => (
                  <span key={item}
                        className="text-xs px-3 py-1.5 rounded-full bg-slate-800
                           text-slate-400 border border-slate-700">
                {item}
              </span>
              ))}
            </div>
          </div>
          <p className="text-slate-600 text-sm">
            © 2025 VetAdmin · Spring Boot 3 + React
          </p>
        </div>

        {/* Right panel */}
        <div className="flex flex-1 items-center justify-center px-6">
          <div className="w-full max-w-sm">

            <div className="mb-8 flex items-center gap-3 lg:hidden">
              <div className="flex h-9 w-9 items-center justify-center rounded-lg
                            bg-blue-600 text-white font-bold text-sm">V</div>
              <span className="text-white font-semibold">VetAdmin</span>
            </div>

            <h1 className="mb-1 text-2xl font-bold text-white tracking-tight">
              Iniciar sesión
            </h1>
            <p className="mb-8 text-sm text-slate-500">
              Panel administrativo — acceso restringido
            </p>

            <form onSubmit={handleSubmit} className="space-y-4">

              {/* ✅ Usuario — no email */}
              <div className="space-y-1.5">
                <label className="block text-xs font-medium text-slate-400 uppercase tracking-wider">
                  Usuario
                </label>
                <input
                    type="text"
                    value={username}
                    onChange={e => setUsername(e.target.value)}
                    placeholder="admin"
                    autoComplete="username"
                    className="w-full rounded-lg bg-slate-800 border border-slate-700
                           px-4 py-2.5 text-sm text-white placeholder-slate-600
                           focus:border-blue-500 focus:outline-none focus:ring-1
                           focus:ring-blue-500 transition-colors"
                />
              </div>

              {/* Contraseña */}
              <div className="space-y-1.5">
                <label className="block text-xs font-medium text-slate-400 uppercase tracking-wider">
                  Contraseña
                </label>
                <input
                    type="password"
                    value={password}
                    onChange={e => setPassword(e.target.value)}
                    placeholder="••••••••"
                    autoComplete="current-password"
                    className="w-full rounded-lg bg-slate-800 border border-slate-700
                           px-4 py-2.5 text-sm text-white placeholder-slate-600
                           focus:border-blue-500 focus:outline-none focus:ring-1
                           focus:ring-blue-500 transition-colors"
                />
              </div>

              {error && (
                  <div className="rounded-lg bg-red-950/50 border border-red-900
                              px-4 py-3 text-sm text-red-400">
                    {error}
                  </div>
              )}

              <button
                  type="submit"
                  disabled={loading}
                  className="w-full rounded-lg bg-blue-600 px-4 py-2.5 text-sm
                         font-semibold text-white transition-all
                         hover:bg-blue-500 active:scale-[0.98]
                         disabled:opacity-50 disabled:cursor-not-allowed
                         focus:outline-none focus:ring-2 focus:ring-blue-500
                         focus:ring-offset-2 focus:ring-offset-slate-950"
              >
                {loading ? (
                    <span className="flex items-center justify-center gap-2">
                  <span className="h-4 w-4 animate-spin rounded-full
                                   border-2 border-white/30 border-t-white" />
                  Verificando...
                </span>
                ) : (
                    'Entrar al panel'
                )}
              </button>

            </form>

            {/* ✅ Puerto 8081 */}
            <p className="mt-8 text-center text-xs text-slate-600">
              Conectado a{' '}
              <code className="text-slate-500">localhost:8081/api/v1</code>
            </p>
          </div>
        </div>

      </div>
  )
}