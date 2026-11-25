import type { SpaceTypeCode } from '@/api/types'
import { isSpaceTypeCode } from './spaceTypes'

/** List view state as it lives in the URL: `?type=CAFE&wifi=1&page=2`. */
export interface SpaceListParams {
  type?: SpaceTypeCode
  hasWifi?: true
  /** 1-based, as shown to users. */
  page: number
}

export const PAGE_SIZE = 12

/** Parses search params leniently: unknown or malformed values fall back to "no filter". */
export function parseListParams(search: URLSearchParams): SpaceListParams {
  const rawType = search.get('type')?.toUpperCase()
  const rawPage = Number(search.get('page'))
  return {
    type: rawType && isSpaceTypeCode(rawType) ? rawType : undefined,
    hasWifi: search.get('wifi') === '1' ? true : undefined,
    page: Number.isInteger(rawPage) && rawPage >= 1 ? rawPage : 1,
  }
}

/** Serialises list state, omitting defaults so URLs stay short. */
export function toSearchParams(params: SpaceListParams): URLSearchParams {
  const search = new URLSearchParams()
  if (params.type) search.set('type', params.type)
  if (params.hasWifi) search.set('wifi', '1')
  if (params.page > 1) search.set('page', String(params.page))
  return search
}

export function hasActiveFilters(params: SpaceListParams): boolean {
  return params.type !== undefined || params.hasWifi !== undefined
}
