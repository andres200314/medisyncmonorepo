import { useState } from 'react'
import { API_BASE } from '../config/api.js'
import { useAuth } from '../context/AuthContext.jsx'

/** NIT colombiano: dígitos del número + guion + dígito de verificación (ej. 900123456-1). */
function isValidNit(value) {
  const s = value.trim()
  return /^[0-9]{8,11}-[0-9]$/.test(s)
}

function parseCoordinate(value) {
  const normalized = String(value).trim().replace(',', '.')
  const n = Number(normalized)
  return Number.isFinite(n) ? n : NaN
}

function onlyDigits(str, maxLen) {
  const d = str.replace(/\D/g, '')
  return maxLen ? d.slice(0, maxLen) : d
}

export default function Register({ onNavigate }) {
  const { login } = useAuth()
  const [nombre, setNombre] = useState('')
  const [nit, setNit] = useState('')
  const [direccion, setDireccion] = useState('')
  const [telefono, setTelefono] = useState('')
  const [email, setEmail] = useState('')
  const [password, setPassword] = useState('')
  const [latitud, setLatitud] = useState('')
  const [longitud, setLongitud] = useState('')
  const [loading, setLoading] = useState(false)
  const [error, setError] = useState(null)

  const inputClass =
    'mt-1.5 block w-full rounded-lg border border-border bg-input px-3 py-2.5 text-foreground shadow-sm placeholder:text-muted-foreground focus:border-ring focus:outline-none focus:ring-2 focus:ring-ring/30'

  function validateForm() {
    const n = nombre.trim()
    const nd = nit.trim()
    const dir = direccion.trim()
    const tel = onlyDigits(telefono)
    const em = email.trim()

    if (!n) return 'Indica el nombre de la farmacia.'
    if (!isValidNit(nd))
      return 'El NIT debe tener formato colombiano (ej. 900123456-1): dígitos, guion y dígito de verificación.'
    if (!dir) return 'Indica la dirección.'
    if (tel.length !== 10) return 'El teléfono debe tener 10 dígitos.'
    if (!em) return 'Indica el correo electrónico.'
    if (!password || password.length < 6) return 'La contraseña debe tener al menos 6 caracteres.'

    const lat = parseCoordinate(latitud)
    const lng = parseCoordinate(longitud)
    if (!Number.isFinite(lat) || lat < -90 || lat > 90)
      return 'Latitud inválida (usa un número entre -90 y 90).'
    if (!Number.isFinite(lng) || lng < -180 || lng > 180)
      return 'Longitud inválida (usa un número entre -180 y 180).'

    return null
  }

  function fillLocationFromBrowser() {
    if (!navigator.geolocation) {
      setError('Tu navegador no permite obtener la ubicación.')
      return
    }
    navigator.geolocation.getCurrentPosition(
      (pos) => {
        setError(null)
        setLatitud(String(pos.coords.latitude))
        setLongitud(String(pos.coords.longitude))
      },
      () => {
        setError('No pudimos leer tu ubicación. Revisa los permisos del navegador.')
      },
      { enableHighAccuracy: true, timeout: 12000 },
    )
  }

  async function handleSubmit(e) {
    e.preventDefault()
    setError(null)

    const validationMsg = validateForm()
    if (validationMsg) {
      setError(validationMsg)
      return
    }

    const tel = onlyDigits(telefono)
    const lat = parseCoordinate(latitud)
    const lng = parseCoordinate(longitud)

    setLoading(true)
    try {
      const res = await fetch(`${API_BASE}/api/auth/register`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json', Accept: 'application/json' },
        body: JSON.stringify({
          nombre: nombre.trim(),
          nit: nit.trim(),
          direccion: direccion.trim(),
          telefono: tel,
          email: email.trim(),
          password,
          latitud: lat,
          longitud: lng,
        }),
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
            : 'No pudimos completar el registro. Intenta de nuevo.'
        setError(msg)
        return
      }

      if (!body.token) {
        setError('Respuesta inválida del servidor.')
        return
      }

      login({
        token: body.token,
        email: body.email ?? email.trim(),
        nombre: body.nombre ?? nombre.trim(),
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
            Registra tu farmacia y muestra disponibilidad en tiempo real.
          </h1>
          <p className="mt-4 max-w-sm text-sm leading-relaxed text-primary-foreground/85">
            Un solo formulario para crear tu cuenta de gestor y empezar a conectar con pacientes en
            Medellín.
          </p>
        </div>
        <ul className="relative mt-12 hidden space-y-3 text-sm text-primary-foreground/80 sm:block lg:mt-auto">
          <li className="flex gap-2">
            <span className="mt-1.5 h-1.5 w-1.5 shrink-0 rounded-full bg-white/70" />
            Mayor visibilidad frente a pacientes que buscan medicamentos
          </li>
          <li className="flex gap-2">
            <span className="mt-1.5 h-1.5 w-1.5 shrink-0 rounded-full bg-white/70" />
            Ubicación geográfica para futuras mejoras en mapas y cercanía
          </li>
        </ul>
      </aside>

      {/* Formulario */}
      <div className="flex flex-1 items-start justify-center bg-background px-4 py-10 sm:px-8 lg:py-14">
        <div className="w-full max-w-xl">
          <div className="lg:hidden">
            <p className="text-xs font-semibold uppercase tracking-wide text-accent">
              MediSync · Gestores
            </p>
            <h2 className="mt-2 text-2xl font-semibold tracking-tight text-foreground">
              Registrar farmacia
            </h2>
            <p className="mt-1 text-sm text-muted-foreground">
              Completa los datos de tu droguería o farmacia.
            </p>
          </div>

          <div className="hidden lg:block">
            <h2 className="text-2xl font-semibold tracking-tight text-foreground">
              Registrar farmacia
            </h2>
            <p className="mt-2 text-sm text-muted-foreground">
              Datos de contacto, ubicación y credenciales para acceder al panel.
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
                <label htmlFor="reg-nombre" className="block text-sm font-medium text-foreground">
                  Nombre de la farmacia
                </label>
                <input
                  id="reg-nombre"
                  name="nombre"
                  type="text"
                  autoComplete="organization"
                  value={nombre}
                  onChange={(e) => setNombre(e.target.value)}
                  className={inputClass}
                  placeholder="Ej. Droguería Salud Total"
                  disabled={loading}
                />
              </div>

              <div>
                <label htmlFor="reg-nit" className="block text-sm font-medium text-foreground">
                  NIT
                </label>
                <input
                  id="reg-nit"
                  name="nit"
                  type="text"
                  value={nit}
                  onChange={(e) => setNit(e.target.value)}
                  className={inputClass}
                  placeholder="900123456-1"
                  disabled={loading}
                  spellCheck={false}
                />
                <p className="mt-1 text-xs text-muted-foreground">
                  Formato colombiano: número sin puntos + guion + dígito de verificación.
                </p>
              </div>

              <div>
                <label htmlFor="reg-dir" className="block text-sm font-medium text-foreground">
                  Dirección
                </label>
                <input
                  id="reg-dir"
                  name="direccion"
                  type="text"
                  autoComplete="street-address"
                  value={direccion}
                  onChange={(e) => setDireccion(e.target.value)}
                  className={inputClass}
                  placeholder="Calle, número, barrio"
                  disabled={loading}
                />
              </div>

              <div>
                <label htmlFor="reg-tel" className="block text-sm font-medium text-foreground">
                  Teléfono
                </label>
                <input
                  id="reg-tel"
                  name="telefono"
                  type="tel"
                  inputMode="numeric"
                  autoComplete="tel"
                  maxLength={10}
                  value={telefono}
                  onChange={(e) => setTelefono(onlyDigits(e.target.value, 10))}
                  className={inputClass}
                  placeholder="3001234567"
                  disabled={loading}
                />
                <p className="mt-1 text-xs text-muted-foreground">10 dígitos, sin indicativo de país.</p>
              </div>

              <div>
                <label htmlFor="reg-email" className="block text-sm font-medium text-foreground">
                  Correo electrónico
                </label>
                <input
                  id="reg-email"
                  name="email"
                  type="email"
                  autoComplete="email"
                  value={email}
                  onChange={(e) => setEmail(e.target.value)}
                  className={inputClass}
                  placeholder="nombre@farmacia.com"
                  disabled={loading}
                />
              </div>

              <div>
                <label htmlFor="reg-password" className="block text-sm font-medium text-foreground">
                  Contraseña
                </label>
                <input
                  id="reg-password"
                  name="password"
                  type="password"
                  autoComplete="new-password"
                  value={password}
                  onChange={(e) => setPassword(e.target.value)}
                  className={inputClass}
                  placeholder="Mínimo 6 caracteres"
                  disabled={loading}
                />
              </div>

              <div className="border-t border-border pt-5">
                <p className="text-sm font-medium text-foreground">Ubicación</p>
                <p className="mt-1 text-xs text-muted-foreground">
                  Coordenadas en grados decimales (ej. Medellín ~ 6.25, -75.56). Usa coma o punto.
                </p>
                <div className="mt-4 grid gap-4 sm:grid-cols-2">
                  <div>
                    <label htmlFor="reg-lat" className="block text-sm font-medium text-foreground">
                      Latitud
                    </label>
                    <input
                      id="reg-lat"
                      name="latitud"
                      type="text"
                      inputMode="decimal"
                      value={latitud}
                      onChange={(e) => setLatitud(e.target.value)}
                      className={inputClass}
                      placeholder="6.247638"
                      disabled={loading}
                    />
                  </div>
                  <div>
                    <label htmlFor="reg-lng" className="block text-sm font-medium text-foreground">
                      Longitud
                    </label>
                    <input
                      id="reg-lng"
                      name="longitud"
                      type="text"
                      inputMode="decimal"
                      value={longitud}
                      onChange={(e) => setLongitud(e.target.value)}
                      className={inputClass}
                      placeholder="-75.565815"
                      disabled={loading}
                    />
                  </div>
                </div>
                <button
                  type="button"
                  onClick={fillLocationFromBrowser}
                  disabled={loading}
                  className="mt-3 text-sm font-semibold text-accent hover:underline disabled:opacity-50"
                >
                  Usar mi ubicación actual
                </button>
              </div>
            </div>

            <button
              type="submit"
              disabled={loading}
              className="mt-8 flex h-11 w-full items-center justify-center rounded-lg bg-primary text-sm font-semibold text-primary-foreground shadow-sm transition-all duration-200 hover:bg-primary/90 disabled:cursor-not-allowed disabled:opacity-70"
            >
              {loading ? 'Creando cuenta…' : 'Crear cuenta y entrar'}
            </button>

            <p className="mt-6 text-center text-sm text-muted-foreground">
              ¿Ya tienes cuenta?{' '}
              <button
                type="button"
                onClick={() => onNavigate('login')}
                className="font-semibold text-accent hover:underline"
              >
                Iniciar sesión
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
