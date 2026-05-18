import { useAuth } from '../../context/AuthContext.jsx'

const NAVY = '#1E3A8A'

function scrollToHomeSection(id) {
  window.requestAnimationFrame(() => {
    document.getElementById(id)?.scrollIntoView({ behavior: 'smooth', block: 'start' })
  })
}

export default function AppNavbar({ variant = 'default', view, onNavigate, onLogout }) {
  const { isAuthenticated, authUser } = useAuth()

  const isHome = variant === 'home'
  const isDashboard = variant === 'dashboard'

  const headerClass = isHome
    ? 'fixed inset-x-0 top-0 z-50 border-b border-white/10 shadow-sm'
    : 'glass sticky top-0 z-40 border-b border-border/60'

  const headerStyle = isHome ? { backgroundColor: NAVY } : undefined

  const linkBase = isHome
    ? 'text-sm font-medium text-white/90 transition-colors hover:text-white'
    : 'text-sm font-medium text-muted-foreground transition-all duration-200 hover:text-accent'

  const activeLink = isHome
    ? 'text-sm font-semibold text-white'
    : 'text-sm font-semibold text-accent'

  function goHome() {
    onNavigate('home')
  }

  function goHomeSection(sectionId) {
    if (view === 'home') {
      scrollToHomeSection(sectionId)
      return
    }
    onNavigate('home')
    window.setTimeout(() => scrollToHomeSection(sectionId), 50)
  }

  const primaryBtnClass = isHome
    ? 'rounded-xl bg-white px-4 py-2 text-sm font-semibold transition-all duration-200 hover:bg-white/90'
    : 'rounded-xl bg-primary px-3 py-1.5 text-sm font-semibold text-primary-foreground shadow-sm transition-all duration-200 hover:bg-primary/90'

  const primaryBtnStyle = isHome ? { color: NAVY } : undefined

  const outlineBtnClass = isHome
    ? 'rounded-xl border border-white/30 bg-white/10 px-4 py-2 text-sm font-semibold text-white transition-all duration-200 hover:bg-white/20'
    : 'rounded-lg border border-border bg-card px-3 py-1.5 text-sm font-medium text-foreground shadow-sm transition-all duration-200 hover:bg-muted/50'

  return (
    <header className={headerClass} style={headerStyle}>
      <div
        className={`mx-auto flex h-14 items-center justify-between gap-4 px-4 sm:h-16 sm:px-6 ${
          isDashboard ? 'max-w-none' : 'max-w-6xl'
        }`}
      >
        <button
          type="button"
          onClick={() => (isHome ? goHomeSection('hero') : goHome())}
          className={
            isHome
              ? 'flex shrink-0 items-center gap-2 font-bold tracking-tight text-white transition-opacity hover:opacity-90'
              : 'flex items-center gap-2 font-semibold tracking-tight text-foreground transition-colors duration-200 hover:text-primary focus-visible:outline focus-visible:outline-2 focus-visible:outline-offset-2 focus-visible:outline-ring'
          }
        >
          <span
            className={
              isHome
                ? 'flex h-8 w-8 items-center justify-center rounded-lg bg-white/15 text-sm font-bold'
                : 'flex h-8 w-8 items-center justify-center rounded-lg bg-primary text-xs font-bold text-primary-foreground'
            }
          >
            M
          </span>
          <span className={isHome ? 'text-base sm:text-lg' : 'hidden sm:inline'}>MediSync</span>
        </button>

        <nav className="flex flex-wrap items-center justify-end gap-x-3 gap-y-2 sm:gap-x-5">
          {isHome && (
            <div className="hidden items-center gap-6 sm:flex">
              <button type="button" onClick={() => goHomeSection('hero')} className={linkBase}>
                Inicio
              </button>
              <button
                type="button"
                onClick={() => goHomeSection('farmacias-cta')}
                className={linkBase}
              >
                Para farmacias
              </button>
            </div>
          )}

          {!isHome && (
            <button type="button" onClick={goHome} className={view === 'home' ? activeLink : linkBase}>
              Inicio
            </button>
          )}

          {isAuthenticated ? (
            <>
              <button
                type="button"
                onClick={() => onNavigate('dashboard')}
                className={isDashboard || view === 'dashboard' ? activeLink : linkBase}
              >
                {isDashboard ? 'Panel' : 'Ir al dashboard'}
              </button>

              {authUser?.nombre && (
                <span
                  className={
                    isHome
                      ? 'hidden max-w-[11rem] truncate text-sm font-medium text-white/90 md:inline'
                      : 'hidden max-w-[10rem] truncate text-sm text-muted-foreground md:inline'
                  }
                  title={authUser.nombre}
                >
                  {authUser.nombre}
                </span>
              )}

              <button type="button" onClick={onLogout} className={outlineBtnClass}>
                Cerrar sesión
              </button>
            </>
          ) : (
            <>
              {!isHome && (
                <button type="button" onClick={() => onNavigate('login')} className={linkBase}>
                  Acceso farmacias
                </button>
              )}
              <button
                type="button"
                onClick={() => onNavigate(isHome ? 'login' : 'register')}
                className={primaryBtnClass}
                style={primaryBtnStyle}
              >
                {isHome ? 'Acceder' : 'Registrar farmacia'}
              </button>
            </>
          )}
        </nav>
      </div>
    </header>
  )
}
