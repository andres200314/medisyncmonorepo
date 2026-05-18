import { useState } from 'react'
import { API_BASE } from '../config/api.js'
import { useAuth } from '../context/AuthContext.jsx'

export default function Login({ onNavigate }) {
  const { login } = useAuth()
  const [email, setEmail] = useState('')
  const [password, setPassword] = useState('')
  const [loading, setLoading] = useState(false)
  const [error, setError] = useState(null)

  async function handleSubmit(e) {
    e.preventDefault()
    setError(null)

    const trimmed = email.trim()
    if (!trimmed || !password) {
      setError('Completa el correo y la contraseña.')
      return
    }

    setLoading(true)
    try {
      const res = await fetch(`${API_BASE}/api/auth/login`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json', Accept: 'application/json' },
        body: JSON.stringify({ email: trimmed, password }),
      })

      let body = {}
      try {
        body = await res.json()
      } catch {
        /* ignore */
      }

      if (!res.ok) {
        const msg =
          typeof body.error === 'string'
            ? body.error
            : 'Credenciales incorrectas o servicio no disponible.'
        setError(msg)
        return
      }

      if (!body.token) {
        setError('Respuesta inválida del servidor.')
        return
      }

      login({
        token: body.token,
        email: body.email ?? trimmed,
        nombre: body.nombre ?? '',
        tipo: body.tipo ?? 'Bearer',
      })
      onNavigate('dashboard')
    } catch {
      setError(
        'No hay conexión con el servidor. Verifica que el backend esté activo en localhost:8080.',
      )
    } finally {
      setLoading(false)
    }
  }

  return (
    <div className="flex min-h-[calc(100vh-3.5rem)] flex-col lg:flex-row">
      {/* Branding */}
      <aside className="relative flex flex-col justify-between overflow-hidden bg-primary px-8 py-10 text-primary-foreground lg:w-[42%] lg:max-w-xl lg:shrink-0 lg:py-14 xl:px-14">
        <div className="pointer-events-none absolute inset-0 bg-[radial-gradient(ellipse_90%_80%_at_30%_-10%,rgba(255,255,255,0.18),transparent)]" />
        <div className="relative">
          <div className="flex items-center gap-3">
            <span className="flex h-11 w-11 items-center justify-center rounded-xl bg-white/15 text-lg font-bold backdrop-blur-sm">
              M
            </span>
            <span className="text-xl font-semibold tracking-tight">MediSync</span>
          </div>
          <h1 className="mt-10 max-w-md text-3xl font-semibold leading-tight tracking-tight lg:text-[2rem] lg:leading-snug">
            Gestiona inventario y llega a más pacientes en Medellín.
          </h1>
          <p className="mt-4 max-w-sm text-sm leading-relaxed text-primary-foreground/85">
            Panel exclusivo para farmacias registradas. Actualiza stock en tiempo real y mantén tu
            información al día.
          </p>
        </div>
        <ul className="relative mt-12 hidden space-y-3 text-sm text-primary-foreground/80 sm:block lg:mt-auto">
          <li className="flex gap-2">
            <span className="mt-1.5 h-1.5 w-1.5 shrink-0 rounded-full bg-white/70" />
            Stock sincronizado con la red MediSync
          </li>
          <li className="flex gap-2">
            <span className="mt-1.5 h-1.5 w-1.5 shrink-0 rounded-full bg-white/70" />
            Seguridad con sesión en servidor — sin guardar datos sensibles en el navegador
          </li>
        </ul>
      </aside>

      {/* Formulario */}
      <div className="flex flex-1 items-start justify-center bg-background px-4 py-10 sm:px-8 lg:items-center lg:py-14">
        <div className="w-full max-w-md">
          <div className="lg:hidden">
            <p className="text-xs font-semibold uppercase tracking-wide text-accent">
              MediSync · Gestores
            </p>
            <h2 className="mt-2 text-2xl font-semibold tracking-tight text-foreground">
              Iniciar sesión
            </h2>
            <p className="mt-1 text-sm text-muted-foreground">
              Accede con el correo de tu farmacia.
            </p>
          </div>

          <div className="hidden lg:block">
            <h2 className="text-2xl font-semibold tracking-tight text-foreground">
              Iniciar sesión
            </h2>
            <p className="mt-2 text-sm text-muted-foreground">
              Usa el correo y la contraseña de tu cuenta de farmacia.
            </p>
          </div>

          <form
            onSubmit={handleSubmit}
            className="mt-8 rounded-2xl border border-border bg-card p-6 shadow-[0_1px_2px_rgba(23,43,77,0.08)] sm:p-8"
            noValidate
          >
            {error && (
              <div
                role="alert"
                className="mb-6 rounded-lg border border-destructive/25 bg-destructive/10 px-4 py-3 text-sm text-destructive"
              >
                {error}
              </div>
            )}

            <div className="space-y-5">
              <div>
                <label htmlFor="login-email" className="block text-sm font-medium text-foreground">
                  Correo electrónico
                </label>
                <input
                  id="login-email"
                  name="email"
                  type="email"
                  autoComplete="email"
                  value={email}
                  onChange={(e) => setEmail(e.target.value)}
                  aria-invalid={error ? 'true' : undefined}
                  className="mt-1.5 block w-full rounded-lg border border-border bg-input px-3 py-2.5 text-foreground shadow-sm placeholder:text-muted-foreground focus:border-ring focus:outline-none focus:ring-2 focus:ring-ring/30"
                  placeholder="nombre@farmacia.com"
                  disabled={loading}
                />
              </div>

              <div>
                <label htmlFor="login-password" className="block text-sm font-medium text-foreground">
                  Contraseña
                </label>
                <input
                  id="login-password"
                  name="password"
                  type="password"
                  autoComplete="current-password"
                  value={password}
                  onChange={(e) => setPassword(e.target.value)}
                  aria-invalid={error ? 'true' : undefined}
                  className="mt-1.5 block w-full rounded-lg border border-border bg-input px-3 py-2.5 text-foreground shadow-sm placeholder:text-muted-foreground focus:border-ring focus:outline-none focus:ring-2 focus:ring-ring/30"
                  placeholder="••••••••"
                  disabled={loading}
                />
              </div>
            </div>

            <button
              type="submit"
              disabled={loading}
              className="mt-8 flex h-11 w-full items-center justify-center rounded-lg bg-primary text-sm font-semibold text-primary-foreground shadow-sm transition-all duration-200 hover:bg-primary/90 disabled:cursor-not-allowed disabled:opacity-70"
            >
              {loading ? 'Entrando…' : 'Entrar al panel'}
            </button>

            <p className="mt-6 text-center text-sm text-muted-foreground">
              ¿Tu farmacia no está registrada?{' '}
              <button
                type="button"
                onClick={() => onNavigate('register')}
                className="font-semibold text-accent hover:underline"
              >
                Crear cuenta
              </button>
            </p>

            <button
              type="button"
              onClick={() => onNavigate('home')}
              className="mt-4 w-full text-center text-sm font-medium text-muted-foreground hover:text-accent"
            >
              Volver al inicio
            </button>
          </form>
        </div>
      </div>
    </div>
  )
}
