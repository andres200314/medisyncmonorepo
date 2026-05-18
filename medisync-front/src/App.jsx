import { useEffect, useState } from 'react'
import AppNavbar from './components/layout/AppNavbar.jsx'
import { AuthProvider, useAuth } from './context/AuthContext.jsx'
import Home from './views/Home.jsx'
import Login from './views/Login.jsx'
import Register from './views/Register.jsx'
import Dashboard from './views/Dashboard.jsx'

function AppContent() {
  const [view, setView] = useState('home')
  const { isAuthenticated, isHydrated, logout } = useAuth()

  useEffect(() => {
    if (!isHydrated) return
    if (view === 'dashboard' && !isAuthenticated) {
      setView('home')
    }
  }, [view, isAuthenticated, isHydrated])

  useEffect(() => {
    if (!isHydrated) return
    if ((view === 'login' || view === 'register') && isAuthenticated) {
      setView('dashboard')
    }
  }, [view, isAuthenticated, isHydrated])

  function navigate(next) {
    setView(next)
  }

  function handleLogout() {
    logout()
    setView('home')
  }

  const navbarVariant =
    view === 'home' ? 'home' : view === 'dashboard' ? 'dashboard' : 'default'

  if (!isHydrated) {
    return (
      <div className="flex min-h-screen items-center justify-center bg-background text-sm text-muted-foreground">
        Cargando sesión…
      </div>
    )
  }

  return (
    <div className="min-h-screen bg-background font-sans text-foreground antialiased">
      <AppNavbar
        variant={navbarVariant}
        view={view}
        onNavigate={navigate}
        onLogout={handleLogout}
      />

      <main
        className={
          view === 'dashboard'
            ? 'flex h-[calc(100svh-3.5rem)] min-h-0 w-full flex-col overflow-hidden sm:h-[calc(100svh-4rem)]'
            : undefined
        }
      >
        {view === 'home' && <Home onNavigate={navigate} onLogout={handleLogout} />}
        {view === 'login' && <Login onNavigate={navigate} />}
        {view === 'register' && <Register onNavigate={navigate} />}
        {view === 'dashboard' && isAuthenticated && (
          <Dashboard onLogout={handleLogout} />
        )}
      </main>
    </div>
  )
}

export default function App() {
  return (
    <AuthProvider>
      <AppContent />
    </AuthProvider>
  )
}
