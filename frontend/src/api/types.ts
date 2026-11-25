/**
 * Wire types for the work-from-here REST API.
 *
 * These mirror the backend DTOs one-to-one. UI code should consume the
 * normalised shapes (e.g. `Paged<T>`) rather than raw Spring structures.
 */

/** Enum constant names of `SpaceTypeResource` as serialised in responses. */
export type SpaceTypeCode = 'CAFE' | 'HOT_DESK'

/** Ids of `SpaceTypeResource`, used by request bodies (`typeId`). */
export type SpaceTypeId = 1 | 2

/** `SpaceResponseDTO` */
export interface Space {
  id: number
  name: string
  description: string
  type: SpaceTypeCode
  /** `null` means the space has no Wi-Fi. */
  wifiPassword: string | null
}

/** `SpaceRequestDTO` (POST and PUT; PUT is a full replace). */
export interface SpaceRequest {
  /** 1–100 characters, not blank, unique case-insensitively. */
  name: string
  /** 1–255 characters, not blank. */
  description: string
  typeId: SpaceTypeId
  /** 1–100 characters and not whitespace-only. Send `null`, never `''`, for "no Wi-Fi". */
  wifiPassword?: string | null
}

/** `SpaceFilterDTO`. Omitted fields mean "no filter". */
export interface SpaceFilter {
  type?: SpaceTypeCode
  hasWifi?: boolean
}

export type SpaceSortField = 'id' | 'name' | 'description' | 'type' | 'wifiPassword'
export type SortDirection = 'asc' | 'desc'
export type SortParam = `${SpaceSortField},${SortDirection}`

/** Spring `Pageable` query params. `page` is 0-based. */
export interface PageRequest {
  page?: number
  size?: number
  sort?: SortParam
}

/** `ApiErrorResource`, returned for every error status. */
export interface ApiErrorBody {
  status: number
  message: string
  path: string
  /** ISO-8601 instant. */
  timestamp: string
}

interface SpringSort {
  empty: boolean
  sorted: boolean
  unsorted: boolean
}

/** Spring Data `PageImpl` as currently serialised by `GET /api/v1/spaces`. */
export interface SpringPage<T> {
  content: T[]
  pageable:
    | {
        pageNumber: number
        pageSize: number
        sort: SpringSort
        offset: number
        paged: boolean
        unpaged: boolean
      }
    | 'INSTANCE'
  totalElements: number
  totalPages: number
  last: boolean
  first: boolean
  size: number
  number: number
  numberOfElements: number
  empty: boolean
  sort: SpringSort
}

/** Spring Data `PagedModel` (the `VIA_DTO` serialisation mode). */
export interface SpringPagedModel<T> {
  content: T[]
  page: {
    size: number
    number: number
    totalElements: number
    totalPages: number
  }
}

/** Page shape consumed by the UI, independent of how Spring serialises it. */
export interface Paged<T> {
  items: T[]
  /** 0-based. */
  page: number
  size: number
  totalElements: number
  totalPages: number
}
