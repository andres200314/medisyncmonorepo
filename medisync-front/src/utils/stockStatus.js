/** @param {number} stock */
export function getStockStatus(stock) {
  if (stock === 0) return { key: 'agotado', label: 'Agotado' }
  if (stock < 10) return { key: 'bajo', label: 'Bajo' }
  return { key: 'disponible', label: 'Disponible' }
}
