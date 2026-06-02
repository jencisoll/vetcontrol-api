import {
  createContext,
  useCallback,
  useEffect,
  useMemo,
  useState,
  type ReactNode,
} from 'react'
import { authApi } from '../api/services'
import type { AuthUser, LoginRequest } from '../types'

// ── Context shape ─────────────────────────────────────────
export interface AuthContextValue {
  user: AuthUser | null
  token: string | null
  isLoading: boolean
  login: (credentials: LoginRequest) => Promise<void>
  logout: () => void
  isAuthenticated: boolean
}

export const AuthContext = createContext<AuthContextValue | null>(null)

// ── Provider ──────────────────────────────────────────────
export function AuthProvider({ children }: { children: ReactNode }) {
  const [user,      setUser]      = useState<AuthUser | null>(null)
  const [token,     setToken]     = useState<string | null>(null)
  const [isLoading, setIsLoading] = useState(true)   // true while hydrating

  // Hydrate from localStorage on mount
  useEffect(() => {
    try {
      const savedToken = localStorage.getItem('jwt_token')
      const savedUser  = localStorage.getItem('auth_user')
      if (savedToken && savedUser) {
        setToken(savedToken)
        setUser(JSON.parse(savedUser) as AuthUser)
      }
    } catch {
      localStorage.removeItem('jwt_token')
      localStorage.removeItem('auth_user')
    } finally {
      setIsLoading(false)
    }
  }, [])

  const login = useCallback(async (credentials: LoginRequest) => {
    const response = await authApi.login(credentials)

    // ✅ Mapeo correcto con los campos reales de Java
    const authUser: AuthUser = {
      id:       response.user.id,
      username: response.user.username,
      email:    response.user.email,
      fullName: response.user.fullName,  // ← fullName, no nombre
      role:     response.user.role,      // ← role singular
    }

    localStorage.setItem('jwt_token', response.token)
    localStorage.setItem('auth_user', JSON.stringify(authUser))

    setToken(response.token)
    setUser(authUser)
  }, [])

  const logout = useCallback(() => {
    localStorage.removeItem('jwt_token')
    localStorage.removeItem('auth_user')
    setToken(null)
    setUser(null)
  }, [])

  const value = useMemo<AuthContextValue>(
    () => ({
      user,
      token,
      isLoading,
      login,
      logout,
      isAuthenticated: !!token && !!user,
    }),
    [user, token, isLoading, login, logout]
  )

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>
}
