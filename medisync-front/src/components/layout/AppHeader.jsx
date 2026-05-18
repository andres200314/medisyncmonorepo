export default function AppHeader({
  view,
  onNavigate,
  token,
  authUser,
  onLogout,
}) {
  const linkBase =
    'text-sm font-medium text-muted-foreground transition-all duration-200 hover:text-accent'

  return (
    <header className="glass sticky top-0 z-40">
      <div className="mx-auto flex h-14 max-w-7xl items-center justify-between gap-4 px-4 sm:px-6">
        <button
          type="button"
          onClick={() => onNavigate('home')}
          className="flex items-center gap-2 font-semibold tracking-tight text-foreground transition-colors duration-200 hover:text-primary focus-visible:outline focus-visible:outline-2 focus-visible:outline-offset-2 focus-visible:outline-ring"
        >
          <span className="flex h-8 w-8 items-center justify-center rounded-lg bg-primary text-xs font-bold text-primary-foreground">
            M
          </span>
          <span className="hidden sm:inline">MediSync</span>
        </button>

        <nav className="flex flex-wrap items-center justify-end gap-x-4 gap-y-2 sm:gap-x-6">
          <button type="button" onClick={() => onNavigate('home')} className={linkBase}>
            Inicio
          </button>

          {token ? (
            <>
              <button
                type="button"
                onClick={() => onNavigate('dashboard')}
                className={
                  view === 'dashboard'
                    ? 'text-sm font-semibold text-accent'
                    : linkBase
                }
              >
                Panel
              </button>
              {authUser?.nombre && (
                <span className="hidden max-w-[10rem] truncate text-sm text-muted-foreground md:inline">
                  {authUser.nombre}
                </span>
              )}
              <button
                type="button"
                onClick={onLogout}
                className="rounded-lg border border-border bg-card px-3 py-1.5 text-sm font-medium text-foreground shadow-sm transition-all duration-200 hover:bg-muted/50"
              >
                Salir
              </button>
            </>
          ) : (
            <>
              <button type="button" onClick={() => onNavigate('login')} className={linkBase}>
                Acceso farmacias
              </button>
              <button
                type="button"
                onClick={() => onNavigate('register')}
                className="rounded-xl bg-primary px-3 py-1.5 text-sm font-semibold text-primary-foreground shadow-sm transition-all duration-200 hover:bg-primary/90"
              >
                Registrar farmacia
              </button>
            </>
          )}
        </nav>
      </div>
    </header>
  )
}
