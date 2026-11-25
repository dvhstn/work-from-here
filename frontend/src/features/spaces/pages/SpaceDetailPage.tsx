import { ArrowLeftIcon } from 'lucide-react'
import { Link, useLocation, useParams } from 'react-router-dom'
import { ApiError, describeError } from '@/api/errors'
import type { Space } from '@/api/types'
import { Button } from '@/components/ui/button'
import { Skeleton } from '@/components/ui/skeleton'
import { useDocumentTitle } from '@/hooks/useDocumentTitle'
import { SpaceLoadError, SpaceNotFound } from '../components/SpaceDetailStates'
import { SpaceTypeBadge, SpaceTypeTile } from '../components/SpaceTypeBadge'
import { WifiPasswordPanel } from '../components/WifiPasswordPanel'
import { useSpace } from '../hooks/useSpace'
import { listHrefFromState, parseSpaceId } from '../navigation'

/**
 * Loaded and loading views share this layout, so the h1 element stays the
 * same and keeps focus when the data arrives.
 */
function SpaceDetailLayout({ space }: { space: Space | undefined }) {
  return (
    <article aria-busy={space ? undefined : true} className="space-y-8">
      <header className="space-y-4">
        <div className="flex items-center gap-3">
          {space ? (
            <>
              <SpaceTypeTile type={space.type} />
              <SpaceTypeBadge type={space.type} />
            </>
          ) : (
            <>
              <Skeleton className="size-10 rounded-md" />
              <Skeleton className="h-6 w-20 rounded-sm" />
            </>
          )}
        </div>
        <h1 tabIndex={-1} className="text-2xl font-semibold tracking-tight text-balance outline-none sm:text-3xl">
          {space ? (
            space.name
          ) : (
            <>
              <span className="sr-only">Loading space…</span>
              <Skeleton aria-hidden="true" className="h-9 w-2/3 max-w-sm" />
            </>
          )}
        </h1>
      </header>

      <hr />

      <section aria-labelledby="space-about" className="space-y-2">
        <h2 id="space-about" className="text-sm font-medium text-muted-foreground">
          About
        </h2>
        {space ? (
          <p className="max-w-prose text-base leading-relaxed text-pretty">{space.description}</p>
        ) : (
          <div className="max-w-prose space-y-2">
            <Skeleton className="h-5 w-full" />
            <Skeleton className="h-5 w-4/5" />
          </div>
        )}
      </section>

      {space ? (
        <WifiPasswordPanel password={space.wifiPassword} />
      ) : (
        <div className="space-y-3 rounded-lg border bg-card p-5 shadow-xs">
          <Skeleton className="h-5 w-16" />
          <Skeleton className="h-6 w-40" />
        </div>
      )}
    </article>
  )
}

export function SpaceDetailPage() {
  const { id: rawId } = useParams()
  const location = useLocation()
  const backHref = listHrefFromState(location.state)
  const id = parseSpaceId(rawId)
  const { data, error, isError, isFetching, refetch } = useSpace(id)

  const notFound = id === null || (isError && !data && error instanceof ApiError && error.status === 404)
  const failed = isError && !data && !notFound

  useDocumentTitle(data?.name ?? (notFound ? 'Space not found' : undefined))

  let content
  if (notFound) {
    content = <SpaceNotFound backHref={backHref} />
  } else if (failed) {
    content = (
      <SpaceLoadError
        message={describeError(error)}
        onRetry={() => void refetch()}
        retrying={isFetching}
        backHref={backHref}
      />
    )
  } else {
    content = <SpaceDetailLayout space={data} />
  }

  return (
    <div className="mx-auto max-w-3xl space-y-6">
      <Button render={<Link to={backHref} />} nativeButton={false} variant="ghost" size="sm" className="-ml-3">
        <ArrowLeftIcon aria-hidden="true" data-icon="inline-start" />
        All spaces
      </Button>
      {content}
    </div>
  )
}
