import { useCallback, useEffect, useMemo, useState } from 'react'
import { API_BASE } from '../config/api.js'
import { useAuth } from '../context/AuthContext.jsx'
import InventarioPanel from '../components/dashboard/InventarioPanel.jsx'
import AlertasPanel from '../components/dashboard/AlertasPanel.jsx'
import PerfilPanel from '../components/dashboard/PerfilPanel.jsx'
import { getStockStatus } from '../utils/stockStatus.js'

const SECTIONS = [
  { id: 'inventario', label: 'Inventario' },
  { id: 'alertas', label: 'Alertas' },
  { id: 'perfil', label: 'Perfil' },
]

export default function Dashboard({ onLogout }) {
  const { token, authUser } = useAuth()
  const user = { nombre: authUser?.nombre, email: authUser?.email }
  const [section, setSection] = useState('inventario')
  const [stockModalMedicamentoId, setStockModalMedicamentoId] = useState(null)
  const [rows, setRows] = useState([])
  const [inventoryLoading, setInventoryLoading] = useState(true)
  const [inventoryError, setInventoryError] = useState(null)

  const loadInventory = useCallback(async () => {
    setInventoryLoading(true)
    setInventoryError(null)
    try {
      const headers = { Authorization: `Bearer ${token}`, Accept: 'application/json' }
      const invRes = await fetch(`${API_BASE}/api/inventario/mi-inventario`, { headers })

      let body = {}
      try {
        body = await invRes.json()
      } catch {
        /* ignore */
      }

      if (!invRes.ok) {
        const msg =
          typeof body.error === 'string'
            ? body.error
            : 'No se pudo cargar tu inventario. Verifica tu sesión.'
        throw new Error(msg)
      }

      const catRes = await fetch(`${API_BASE}/api/medicamentos`)
      let catalog = []
      if (catRes.ok) {
        try {
          catalog = await catRes.json()
        } catch {
          catalog = []
        }
      }

      const descById = {}
      for (const m of catalog) {
        if (m?.id) descById[m.id] = m.descripcion ?? ''
      }

      const merged = (body.items ?? []).map((item) => ({
        ...item,
        medicamentoDescripcion: descById[item.medicamentoId] ?? '—',
      }))

      setRows(merged)
    } catch (e) {
      setRows([])
      setInventoryError(e instanceof Error ? e.message : 'Error de red.')
    } finally {
      setInventoryLoading(false)
    }
  }, [token])

  useEffect(() => {
    loadInventory()
  }, [loadInventory])

  const alertCount = useMemo(
    () => rows.filter((r) => getStockStatus(r.cantidad).key !== 'disponible').length,
    [rows],
  )

  const sectionMeta = SECTIONS.find((s) => s.id === section)

  const clearStockModalIntent = useCallback(() => {
    setStockModalMedicamentoId(null)
  }, [])

  function navigateSection(next) {
    setSection(next)
    if (next !== 'inventario') {
      setStockModalMedicamentoId(null)
    }
  }

  function handleActualizarStockDesdeAlerta(medicamentoId) {
    setSection('inventario')
    setStockModalMedicamentoId(medicamentoId)
  }

  return (
    <div className="flex min-h-0 flex-1 w-full max-w-none flex-col overflow-hidden lg:flex-row">
      <aside className="hidden h-full min-h-0 w-64 shrink-0 flex-col border-r border-sidebar-border bg-sidebar text-sidebar-foreground lg:flex">
        <div className="flex min-h-0 flex-1 flex-col overflow-y-auto p-4 pt-6">
          <p className="mb-3 px-3 text-xs font-semibold uppercase tracking-wide text-sidebar-foreground/70">
            Panel gestor
          </p>
          <nav className="space-y-1">
            {SECTIONS.map((s) => (
              <button
                key={s.id}
                type="button"
                onClick={() => navigateSection(s.id)}
                className={`flex w-full items-center gap-2 rounded-lg px-3 py-2 text-left text-sm font-medium transition-all duration-200 ${
                  section === s.id
                    ? 'bg-sidebar-accent text-sidebar-foreground'
                    : 'text-sidebar-foreground/90 hover:bg-sidebar-accent/80'
                }`}
              >
                <span className="flex-1">{s.label}</span>
                {s.id === 'alertas' && alertCount > 0 && (
                  <span className="min-w-[1.25rem] rounded-full bg-white/20 px-1.5 py-0.5 text-center text-xs font-semibold text-sidebar-foreground">
                    {alertCount > 99 ? '99+' : alertCount}
                  </span>
                )}
              </button>
            ))}
          </nav>
        </div>
      </aside>

      <div className="shrink-0 border-b border-border bg-card px-4 py-3 lg:hidden">
        <label htmlFor="dash-section" className="sr-only">
          Sección del panel
        </label>
        <select
          id="dash-section"
          value={section}
          onChange={(e) => navigateSection(e.target.value)}
          className="w-full rounded-lg border border-border bg-white px-3 py-2 text-sm font-medium text-foreground focus:border-ring focus:outline-none focus:ring-2 focus:ring-ring/30"
        >
          {SECTIONS.map((s) => (
            <option key={s.id} value={s.id}>
              {s.id === 'alertas' && alertCount > 0
                ? `${s.label} (${alertCount})`
                : s.label}
            </option>
          ))}
        </select>
      </div>

      <main className="min-h-0 flex-1 overflow-y-auto bg-background px-4 py-4 sm:px-6 sm:py-6">
        <h1 className="text-xl font-semibold tracking-tight text-foreground">
          {sectionMeta?.label ?? 'Panel'}
        </h1>
        <p className="mt-1 text-sm text-muted-foreground">
          {section === 'inventario' &&
            'Consulta y actualiza el stock de los medicamentos en tu farmacia.'}
          {section === 'alertas' &&
            'Medicamentos agotados o con stock bajo que requieren atención.'}
          {section === 'perfil' && 'Datos de tu cuenta y cierre de sesión.'}
        </p>

        <div className="mt-8">
          {section === 'inventario' && (
            <InventarioPanel
              token={token}
              rows={rows}
              loading={inventoryLoading}
              fetchError={inventoryError}
              onReload={loadInventory}
              stockModalMedicamentoId={stockModalMedicamentoId}
              onStockModalMedicamentoIdConsumed={clearStockModalIntent}
            />
          )}
          {section === 'alertas' && (
            <AlertasPanel
              rows={rows}
              loading={inventoryLoading}
              fetchError={inventoryError}
              onActualizarStock={handleActualizarStockDesdeAlerta}
            />
          )}
          {section === 'perfil' && <PerfilPanel user={user} onLogout={onLogout} />}
        </div>
      </main>
    </div>
  )
}
