import { keepPreviousData, useQuery } from '@tanstack/react-query'
import { listSpaces } from '@/api/spaces'
import type { PageRequest, SpaceFilter } from '@/api/types'
import { spaceKeys } from '../queryKeys'

export function useSpaces(filter: SpaceFilter, page: PageRequest) {
  return useQuery({
    queryKey: spaceKeys.list(filter, page),
    queryFn: ({ signal }) => listSpaces(filter, page, signal),
    placeholderData: keepPreviousData,
  })
}
