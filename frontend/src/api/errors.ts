import type { ApiErrorBody } from './types'

/**
 * Error thrown by every API call. `status` is the HTTP status, or 0 when the
 * request never got a response (network failure, backend down).
 */
export class ApiError extends Error {
  readonly status: number
  readonly body: ApiErrorBody | null

  constructor(status: number, message: string, body: ApiErrorBody | null = null) {
    super(message)
    this.name = 'ApiError'
    this.status = status
    this.body = body
  }

  /** True when the server couldn't be reached or the dev proxy had no backend. */
  get isUnreachable(): boolean {
    return this.status === 0 || (this.body === null && this.status >= 500)
  }
}

export function isApiErrorBody(value: unknown): value is ApiErrorBody {
  if (typeof value !== 'object' || value === null) return false
  const candidate = value as Record<string, unknown>
  return typeof candidate.status === 'number' && typeof candidate.message === 'string'
}

/** A short, user-facing explanation of why a request failed. */
export function describeError(error: unknown): string {
  if (error instanceof ApiError) {
    if (error.isUnreachable) {
      return "We can't reach the server right now. Check your connection and try again."
    }
    if (error.status >= 500) {
      return 'Something went wrong on our side. Please try again.'
    }
    return error.message
  }
  return 'Something unexpected went wrong. Please try again.'
}
