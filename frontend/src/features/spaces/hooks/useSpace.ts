import { useQuery, useQueryClient } from '@tanstack/react-query'
import { getSpace } from '@/api/spaces'
import type { Paged, Space } from '@/api/types'
import { spaceKeys } from '../queryKeys'

/**
 * Loads one space. Pass `null` for an invalid id to skip the request.
 * If the space is already in a cached list page, it renders immediately
 * while the detail request confirms it.
 */
export function useSpace(id: number | null) {
  const queryClient = useQueryClient()

  return useQuery<Space, Error, Space, ReturnType<typeof spaceKeys.detail>>({
    queryKey: spaceKeys.detail(id ?? -1),
    queryFn: ({ signal }) => getSpace(id as number, signal),
    enabled: id !== null,
    placeholderData: (): Space | undefined => {
      if (id === null) return undefined
      for (const [, page] of queryClient.getQueriesData<Paged<Space>>({ queryKey: spaceKeys.lists() })) {
        const match = page?.items.find((space) => space.id === id)
        if (match) return match
      }
      return undefined
    },
  })
}
