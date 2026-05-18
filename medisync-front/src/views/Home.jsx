import { useCallback, useEffect, useMemo, useRef, useState } from 'react'
import { API_BASE } from '../config/api.js'
import { useAuth } from '../context/AuthContext.jsx'
import { getStockStatus } from '../utils/stockStatus.js'

const NAVY = '#1E3A8A'
const ACCENT_SKY = '#60A5FA'
const SEARCH_DEBOUNCE_MS = 350
const SUGGESTED_SEARCHES = ['Ibuprofeno', 'Amoxicilina', 'Metformina']

function formatCop(value) {
  if (value == null || Number.isNaN(Number(value))) return '—'
  return new Intl.NumberFormat('es-CO', {
    style: 'currency',
    currency: 'COP',
    maximumFractionDigits: 0,
  }).format(Number(value))
}

function stockTone(key) {
  if (key === 'agotado') return 'text-destructive'
  if (key === 'bajo') return 'text-warning'
  return 'text-success'
}

function stockBadgeClass(key) {
  if (key === 'agotado') return 'border-destructive/30 bg-destructive/10 text-destructive'
  if (key === 'bajo') return 'border-warning/35 bg-warning/10 text-warning'
  return 'border-success/35 bg-success/10 text-success'
}

function SearchIcon({ className }) {
  return (
    <svg
      xmlns="http://www.w3.org/2000/svg"
      viewBox="0 0 24 24"
      fill="none"
      stroke="currentColor"
      strokeWidth="2"
      strokeLinecap="round"
      strokeLinejoin="round"
      aria-hidden
      className={className}
    >
      <circle cx="11" cy="11" r="8" />
      <path d="m21 21-4.3-4.3" />
    </svg>
  )
}

function StepIcon({ type }) {
  const cls = 'h-7 w-7 text-primary'
  if (type === 'search') {
    return (
      <svg className={cls} viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" aria-hidden>
        <circle cx="11" cy="11" r="8" />
        <path d="m21 21-4.3-4.3" />
      </svg>
    )
  }
  if (type === 'store') {
    return (
      <svg className={cls} viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" aria-hidden>
        <path d="M3 9l9-7 9 7v11a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2z" />
        <polyline points="9 22 9 12 15 12 15 22" />
      </svg>
    )
  }
  return (
    <svg className={cls} viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" aria-hidden>
      <path d="M22 11.08V12a10 10 0 1 1-5.93-9.14" />
      <polyline points="22 4 12 14.01 9 11.01" />
    </svg>
  )
}

function flattenAvailability(results) {
  const cards = []
  for (const row of results) {
    for (const item of row.items ?? []) {
      cards.push({
        key: `${row.gestorId}-${item.medicamentoId}`,
        gestorNombre: row.gestorNombre,
        gestorDireccion: row.gestorDireccion,
        latitud: row.latitud,
        longitud: row.longitud,
        medicamentoNombre: item.medicamentoNombre,
        cantidad: item.cantidad,
        precioUnitario: item.precioUnitario,
      })
    }
  }
  return cards
}

function mapsUrl(lat, lng) {
  if (lat == null || lng == null || Number.isNaN(Number(lat)) || Number.isNaN(Number(lng))) {
    return null
  }
  return `https://www.google.com/maps?q=${encodeURIComponent(lat)},${encodeURIComponent(lng)}`
}

function ResultCardsList({ cards, searchedTerm }) {
  if (cards.length === 0) {
    return (
      <div className="rounded-xl border border-border bg-card px-6 py-12 text-center shadow-sm">
        <p className="text-lg font-semibold text-foreground">
          No encontramos «{searchedTerm}» en farmacias por ahora
        </p>
        <p className="mt-2 text-sm text-muted-foreground">
          Prueba con otro nombre o vuelve más tarde cuando las farmacias actualicen su inventario.
        </p>
      </div>
    )
  }

  return (
    <>
      <p className="mb-6 text-center text-sm font-medium text-muted-foreground">
        {cards.length} resultado{cards.length !== 1 ? 's' : ''} para «{searchedTerm}»
      </p>
      <ul className="space-y-4">
        {cards.map((card) => {
          const st = getStockStatus(card.cantidad)
          const mapLink = mapsUrl(card.latitud, card.longitud)
          return (
            <li
              key={card.key}
              className="rounded-xl border border-border bg-card p-5 shadow-sm transition-shadow duration-200 hover:shadow-md"
            >
              <div className="flex flex-wrap items-start justify-between gap-2">
                <div>
                  <p className="font-bold text-foreground">{card.gestorNombre}</p>
                  {card.gestorDireccion ? (
                    <p className="mt-0.5 text-sm text-muted-foreground">{card.gestorDireccion}</p>
                  ) : (
                    <p className="mt-0.5 text-sm text-muted-foreground">Medellín, Colombia</p>
                  )}
                  {mapLink && (
                    <p className="mt-1 text-xs text-muted-foreground">
                      <a
                        href={mapLink}
                        target="_blank"
                        rel="noopener noreferrer"
                        className="font-medium text-accent hover:underline"
                      >
                        Ver en mapa
                      </a>
                      <span className="mx-1.5 text-border">·</span>
                      <span className="tabular-nums">
                        {Number(card.latitud).toFixed(5)}, {Number(card.longitud).toFixed(5)}
                      </span>
                    </p>
                  )}
                </div>
                <span
                  className={`inline-flex rounded-full border px-2.5 py-0.5 text-xs font-semibold ${stockBadgeClass(st.key)}`}
                >
                  {st.label}
                </span>
              </div>
              <div className="mt-4 flex flex-wrap items-end justify-between gap-2 border-t border-border pt-4">
                <p className="font-medium text-foreground">{card.medicamentoNombre}</p>
                <div className="text-right text-sm">
                  <p className={`font-bold tabular-nums ${stockTone(st.key)}`}>
                    Stock: {card.cantidad}
                  </p>
                  <p className="mt-0.5 font-semibold text-foreground">
                    {formatCop(card.precioUnitario)}
                  </p>
                </div>
              </div>
            </li>
          )
        })}
      </ul>
    </>
  )
}

export default function Home({ onNavigate, onLogout }) {
  const { isAuthenticated, authUser } = useAuth()
  const [query, setQuery] = useState('')
  const [results, setResults] = useState(null)
  const [searchedTerm, setSearchedTerm] = useState('')
  const [loading, setLoading] = useState(false)
  const [error, setError] = useState(null)
  const skipDebounceRef = useRef(false)

  const fetchAvailability = useCallback(async (term, signal) => {
    const params = new URLSearchParams({ medicamento: term })
    const res = await fetch(`${API_BASE}/api/inventario/disponibilidad?${params}`, { signal })
    if (!res.ok) throw new Error('No pudimos consultar la disponibilidad.')
    const data = await res.json()
    return Array.isArray(data) ? data : []
  }, [])

  const executeSearch = useCallback(
    async (rawTerm, { signal, scrollToResults = false } = {}) => {
      const term = rawTerm.trim()
      if (!term) {
        setError('Escribe el nombre de un medicamento.')
        setResults(null)
        setSearchedTerm('')
        return
      }

      setLoading(true)
      setError(null)

      try {
        const data = await fetchAvailability(term, signal)
        if (signal?.aborted) return
        setResults(data)
        setSearchedTerm(term)
        if (scrollToResults) {
          window.requestAnimationFrame(() => {
            document.getElementById('resultados')?.scrollIntoView({ behavior: 'smooth', block: 'start' })
          })
        }
      } catch (e) {
        if (e instanceof Error && e.name === 'AbortError') return
        setError(
          'No hay conexión con el servidor o hubo un error. ¿Está corriendo el backend en localhost:8080?',
        )
        setResults(null)
        setSearchedTerm(term)
        if (scrollToResults) {
          window.requestAnimationFrame(() => {
            document.getElementById('resultados')?.scrollIntoView({ behavior: 'smooth', block: 'start' })
          })
        }
      } finally {
        if (!signal?.aborted) setLoading(false)
      }
    },
    [fetchAvailability],
  )

  useEffect(() => {
    const term = query.trim()
    if (!term) {
      setResults(null)
      setSearchedTerm('')
      setError(null)
      setLoading(false)
      return
    }

    if (skipDebounceRef.current) {
      skipDebounceRef.current = false
      return
    }

    const controller = new AbortController()
    const timer = window.setTimeout(() => {
      executeSearch(term, { signal: controller.signal })
    }, SEARCH_DEBOUNCE_MS)

    return () => {
      window.clearTimeout(timer)
      controller.abort()
    }
  }, [query, executeSearch])

  async function handleSearchSubmit(e) {
    e.preventDefault()
    skipDebounceRef.current = true
    setQuery(query.trim())
    await executeSearch(query, { scrollToResults: true })
  }

  function handleSuggestedSearch(term) {
    skipDebounceRef.current = true
    setQuery(term)
    executeSearch(term, { scrollToResults: true })
  }

  const resultCards = useMemo(
    () => (results ? flattenAvailability(results) : []),
    [results],
  )

  const showResults = searchedTerm && results !== null && !error
  const showResultsSection = showResults || (searchedTerm && loading)

  function scrollToId(id) {
    document.getElementById(id)?.scrollIntoView({ behavior: 'smooth', block: 'start' })
  }

  return (
    <div className="bg-white">
      <section
        id="hero"
        className="relative h-[100vh] min-h-[100dvh] w-full overflow-hidden bg-gradient-to-b from-[#0F172A] to-[#1E3A8A] text-white"
      >
        <div className="hero-dots-pattern pointer-events-none absolute inset-0 opacity-80" />
        <div className="hero-grid-pattern pointer-events-none absolute inset-0 opacity-60" />
        <div className="pointer-events-none absolute inset-0 bg-[radial-gradient(ellipse_80%_60%_at_50%_20%,rgba(59,130,246,0.18),transparent_55%)]" />

        <div className="relative z-10 flex h-full flex-col">
          <div className="flex flex-1 flex-col items-center justify-center px-4 pb-24 pt-16 sm:px-6 sm:pt-20">
            <div className="w-full max-w-3xl text-center">
              <span className="inline-flex items-center rounded-full border border-white/25 bg-white/15 px-4 py-1.5 text-sm font-medium text-white backdrop-blur-sm">
                Disponible en Medellín, Colombia
              </span>

              <h1 className="mt-6 text-5xl font-bold leading-[1.08] tracking-tight md:text-7xl">
                Encuentra tu{' '}
                <span style={{ color: ACCENT_SKY }}>medicamento</span>
                <br className="hidden sm:block" />
                {' '}en segundos
              </h1>
              <p className="mx-auto mt-6 max-w-xl text-base leading-relaxed text-white/80 sm:text-lg">
                Consulta disponibilidad en farmacias de Medellín en tiempo real
              </p>

              <form onSubmit={handleSearchSubmit} className="mx-auto mt-10 w-full max-w-[600px]">
                <label htmlFor="hero-search" className="sr-only">
                  Buscar medicamento
                </label>
                <div className="flex flex-col gap-3 shadow-2xl sm:flex-row sm:items-stretch sm:rounded-2xl">
                  <div className="relative min-h-[3.75rem] flex-1 sm:min-h-[4rem]">
                    <span className="pointer-events-none absolute left-5 top-1/2 -translate-y-1/2 text-slate-400">
                      <SearchIcon className="h-5 w-5 sm:h-6 sm:w-6" />
                    </span>
                    <input
                      id="hero-search"
                      type="search"
                      value={query}
                      onChange={(e) => setQuery(e.target.value)}
                      placeholder="Buscar medicamento..."
                      autoComplete="off"
                      className="h-full w-full rounded-2xl border-0 bg-white py-4 pl-14 pr-5 text-base text-foreground placeholder:text-slate-400 focus:outline-none focus:ring-2 focus:ring-[#3B82F6] sm:rounded-r-none sm:py-5 sm:text-lg"
                    />
                  </div>
                  <button
                    type="submit"
                    disabled={loading}
                    className="inline-flex min-h-[3.75rem] shrink-0 items-center justify-center rounded-2xl bg-[#3B82F6] px-10 text-base font-semibold text-white transition-all duration-200 hover:bg-[#2563EB] disabled:opacity-70 sm:min-h-[4rem] sm:rounded-l-none sm:px-12 sm:text-lg"
                  >
                    {loading ? 'Buscando…' : 'Buscar'}
                  </button>
                </div>
              </form>

              <div className="mx-auto mt-5 flex max-w-[600px] flex-wrap items-center justify-center gap-2">
                <span className="text-xs font-medium text-white/60 sm:text-sm">Sugerencias:</span>
                {SUGGESTED_SEARCHES.map((term) => (
                  <button
                    key={term}
                    type="button"
                    onClick={() => handleSuggestedSearch(term)}
                    disabled={loading}
                    className="rounded-full border border-white/25 bg-white/10 px-4 py-1.5 text-sm font-medium text-white backdrop-blur-sm transition-all duration-200 hover:border-white/40 hover:bg-white/20 disabled:opacity-60"
                  >
                    {term}
                  </button>
                ))}
              </div>

              {error && (
                <div
                  role="alert"
                  className="mx-auto mt-6 max-w-[600px] rounded-xl border border-red-400/30 bg-red-500/10 px-4 py-3 text-sm text-red-100"
                >
                  {error}
                </div>
              )}
            </div>
          </div>

          <button
            type="button"
            onClick={() => scrollToId('como-funciona')}
            className="absolute bottom-8 left-1/2 z-20 flex -translate-x-1/2 flex-col items-center gap-1 text-white/70 transition-colors hover:text-white"
            aria-label="Ver cómo funciona"
          >
            <span className="text-xs font-medium tracking-wide">Explorar</span>
            <svg
              className="h-6 w-6 animate-bounce"
              viewBox="0 0 24 24"
              fill="none"
              stroke="currentColor"
              strokeWidth="2"
              strokeLinecap="round"
              strokeLinejoin="round"
              aria-hidden
            >
              <path d="M12 5v14M19 12l-7 7-7-7" />
            </svg>
          </button>
        </div>
      </section>

      {showResultsSection && (
        <section
          id="resultados"
          className="scroll-mt-16 border-b border-border bg-background px-4 py-12 sm:px-6 sm:py-16"
        >
          <div className="mx-auto max-w-2xl">
            {loading && !showResults && (
              <p className="text-center text-sm text-muted-foreground">Buscando disponibilidad…</p>
            )}
            {showResults && (
              <>
                {loading && (
                  <p className="mb-4 text-center text-sm text-muted-foreground">
                    Actualizando resultados…
                  </p>
                )}
                <ResultCardsList cards={resultCards} searchedTerm={searchedTerm} />
              </>
            )}
          </div>
        </section>
      )}

      <section id="como-funciona" className="scroll-mt-16 bg-white px-4 py-20 sm:px-6 sm:py-28">
        <div className="mx-auto max-w-6xl">
          <div className="mx-auto max-w-2xl text-center">
            <h2 className="text-3xl font-bold tracking-tight text-foreground sm:text-4xl">
              Cómo funciona
            </h2>
            <p className="mt-4 text-base text-muted-foreground">
              Tres pasos simples para encontrar lo que necesitas sin llamadas ni filas.
            </p>
          </div>

          <div className="mt-16 grid gap-8 md:grid-cols-3">
            {[
              {
                icon: 'search',
                title: 'Busca tu medicamento',
                body: 'Escribe el nombre y consulta al instante qué farmacias tienen stock disponible.',
              },
              {
                icon: 'store',
                title: 'Compara farmacias',
                body: 'Revisa precio, cantidad y ubicación de cada opción en Medellín.',
              },
              {
                icon: 'check',
                title: 'Ve con confianza',
                body: 'Acude a la farmacia sabiendo que el medicamento está disponible.',
              },
            ].map((step) => (
              <div
                key={step.title}
                className="rounded-xl border border-border bg-white p-8 shadow-sm transition-shadow duration-200 hover:shadow-md"
              >
                <div className="flex h-12 w-12 items-center justify-center rounded-xl bg-secondary">
                  <StepIcon type={step.icon} />
                </div>
                <h3 className="mt-6 text-lg font-bold text-foreground">{step.title}</h3>
                <p className="mt-3 text-sm leading-relaxed text-muted-foreground">{step.body}</p>
              </div>
            ))}
          </div>
        </div>
      </section>

      <section className="px-4 py-20 sm:px-6 sm:py-28" style={{ backgroundColor: NAVY }}>
        <div className="mx-auto max-w-6xl">
          <div className="mx-auto max-w-2xl text-center">
            <h2 className="text-3xl font-bold tracking-tight text-white sm:text-4xl">
              MediSync en números
            </h2>
            <p className="mt-4 text-base text-white/70">
              Una red pensada para pacientes y farmacias en la ciudad.
            </p>
          </div>

          <div className="mt-16 grid gap-10 sm:grid-cols-3">
            {[
              { value: '+120', label: 'Consultas diarias de disponibilidad' },
              { value: '24/7', label: 'Consulta cuando lo necesites' },
              { value: '100%', label: 'Enfoque en Medellín' },
            ].map((stat) => (
              <div key={stat.label} className="text-center">
                <p className="text-4xl font-bold tracking-tight text-white sm:text-5xl">
                  {stat.value}
                </p>
                <p className="mt-3 text-sm font-medium text-white/80">{stat.label}</p>
              </div>
            ))}
          </div>
        </div>
      </section>

      <section
        id="farmacias-cta"
        className="scroll-mt-16 border-t border-border bg-slate-50 px-4 py-20 sm:px-6 sm:py-28"
      >
        <div className="mx-auto max-w-2xl text-center">
          {isAuthenticated ? (
            <>
              <h2 className="text-3xl font-bold tracking-tight text-foreground sm:text-4xl">
                Hola, {authUser?.nombre || 'farmacia'}
              </h2>
              <p className="mt-4 text-base text-muted-foreground">
                Tu sesión está activa. Gestiona inventario y alertas desde el panel de gestor.
              </p>
              {authUser?.email && (
                <p className="mt-2 text-sm text-muted-foreground">{authUser.email}</p>
              )}
              <div className="mt-10 flex flex-col items-center justify-center gap-3 sm:flex-row">
                <button
                  type="button"
                  onClick={() => onNavigate('dashboard')}
                  className="inline-flex h-12 w-full items-center justify-center rounded-xl px-8 text-base font-semibold text-white shadow-md transition-all duration-200 hover:opacity-90 sm:w-auto"
                  style={{ backgroundColor: NAVY }}
                >
                  Ir al dashboard
                </button>
                <button
                  type="button"
                  onClick={onLogout}
                  className="inline-flex h-12 w-full items-center justify-center rounded-xl border border-border bg-white px-8 text-base font-semibold text-foreground shadow-sm transition-all duration-200 hover:bg-muted/40 sm:w-auto"
                >
                  Cerrar sesión
                </button>
              </div>
            </>
          ) : (
            <>
              <h2 className="text-3xl font-bold tracking-tight text-foreground sm:text-4xl">
                ¿Eres una farmacia?
              </h2>
              <p className="mt-4 text-base text-muted-foreground">
                Únete a MediSync, publica tu inventario en tiempo real y llega a más pacientes en
                Medellín.
              </p>
              <button
                type="button"
                onClick={() => onNavigate('register')}
                className="mt-10 inline-flex h-12 items-center justify-center rounded-xl px-8 text-base font-semibold text-white shadow-md transition-all duration-200 hover:opacity-90"
                style={{ backgroundColor: NAVY }}
              >
                Registrar mi farmacia
              </button>
              <button
                type="button"
                onClick={() => onNavigate('login')}
                className="mt-4 block w-full text-sm font-semibold text-accent hover:underline sm:ml-4 sm:inline sm:w-auto"
              >
                Ya tengo cuenta
              </button>
            </>
          )}
        </div>
      </section>

      <footer className="border-t border-border bg-white px-4 py-8 sm:px-6">
        <div className="mx-auto flex max-w-6xl flex-col items-center justify-between gap-4 text-center text-sm text-muted-foreground sm:flex-row sm:text-left">
          <p>© {new Date().getFullYear()} MediSync. Todos los derechos reservados.</p>
          <p className="text-xs">Disponibilidad de medicamentos en farmacias de Medellín.</p>
        </div>
      </footer>
    </div>
  )
}
