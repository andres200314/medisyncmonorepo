/**
 * Vacío en Docker/proxy: las peticiones van a /api en el mismo origen (nginx o Vite).
 * En desarrollo directo: VITE_API_BASE_URL=http://localhost:8080 (.env.development).
 */
export const API_BASE = (import.meta.env.VITE_API_BASE_URL ?? '').replace(/\/$/, '')
