export default function PerfilPanel({ user, onLogout }) {
  return (
    <div className="max-w-lg rounded-xl border border-border bg-card p-8 shadow-sm">
      <h2 className="text-lg font-semibold text-foreground">Tu cuenta</h2>
      <p className="mt-1 text-sm text-muted-foreground">
        Información del gestor autenticado en MediSync.
      </p>

      <dl className="mt-8 space-y-5 border-t border-border pt-6">
        <div>
          <dt className="text-xs font-semibold uppercase tracking-wide text-muted-foreground">Farmacia</dt>
          <dd className="mt-1 text-sm font-medium text-foreground">{user?.nombre ?? '—'}</dd>
        </div>
        <div>
          <dt className="text-xs font-semibold uppercase tracking-wide text-muted-foreground">Correo</dt>
          <dd className="mt-1 text-sm font-medium text-foreground">{user?.email ?? '—'}</dd>
        </div>
      </dl>

      <button
        type="button"
        onClick={onLogout}
        className="mt-10 flex h-11 w-full items-center justify-center rounded-lg border border-border text-sm font-semibold text-foreground hover:bg-muted/40"
      >
        Cerrar sesión
      </button>
    </div>
  )
}
