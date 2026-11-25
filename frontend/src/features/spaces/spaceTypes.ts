import type { SpaceTypeCode, SpaceTypeId } from '@/api/types'

export interface SpaceTypeMeta {
  code: SpaceTypeCode
  id: SpaceTypeId
  label: string
}

/** Single source of truth for space types until the API exposes them. */
export const SPACE_TYPES: Record<SpaceTypeCode, SpaceTypeMeta> = {
  CAFE: { code: 'CAFE', id: 1, label: 'Cafe' },
  HOT_DESK: { code: 'HOT_DESK', id: 2, label: 'Hot desk' },
}

export const SPACE_TYPE_LIST: readonly SpaceTypeMeta[] = Object.values(SPACE_TYPES)

export function isSpaceTypeCode(value: unknown): value is SpaceTypeCode {
  return typeof value === 'string' && Object.hasOwn(SPACE_TYPES, value)
}

/**
 * Display label for any `type` value the API returns. Unknown values (a new
 * type, or a changed representation) degrade to a readable label instead of
 * throwing during render.
 */
export function spaceTypeLabel(type: unknown): string {
  if (isSpaceTypeCode(type)) return SPACE_TYPES[type].label
  if (typeof type === 'string' && type.trim() !== '') return type
  return 'Other'
}
