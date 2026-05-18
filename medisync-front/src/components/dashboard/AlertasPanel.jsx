import { getStockStatus } from '../../utils/stockStatus.js'

export default function AlertasPanel({ rows, loading, fetchError, onActualizarStock }) {
  const alerts = rows.filter((r) => getStockStatus(r.cantidad).key !== 'disponible')

  return (
    <div className="space-y-6">
      {fetchError && (
        <div
          role="alert"
          className="rounded-lg border border-destructive/25 bg-destructive/10 px-4 py-3 text-sm text-destructive"
        >
          {fetchError}
        </div>
      )}

      {!loading && !fetchError && alerts.length === 0 && (
        <div className="rounded-xl border border-border bg-card px-6 py-12 text-center shadow-sm">
          <p className="font-medium text-foreground">Sin alertas por ahora</p>
          <p className="mt-2 text-sm text-muted-foreground">
            Todo tu inventario tiene stock disponible (10 unidades o más).
          </p>
        </div>
      )}

      {!loading && alerts.length > 0 && (
        <ul className="space-y-3">
          {alerts.map((row) => {
            const st = getStockStatus(row.cantidad)
            const esAgotado = st.key === 'agotado'
            return (
              <li
                key={row.medicamentoId}
                className="rounded-xl border border-border bg-card p-5 shadow-sm"
              >
                <div className="flex flex-col gap-4 sm:flex-row sm:items-start sm:justify-between">
                  <div className="min-w-0 flex-1 space-y-3">
                    <div>
                      <p className="font-semibold text-foreground">{row.medicamentoNombre}</p>
                      <p className="mt-1 text-sm leading-relaxed text-muted-foreground">
                        {row.medicamentoDescripcion}
                      </p>
                    </div>
                    <div className="flex flex-wrap items-center gap-3">
                      <p className="text-sm tabular-nums text-foreground">
                        Stock actual:{' '}
                        <span className="font-semibold">{row.cantidad}</span>
                      </p>
                      <span
                        className={`inline-flex rounded-full border px-2.5 py-0.5 text-xs font-semibold ${
                          esAgotado
                            ? 'border-destructive/35 bg-destructive/10 text-destructive'
                            : 'border-warning/35 bg-warning/10 text-warning'
                        }`}
                      >
                        {st.label}
                      </span>
                    </div>
                  </div>
                  <div className="flex shrink-0 justify-end sm:pt-0">
                    <button
                      type="button"
                      onClick={() => onActualizarStock(row.medicamentoId)}
                      className="inline-flex h-9 items-center justify-center rounded-lg border border-border bg-card px-4 text-sm font-semibold text-accent shadow-sm transition-all duration-200 hover:bg-primary hover:text-primary-foreground"
                    >
                      Actualizar stock
                    </button>
                  </div>
                </div>
              </li>
            )
          })}
        </ul>
      )}

      {loading && (
        <p className="text-sm text-muted-foreground">Cargando datos del inventario…</p>
      )}
    </div>
  )
}
