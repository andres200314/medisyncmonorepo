import { useEffect, useMemo, useState } from 'react'
import { API_BASE } from '../../config/api.js'
import { getStockStatus } from '../../utils/stockStatus.js'

function authHeaders(token, json = false) {
  const h = { Authorization: `Bearer ${token}`, Accept: 'application/json' }
  if (json) h['Content-Type'] = 'application/json'
  return h
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

function formatCop(value) {
  if (value == null || Number.isNaN(Number(value))) return '—'
  return new Intl.NumberFormat('es-CO', {
    style: 'currency',
    currency: 'COP',
    maximumFractionDigits: 0,
  }).format(Number(value))
}

function StatusBadge({ stock }) {
  const s = getStockStatus(stock)
  const map = {
    agotado: 'border-destructive/35 bg-destructive/10 text-destructive',
    bajo: 'border-warning/35 bg-warning/10 text-warning',
    disponible: 'border-success/35 bg-success/10 text-success',
  }
  return (
    <span
      className={`inline-flex rounded-full border px-2.5 py-0.5 text-xs font-semibold ${map[s.key]}`}
    >
      {s.label}
    </span>
  )
}

function ModalShell({ title, children, onClose, footer }) {
  useEffect(() => {
    function onKey(e) {
      if (e.key === 'Escape') onClose()
    }
    window.addEventListener('keydown', onKey)
    return () => window.removeEventListener('keydown', onKey)
  }, [onClose])

  return (
    <div
      className="fixed inset-0 z-50 flex items-center justify-center bg-foreground/40 p-4 backdrop-blur-[1px]"
      role="presentation"
      onMouseDown={(e) => {
        if (e.target === e.currentTarget) onClose()
      }}
    >
      <div
        role="dialog"
        aria-modal="true"
        aria-labelledby="modal-title"
        className="max-h-[90vh] w-full max-w-lg overflow-y-auto rounded-2xl border border-border bg-card shadow-xl"
        onMouseDown={(e) => e.stopPropagation()}
      >
        <div className="border-b border-border px-6 py-4">
          <h2 id="modal-title" className="text-lg font-semibold text-foreground">
            {title}
          </h2>
        </div>
        <div className="px-6 py-5">{children}</div>
        {footer && <div className="border-t border-border px-6 py-4">{footer}</div>}
      </div>
    </div>
  )
}

export default function InventarioPanel({
  token,
  rows,
  loading,
  fetchError,
  onReload,
  stockModalMedicamentoId,
  onStockModalMedicamentoIdConsumed,
}) {
  const [addOpen, setAddOpen] = useState(false)
  const [adjustRow, setAdjustRow] = useState(null)
  const [inventorySearch, setInventorySearch] = useState('')

  const searchNorm = inventorySearch.trim().toLowerCase()
  const filteredRows = useMemo(() => {
    if (!searchNorm) return rows
    return rows.filter((r) => {
      const nom = String(r.medicamentoNombre ?? '').toLowerCase()
      const desc = String(r.medicamentoDescripcion ?? '').toLowerCase()
      return nom.includes(searchNorm) || desc.includes(searchNorm)
    })
  }, [rows, searchNorm])

  useEffect(() => {
    if (!stockModalMedicamentoId || loading) return
    const row = rows.find(
      (r) => String(r.medicamentoId) === String(stockModalMedicamentoId),
    )
    if (row) {
      setAdjustRow(row)
      window.setTimeout(() => {
        document
          .getElementById(`inv-row-${stockModalMedicamentoId}`)
          ?.scrollIntoView({ behavior: 'smooth', block: 'center' })
      }, 120)
    }
    onStockModalMedicamentoIdConsumed?.()
  }, [stockModalMedicamentoId, loading, rows, onStockModalMedicamentoIdConsumed])

  const inputClass =
    'mt-1.5 block w-full rounded-lg border border-border bg-input px-3 py-2 text-sm text-foreground shadow-sm placeholder:text-muted-foreground focus:border-ring focus:outline-none focus:ring-2 focus:ring-ring/30'

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

      <div className="flex flex-col gap-3 sm:flex-row sm:items-center sm:justify-between">
        <p className="text-sm text-muted-foreground">
          {loading
            ? 'Cargando inventario…'
            : searchNorm
              ? `${filteredRows.length} de ${rows.length} medicamento(s) con la búsqueda actual.`
              : `${rows.length} medicamento(s) en tu inventario.`}
        </p>
        <button
          type="button"
          onClick={() => setAddOpen(true)}
          disabled={loading || !!fetchError}
          className="inline-flex h-10 items-center justify-center rounded-lg bg-primary px-4 text-sm font-semibold text-primary-foreground shadow-sm transition-all duration-200 hover:bg-primary/90 disabled:cursor-not-allowed disabled:opacity-60"
        >
          Agregar medicamento
        </button>
      </div>

      <div className="relative">
        <label htmlFor="inv-search" className="sr-only">
          Buscar en inventario
        </label>
        <span
          className="pointer-events-none absolute left-3 top-1/2 -translate-y-1/2 text-muted-foreground"
          aria-hidden
        >
          <SearchIcon className="h-4 w-4 shrink-0" />
        </span>
        <input
          id="inv-search"
          type="search"
          value={inventorySearch}
          onChange={(e) => setInventorySearch(e.target.value)}
          autoComplete="off"
          placeholder="Buscar por nombre o descripción…"
          disabled={loading || !!fetchError}
          className="w-full rounded-lg border border-border bg-white py-2.5 pl-10 pr-4 text-sm text-foreground shadow-sm placeholder:text-muted-foreground focus:border-ring focus:outline-none focus:ring-2 focus:ring-ring/30 disabled:opacity-60"
        />
      </div>

      <div className="overflow-hidden rounded-xl border border-border bg-card shadow-sm">
        <div className="overflow-x-auto">
          <table className="w-full min-w-[720px] text-left text-sm">
            <thead className="border-b border-border bg-muted/50">
              <tr>
                {['Medicamento', 'Descripción', 'Stock', 'Precio', 'Estado', 'Acciones'].map(
                  (col) => (
                    <th
                      key={col}
                      className="whitespace-nowrap px-4 py-3 text-xs font-semibold uppercase tracking-wide text-muted-foreground"
                    >
                      {col}
                    </th>
                  ),
                )}
              </tr>
            </thead>
            <tbody className="divide-y divide-border">
              {!loading && rows.length === 0 && !fetchError && (
                <tr>
                  <td colSpan={6} className="px-4 py-12 text-center text-muted-foreground">
                    Aún no tienes medicamentos. Usa «Agregar medicamento» para empezar.
                  </td>
                </tr>
              )}
              {!loading && rows.length > 0 && filteredRows.length === 0 && (
                <tr>
                  <td colSpan={6} className="px-4 py-12 text-center text-muted-foreground">
                    Ningún medicamento coincide con «{inventorySearch.trim()}». Prueba con otro texto.
                  </td>
                </tr>
              )}
              {filteredRows.map((row) => (
                <tr key={row.medicamentoId} id={`inv-row-${row.medicamentoId}`} className="hover:bg-muted/30">
                  <td className="whitespace-nowrap px-4 py-3 font-medium text-foreground">
                    {row.medicamentoNombre}
                  </td>
                  <td className="max-w-[220px] px-4 py-3 text-muted-foreground">
                    <span className="line-clamp-2">{row.medicamentoDescripcion}</span>
                  </td>
                  <td className="whitespace-nowrap px-4 py-3 tabular-nums text-foreground">
                    {row.cantidad}
                  </td>
                  <td className="whitespace-nowrap px-4 py-3 tabular-nums text-foreground">
                    {formatCop(row.precioUnitario)}
                  </td>
                  <td className="whitespace-nowrap px-4 py-3">
                    <StatusBadge stock={row.cantidad} />
                  </td>
                  <td className="whitespace-nowrap px-4 py-3">
                    <div className="flex flex-wrap gap-2">
                      <button
                        type="button"
                        onClick={() => setAdjustRow(row)}
                        className="rounded-md border border-border px-2.5 py-1 text-xs font-semibold text-accent hover:bg-muted/40"
                      >
                        Editar
                      </button>
                      <button
                        type="button"
                        onClick={() => handleDeleteMedicamento(row, token, onReload)}
                        className="rounded-md border border-destructive/30 px-2.5 py-1 text-xs font-semibold text-destructive hover:bg-destructive/10"
                      >
                        Quitar
                      </button>
                    </div>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      </div>

      {addOpen && (
        <AddMedicamentoModal
          token={token}
          inputClass={inputClass}
          onClose={() => setAddOpen(false)}
          onSaved={() => {
            setAddOpen(false)
            onReload()
          }}
        />
      )}
      {adjustRow && (
        <EditInventarioItemModal
          token={token}
          row={adjustRow}
          inputClass={inputClass}
          onClose={() => setAdjustRow(null)}
          onSaved={() => {
            setAdjustRow(null)
            onReload()
          }}
        />
      )}
    </div>
  )
}

function EditInventarioItemModal({ token, row, inputClass, onClose, onSaved }) {
  const [delta, setDelta] = useState('')
  const [precio, setPrecio] = useState('')
  const [busy, setBusy] = useState(false)
  const [err, setErr] = useState(null)

  async function parseError(res) {
    let body = {}
    try {
      body = await res.json()
    } catch {
      /* ignore */
    }
    return typeof body.error === 'string' ? body.error : null
  }

  async function submit(e) {
    e.preventDefault()
    setErr(null)

    const deltaTrimmed = String(delta).trim()
    const precioTrimmed = String(precio).trim()
    const hasDelta = deltaTrimmed !== ''
    const hasPrecio = precioTrimmed !== ''

    if (!hasDelta && !hasPrecio) {
      setErr('Indica un ajuste de stock, un nuevo precio, o ambos.')
      return
    }

    let stockDelta = null
    if (hasDelta) {
      stockDelta = Number.parseInt(deltaTrimmed, 10)
      if (!Number.isFinite(stockDelta) || !Number.isInteger(stockDelta)) {
        setErr('El ajuste de stock debe ser un número entero.')
        return
      }
      if (stockDelta === 0) {
        setErr('El ajuste de stock no puede ser cero.')
        return
      }
    }

    let nuevoPrecio = null
    if (hasPrecio) {
      nuevoPrecio = Number.parseFloat(precioTrimmed.replace(',', '.'))
      if (!Number.isFinite(nuevoPrecio) || nuevoPrecio < 0) {
        setErr('El precio unitario no es válido.')
        return
      }
      const actual = Number(row.precioUnitario)
      if (Number.isFinite(actual) && nuevoPrecio === actual) {
        setErr('El precio es igual al actual. Déjalo vacío si no quieres cambiarlo.')
        return
      }
    }

    setBusy(true)
    try {
      if (hasDelta) {
        const stockUrl = `${API_BASE}/api/inventario/medicamentos/${row.medicamentoId}/stock/ajustar?cantidad=${encodeURIComponent(stockDelta)}`
        const stockRes = await fetch(stockUrl, {
          method: 'PATCH',
          headers: authHeaders(token),
        })
        if (!stockRes.ok) {
          setErr((await parseError(stockRes)) ?? 'No se pudo ajustar el stock.')
          return
        }
      }

      if (hasPrecio) {
        const precioUrl = `${API_BASE}/api/inventario/medicamentos/${row.medicamentoId}/precio?precioUnitario=${encodeURIComponent(nuevoPrecio)}`
        const precioRes = await fetch(precioUrl, {
          method: 'PATCH',
          headers: authHeaders(token),
        })
        if (!precioRes.ok) {
          setErr((await parseError(precioRes)) ?? 'No se pudo actualizar el precio.')
          return
        }
      }

      onSaved()
    } catch {
      setErr('Error de red.')
    } finally {
      setBusy(false)
    }
  }

  const footer = (
    <div className="flex justify-end gap-2">
      <button
        type="button"
        onClick={onClose}
        disabled={busy}
        className="rounded-lg border border-border px-4 py-2 text-sm font-medium text-foreground hover:bg-muted/40 disabled:opacity-50"
      >
        Cancelar
      </button>
      <button
        type="submit"
        form="edit-inventario-form"
        disabled={busy}
        className="rounded-lg bg-primary px-4 py-2 text-sm font-semibold text-primary-foreground transition-all duration-200 hover:bg-primary/90 disabled:opacity-70"
      >
        {busy ? 'Guardando…' : 'Guardar cambios'}
      </button>
    </div>
  )

  return (
    <ModalShell title="Editar medicamento" onClose={onClose} footer={footer}>
      <form id="edit-inventario-form" onSubmit={submit} className="space-y-5">
        {err && (
          <div className="rounded-lg border border-destructive/25 bg-destructive/10 px-3 py-2 text-sm text-destructive">
            {err}
          </div>
        )}
        <p className="text-sm text-muted-foreground">
          <span className="font-medium text-foreground">{row.medicamentoNombre}</span>
        </p>

        <div className="rounded-lg border border-border bg-muted/20 p-4">
          <p className="text-xs font-semibold uppercase tracking-wide text-muted-foreground">
            Stock
          </p>
          <p className="mt-1 text-sm text-muted-foreground">
            Actual: <span className="font-semibold text-foreground">{row.cantidad}</span>
          </p>
          <div className="mt-3">
            <label htmlFor="adj-delta" className="block text-sm font-medium text-foreground">
              Ajuste de unidades (opcional)
            </label>
            <input
              id="adj-delta"
              type="number"
              step={1}
              value={delta}
              onChange={(e) => setDelta(e.target.value)}
              className={inputClass}
              placeholder="Ej. 10 o -5"
              disabled={busy}
            />
            <p className="mt-1 text-xs text-muted-foreground">
              Positivo suma; negativo resta. Déjalo vacío si no cambias el stock.
            </p>
          </div>
        </div>

        <div className="rounded-lg border border-border bg-muted/20 p-4">
          <p className="text-xs font-semibold uppercase tracking-wide text-muted-foreground">
            Precio
          </p>
          <p className="mt-1 text-sm text-muted-foreground">
            Actual:{' '}
            <span className="font-semibold text-foreground">{formatCop(row.precioUnitario)}</span>
          </p>
          <div className="mt-3">
            <label htmlFor="adj-precio" className="block text-sm font-medium text-foreground">
              Nuevo precio unitario COP (opcional)
            </label>
            <input
              id="adj-precio"
              type="number"
              min={0}
              step={1}
              value={precio}
              onChange={(e) => setPrecio(e.target.value)}
              className={inputClass}
              placeholder="Ej. 12500"
              disabled={busy}
            />
          </div>
        </div>
      </form>
    </ModalShell>
  )
}

function handleDeleteMedicamento(row, token, onReload) {
  const ok = window.confirm(
    `¿Quitar «${row.medicamentoNombre}» de tu inventario?\n\n` +
      'El medicamento seguirá existiendo en el catálogo para otras farmacias.',
  )
  if (!ok) return

  ;(async () => {
    try {
      const res = await fetch(`${API_BASE}/api/inventario/medicamentos/${row.medicamentoId}`, {
        method: 'DELETE',
        headers: authHeaders(token),
      })
      if (!res.ok) {
        let body = {}
        try {
          body = await res.json()
        } catch {
          /* ignore */
        }
        window.alert(typeof body.error === 'string' ? body.error : 'No se pudo quitar del inventario.')
        return
      }
      onReload()
    } catch {
      window.alert('Error de red al quitar del inventario.')
    }
  })()
}

function AddMedicamentoModal({ token, inputClass, onClose, onSaved }) {
  const [nombre, setNombre] = useState('')
  const [requiereFormula, setRequiereFormula] = useState(false)
  const [descripcion, setDescripcion] = useState('')
  const [cantidadInicial, setCantidadInicial] = useState('')
  const [precioUnitario, setPrecioUnitario] = useState('')
  const [busy, setBusy] = useState(false)
  const [err, setErr] = useState(null)

  async function submit(e) {
    e.preventDefault()
    setErr(null)

    const n = nombre.trim()
    const qty = Number.parseInt(String(cantidadInicial).trim(), 10)
    const price = Number.parseFloat(String(precioUnitario).trim().replace(',', '.'))

    if (!n) {
      setErr('Indica el nombre del medicamento.')
      return
    }
    if (!Number.isFinite(qty) || qty < 0 || !Number.isInteger(qty)) {
      setErr('Cantidad inicial debe ser un entero mayor o igual a 0.')
      return
    }
    if (!Number.isFinite(price) || price < 0) {
      setErr('Precio unitario inválido.')
      return
    }

    setBusy(true)
    try {
      const res = await fetch(`${API_BASE}/api/medicamentos`, {
        method: 'POST',
        headers: authHeaders(token, true),
        body: JSON.stringify({
          nombre: n,
          requiereFormula,
          descripcion: descripcion.trim() || '',
          cantidadInicial: qty,
          precioUnitario: price,
        }),
      })

      let body = {}
      try {
        body = await res.json()
      } catch {
        /* ignore */
      }

      if (!res.ok) {
        setErr(typeof body.error === 'string' ? body.error : 'No se pudo agregar el medicamento.')
        return
      }

      onSaved()
    } catch {
      setErr('Error de red. Intenta de nuevo.')
    } finally {
      setBusy(false)
    }
  }

  const footer = (
    <div className="flex justify-end gap-2">
      <button
        type="button"
        onClick={onClose}
        disabled={busy}
        className="rounded-lg border border-border px-4 py-2 text-sm font-medium text-foreground hover:bg-muted/40 disabled:opacity-50"
      >
        Cancelar
      </button>
      <button
        type="submit"
        form="add-med-form"
        disabled={busy}
        className="rounded-lg bg-primary px-4 py-2 text-sm font-semibold text-primary-foreground transition-all duration-200 hover:bg-primary/90 disabled:opacity-70"
      >
        {busy ? 'Guardando…' : 'Guardar'}
      </button>
    </div>
  )

  return (
    <ModalShell title="Agregar medicamento" onClose={onClose} footer={footer}>
      <form id="add-med-form" onSubmit={submit} className="space-y-4">
        {err && (
          <div className="rounded-lg border border-destructive/25 bg-destructive/10 px-3 py-2 text-sm text-destructive">
            {err}
          </div>
        )}
        <div>
          <label htmlFor="am-nombre" className="block text-sm font-medium text-foreground">
            Nombre
          </label>
          <input
            id="am-nombre"
            value={nombre}
            onChange={(e) => setNombre(e.target.value)}
            className={inputClass}
            disabled={busy}
          />
        </div>
        <label className="flex cursor-pointer items-center gap-2 text-sm text-foreground">
          <input
            type="checkbox"
            checked={requiereFormula}
            onChange={(e) => setRequiereFormula(e.target.checked)}
            disabled={busy}
            className="h-4 w-4 rounded border-border text-primary focus:ring-2 focus:ring-ring/40 focus:ring-offset-0"
          />
          Requiere fórmula médica
        </label>
        <div>
          <label htmlFor="am-desc" className="block text-sm font-medium text-foreground">
            Descripción
          </label>
          <textarea
            id="am-desc"
            rows={3}
            value={descripcion}
            onChange={(e) => setDescripcion(e.target.value)}
            className={inputClass}
            disabled={busy}
          />
        </div>
        <div className="grid gap-4 sm:grid-cols-2">
          <div>
            <label htmlFor="am-qty" className="block text-sm font-medium text-foreground">
              Cantidad inicial
            </label>
            <input
              id="am-qty"
              type="number"
              min={0}
              step={1}
              value={cantidadInicial}
              onChange={(e) => setCantidadInicial(e.target.value)}
              className={inputClass}
              disabled={busy}
            />
          </div>
          <div>
            <label htmlFor="am-price" className="block text-sm font-medium text-foreground">
              Precio unitario (COP)
            </label>
            <input
              id="am-price"
              type="number"
              min={0}
              step={1}
              value={precioUnitario}
              onChange={(e) => setPrecioUnitario(e.target.value)}
              className={inputClass}
              disabled={busy}
            />
          </div>
        </div>
        <p className="text-xs text-muted-foreground">
          Si el nombre ya existe en MediSync (misma escritura sin distinguir mayúsculas), se sumará al
          inventario usando ese medicamento; si no, se creará en catálogo y se agregará a tu inventario.
        </p>
      </form>
    </ModalShell>
  )
}
