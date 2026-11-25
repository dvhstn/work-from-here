import type { PageRequest, SpaceFilter } from '@/api/types'

export const spaceKeys = {
  all: ['spaces'] as const,
  lists: () => [...spaceKeys.all, 'list'] as const,
  list: (filter: SpaceFilter, page: PageRequest) => [...spaceKeys.lists(), filter, page] as const,
  detail: (id: number) => [...spaceKeys.all, 'detail', id] as const,
}
