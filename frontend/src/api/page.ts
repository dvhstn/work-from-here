import type { Paged, SpringPage, SpringPagedModel } from './types'

/**
 * Normalises either Spring page serialisation into `Paged<T>`, so a switch
 * to `PagedModel` on the backend doesn't ripple through the UI.
 */
export function toPaged<T>(raw: SpringPage<T> | SpringPagedModel<T>): Paged<T> {
  const meta = 'page' in raw ? raw.page : raw
  return {
    items: raw.content,
    page: meta.number,
    size: meta.size,
    totalElements: meta.totalElements,
    totalPages: meta.totalPages,
  }
}
