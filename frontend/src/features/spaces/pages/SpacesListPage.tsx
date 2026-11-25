import { useCallback, useEffect, useMemo, useRef } from 'react'
import { RotateCwIcon, TriangleAlertIcon } from 'lucide-react'
import { useSearchParams } from 'react-router-dom'
import { describeError } from '@/api/errors'
import type { PageRequest, SpaceFilter, SpaceTypeCode } from '@/api/types'
import { PageHeader } from '@/components/layout/PageHeader'
import { Alert, AlertAction, AlertDescription, AlertTitle } from '@/components/ui/alert'
import { Button } from '@/components/ui/button'
import { cn } from '@/lib/utils'
import { ListPagination } from '../components/ListPagination'
import { SpaceCard } from '../components/SpaceCard'
import { SpaceCardSkeleton } from '../components/SpaceCardSkeleton'
import { SpaceFilters } from '../components/SpaceFilters'
import { NoMatchesState, NoSpacesState, SpacesErrorState } from '../components/SpaceListStates'
import { useSpaces } from '../hooks/useSpaces'
import { PAGE_SIZE, hasActiveFilters, parseListParams, toSearchParams, type SpaceListParams } from '../listParams'
import { SPACE_TYPES } from '../spaceTypes'

const SKELETON_COUNT = 6
const gridClass = 'grid gap-4 sm:grid-cols-2 lg:grid-cols-3'

function hrefFor(params: SpaceListParams): string {
  const search = toSearchParams(params).toString()
  return search ? `/spaces?${search}` : '/spaces'
}

function describeFilters(params: SpaceListParams): string {
  const parts: string[] = []
  if (params.type) parts.push(SPACE_TYPES[params.type].label)
  if (params.hasWifi) parts.push('Has Wi-Fi')
  return parts.join(' · ')
}

function countLabel(total: number): string {
  return `${total} ${total === 1 ? 'space' : 'spaces'}`
}

export function SpacesListPage() {
  const [searchParams, setSearchParams] = useSearchParams()
  const params = useMemo(() => parseListParams(searchParams), [searchParams])

  const filter: SpaceFilter = { type: params.type, hasWifi: params.hasWifi }
  const pageRequest: PageRequest = { page: params.page - 1, size: PAGE_SIZE, sort: 'name,asc' }
  const { data, error, isError, isPending, isPlaceholderData, isFetching, refetch } = useSpaces(filter, pageRequest)

  const update = useCallback(
    (next: SpaceListParams, replace = false) => {
      setSearchParams(toSearchParams(next), { replace, preventScrollReset: true })
    },
    [setSearchParams],
  )

  const setType = (type: SpaceTypeCode | undefined) => update({ ...params, type, page: 1 })
  const setWifi = (hasWifi: boolean) => update({ ...params, hasWifi: hasWifi ? true : undefined, page: 1 })
  // Both "Clear filters" buttons disappear once used, so hand focus to a control that stays.
  const allTypesRef = useRef<HTMLButtonElement>(null)
  const clearFilters = () => {
    update({ page: 1 })
    allTypesRef.current?.focus()
  }

  // A page past the end (e.g. a stale link) jumps to the last page that exists.
  const lastPage = data ? Math.max(1, data.totalPages) : 1
  const outOfRange = data !== undefined && !isPlaceholderData && params.page > lastPage
  useEffect(() => {
    if (outOfRange) update({ ...params, page: lastPage }, true)
  }, [outOfRange, lastPage, params, update])

  const showSkeleton = isPending || outOfRange
  // A background refetch failed but earlier results are still on screen.
  const isStale = isError && data !== undefined

  let status = ''
  if (showSkeleton) status = 'Loading spaces…'
  else if (data) status = data.totalElements === 0 && hasActiveFilters(params) ? 'No spaces match' : countLabel(data.totalElements)

  return (
    <div className="space-y-6">
      <PageHeader
        title="Spaces"
        description="Cafes and hot desks you can work from."
        aside={
          <p role="status" aria-live="polite" className="text-sm text-muted-foreground tabular-nums">
            {status}
          </p>
        }
      />

      <SpaceFilters
        params={params}
        onTypeChange={setType}
        onWifiChange={setWifi}
        onClear={clearFilters}
        allTypesRef={allTypesRef}
      />

      {isStale ? (
        <Alert>
          <TriangleAlertIcon aria-hidden="true" />
          <AlertTitle>Couldn't refresh</AlertTitle>
          <AlertDescription>Showing earlier results. {describeError(error)}</AlertDescription>
          <AlertAction>
            <Button variant="outline" size="sm" onClick={() => void refetch()} disabled={isFetching} focusableWhenDisabled>
              <RotateCwIcon
                aria-hidden="true"
                data-icon="inline-start"
                className={isFetching ? 'motion-safe:animate-spin' : undefined}
              />
              Retry
            </Button>
          </AlertAction>
        </Alert>
      ) : null}

      <section aria-label="Results" className="space-y-8">
        {showSkeleton ? (
          <ul className={gridClass} aria-busy="true" aria-label="Loading spaces">
            {Array.from({ length: SKELETON_COUNT }, (_, i) => (
              <li key={i}>
                <SpaceCardSkeleton />
              </li>
            ))}
          </ul>
        ) : !data ? (
          <SpacesErrorState
            message={isError ? describeError(error) : 'Something unexpected went wrong.'}
            onRetry={() => void refetch()}
            retrying={isFetching}
          />
        ) : data.totalElements === 0 ? (
          hasActiveFilters(params) ? (
            <NoMatchesState summary={describeFilters(params)} onClear={clearFilters} />
          ) : (
            <NoSpacesState />
          )
        ) : (
          <>
            <ul
              className={cn(gridClass, 'transition-opacity duration-150', isPlaceholderData && 'opacity-60')}
              aria-busy={isPlaceholderData}
            >
              {data.items.map((space, index) => (
                <li key={space.id}>
                  <SpaceCard space={space} index={index} />
                </li>
              ))}
            </ul>
            <ListPagination
              page={params.page}
              totalPages={data.totalPages}
              hrefForPage={(page) => hrefFor({ ...params, page })}
            />
          </>
        )}
      </section>
    </div>
  )
}
