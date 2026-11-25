import axios, { AxiosError } from 'axios'
import { ApiError, isApiErrorBody } from './errors'

/**
 * Shared axios instance. Paths are relative so the Vite dev proxy (and a
 * same-origin deploy) handle routing to the backend.
 */
export const apiClient = axios.create({
  baseURL: '/api/v1',
  headers: { Accept: 'application/json' },
})

apiClient.interceptors.response.use(
  (response) => response,
  (error: unknown) => {
    // Let cancellations through untouched so callers (TanStack Query) can tell them apart.
    if (axios.isCancel(error)) return Promise.reject(error)

    if (error instanceof AxiosError) {
      const response = error.response
      if (!response) {
        return Promise.reject(new ApiError(0, 'Network error'))
      }
      const body = isApiErrorBody(response.data) ? response.data : null
      const message = body?.message ?? (response.statusText || `Request failed with status ${response.status}`)
      return Promise.reject(new ApiError(response.status, message, body))
    }

    return Promise.reject(new ApiError(0, error instanceof Error ? error.message : 'Unknown error'))
  },
)
