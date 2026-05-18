import { createContext, useCallback, useContext, useEffect, useMemo, useState } from 'react'

const STORAGE_KEY = 'medisync_session'

const AuthContext = createContext(null)

function readStoredSession() {
  try {
    const raw = sessionStorage.getItem(STORAGE_KEY)
    if (!raw) return null
    const data = JSON.parse(raw)
    if (data?.token && data?.authUser?.email != null) return data
  } catch {
    /* ignore corrupt storage */
  }
  return null
}

function writeStoredSession(session) {
  try {
    if (session?.token && session?.authUser) {
      sessionStorage.setItem(STORAGE_KEY, JSON.stringify(session))
    } else {
      sessionStorage.removeItem(STORAGE_KEY)
    }
  } catch {
    /* ignore quota / private mode */
  }
}

export function AuthProvider({ children }) {
  const [token, setToken] = useState(null)
  const [authUser, setAuthUser] = useState(null)
  const [isHydrated, setIsHydrated] = useState(false)

  useEffect(() => {
    const stored = readStoredSession()
    if (stored) {
      setToken(stored.token)
      setAuthUser(stored.authUser)
    }
    setIsHydrated(true)
  }, [])

  useEffect(() => {
    if (!isHydrated) return
    if (token && authUser) {
      writeStoredSession({ token, authUser })
    } else {
      writeStoredSession(null)
    }
  }, [token, authUser, isHydrated])

  const login = useCallback(({ token: nextToken, email, nombre, tipo }) => {
    setToken(nextToken)
    setAuthUser({ email, nombre: nombre ?? '', tipo: tipo ?? 'Bearer' })
  }, [])

  const logout = useCallback(() => {
    setToken(null)
    setAuthUser(null)
  }, [])

  const value = useMemo(
    () => ({
      token,
      authUser,
      isAuthenticated: Boolean(token && authUser),
      isHydrated,
      login,
      logout,
    }),
    [token, authUser, isHydrated, login, logout],
  )

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>
}

export function useAuth() {
  const ctx = useContext(AuthContext)
  if (!ctx) {
    throw new Error('useAuth debe usarse dentro de AuthProvider')
  }
  return ctx
}
