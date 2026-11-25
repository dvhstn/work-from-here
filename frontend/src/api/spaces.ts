import { apiClient } from './client'
import { toPaged } from './page'
import type {
  PageRequest,
  Paged,
  Space,
  SpaceFilter,
  SpaceRequest,
  SpringPage,
  SpringPagedModel,
} from './types'

const BASE = '/spaces'

/** `GET /api/v1/spaces`. Undefined filter and page fields are omitted from the query. */
export async function listSpaces(
  filter: SpaceFilter,
  page: PageRequest,
  signal?: AbortSignal,
): Promise<Paged<Space>> {
  const params = {
    type: filter.type,
    hasWifi: filter.hasWifi,
    page: page.page,
    size: page.size,
    sort: page.sort,
  }
  const { data } = await apiClient.get<SpringPage<Space> | SpringPagedModel<Space>>(BASE, {
    params,
    signal,
  })
  return toPaged(data)
}

/** `GET /api/v1/spaces/{id}`. */
export async function getSpace(id: number, signal?: AbortSignal): Promise<Space> {
  const { data } = await apiClient.get<Space>(`${BASE}/${id}`, { signal })
  return data
}

/** `POST /api/v1/spaces` → 201. */
export async function createSpace(body: SpaceRequest): Promise<Space> {
  const { data } = await apiClient.post<Space>(BASE, body)
  return data
}

/** `PUT /api/v1/spaces/{id}` → 200. Full replace: omitting `wifiPassword` clears it. */
export async function updateSpace(id: number, body: SpaceRequest): Promise<Space> {
  const { data } = await apiClient.put<Space>(`${BASE}/${id}`, body)
  return data
}

/** `DELETE /api/v1/spaces/{id}` → 204. */
export async function deleteSpace(id: number): Promise<void> {
  await apiClient.delete(`${BASE}/${id}`)
}
