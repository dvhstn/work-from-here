/**
 * Router state passed from the list to the detail page, so "All spaces" can
 * return to the same filters and page. Arriving directly falls back to `/spaces`.
 */
export interface SpaceLinkState {
  listSearch: string
}

export function spaceLinkState(listSearch: string): SpaceLinkState {
  return { listSearch }
}

/** True when the user reached this page by clicking a card, so the previous history entry is the list. */
export function cameFromList(state: unknown): boolean {
  return (
    typeof state === 'object' &&
    state !== null &&
    'listSearch' in state &&
    typeof (state as { listSearch: unknown }).listSearch === 'string'
  )
}

/** Reads the list search from untrusted router state; only accepts a query string. */
export function listHrefFromState(state: unknown): string {
  if (typeof state === 'object' && state !== null && 'listSearch' in state) {
    const { listSearch } = state as { listSearch: unknown }
    if (typeof listSearch === 'string' && listSearch.startsWith('?')) return `/spaces${listSearch}`
  }
  return '/spaces'
}

/** Route ids must be positive integers; anything else is treated as not found without a request. */
export function parseSpaceId(raw: string | undefined): number | null {
  if (!raw || !/^\d+$/.test(raw)) return null
  const id = Number(raw)
  return Number.isSafeInteger(id) && id > 0 ? id : null
}
