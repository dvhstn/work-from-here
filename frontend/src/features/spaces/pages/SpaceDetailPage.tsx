import { useId, type ReactNode } from 'react'
import { ArrowLeftIcon, MapPinOffIcon, RotateCwIcon, TriangleAlertIcon } from 'lucide-react'
import { useParams } from 'react-router-dom'
import { ApiError, describeError } from '@/api/errors'
import type { Space } from '@/api/types'
import { Button } from '@/components/ui/button'
import { Skeleton } from '@/components/ui/skeleton'
import { useDocumentTitle } from '@/hooks/useDocumentTitle'
import { cn } from '@/lib/utils'
import { BackToListLink } from '../components/BackToListLink'
import { SpaceTypeBadge, SpaceTypeTile } from '../components/SpaceTypeBadge'
import { WifiPasswordPanel } from '../components/WifiPasswordPanel'
import { useSpace } from '../hooks/useSpace'
import { parseSpaceId } from '../navigation'

type View =
  | { kind: 'loading' }
  | { kind: 'ready'; space: Space }
  | { kind: 'not-found' }
  | { kind: 'error'; message: string }

const headingText: Record<Exclude<View['kind'], 'ready' | 'loading'>, string> = {
  'not-found': 'Space not found',
  error: "Couldn't load this space",
}

function StatusTile({ tone, children }: { tone: string; children: ReactNode }) {
  return <span className={cn('flex size-10 shrink-0 items-center justify-center rounded-md', tone)}>{children}</span>
}

function HeaderMedia({ view }: { view: View }) {
  switch (view.kind) {
    case 'ready':
      return (
        <>
          <SpaceTypeTile type={view.space.type} />
          <SpaceTypeBadge type={view.space.type} />
        </>
      )
    case 'loading':
      return (
        <>
          <Skeleton className="size-10 rounded-md" />
          <Skeleton className="h-6 w-20 rounded-sm" />
        </>
      )
    case 'not-found':
      return (
        <StatusTile tone="bg-muted text-muted-foreground">
          <MapPinOffIcon aria-hidden="true" className="size-5" />
        </StatusTile>
      )
    case 'error':
      return (
        <StatusTile tone="bg-destructive/10 text-destructive">
          <TriangleAlertIcon aria-hidden="true" className="size-5" />
        </StatusTile>
      )
  }
}

function LoadingBody() {
  return (
    <>
      <hr />
      <div className="max-w-prose space-y-2">
        <Skeleton className="h-4 w-12" />
        <Skeleton className="h-5 w-full" />
        <Skeleton className="h-5 w-4/5" />
      </div>
      <div className="space-y-3 rounded-lg border bg-card p-5 shadow-xs">
        <Skeleton className="h-5 w-16" />
        <Skeleton className="h-6 w-40" />
      </div>
    </>
  )
}

function ReadyBody({ space }: { space: Space }) {
  const aboutId = useId()
  return (
    <>
      <hr />
      <section aria-labelledby={aboutId} className="space-y-2">
        <h2 id={aboutId} className="text-sm font-medium text-muted-foreground">
          About
        </h2>
        <p className="max-w-prose text-base leading-relaxed text-pretty">{space.description}</p>
      </section>
      <WifiPasswordPanel password={space.wifiPassword} />
    </>
  )
}

export function SpaceDetailPage() {
  const { id: rawId } = useParams()
  const id = parseSpaceId(rawId)
  const { data, error, isError, isFetching, refetch } = useSpace(id)

  let view: View
  if (data) view = { kind: 'ready', space: data }
  else if (id === null || (isError && error instanceof ApiError && error.status === 404)) view = { kind: 'not-found' }
  else if (isError) view = { kind: 'error', message: describeError(error) }
  else view = { kind: 'loading' }

  useDocumentTitle(view.kind === 'ready' ? view.space.name : view.kind === 'not-found' ? headingText['not-found'] : undefined)

  // Announced when loading resolves to a problem; the h1 keeps focus but its text change isn't read out.
  const announcement =
    view.kind === 'not-found'
      ? headingText['not-found']
      : view.kind === 'error'
        ? `${headingText.error}. ${view.message}`
        : ''

  return (
    <div className="mx-auto max-w-3xl space-y-6">
      <BackToListLink variant="ghost" size="sm" className="-ml-3">
        <ArrowLeftIcon aria-hidden="true" data-icon="inline-start" />
        All spaces
      </BackToListLink>

      <p role="status" className="sr-only">
        {announcement}
      </p>

      {/*
        One article and one h1 for every state: route-change focus lands on the
        h1 while loading, and the same node keeps focus when the data, a 404 or
        an error arrives.
      */}
      <article aria-busy={view.kind === 'loading' ? true : undefined} className="space-y-8">
        <header className="space-y-4">
          <div className="flex items-center gap-3">
            <HeaderMedia view={view} />
          </div>
          <h1 tabIndex={-1} className="text-2xl font-semibold tracking-tight text-balance outline-none sm:text-3xl">
            {view.kind === 'ready' ? (
              view.space.name
            ) : view.kind === 'loading' ? (
              <>
                <span className="sr-only">Loading space…</span>
                <Skeleton aria-hidden="true" className="h-9 w-2/3 max-w-sm" />
              </>
            ) : (
              headingText[view.kind]
            )}
          </h1>
        </header>

        {view.kind === 'ready' ? <ReadyBody space={view.space} /> : null}
        {view.kind === 'loading' ? <LoadingBody /> : null}

        {view.kind === 'not-found' ? (
          <div className="space-y-4">
            <p className="text-muted-foreground">It may have been removed, or the link might be wrong.</p>
            <BackToListLink>Back to spaces</BackToListLink>
          </div>
        ) : null}

        {view.kind === 'error' ? (
          <div className="space-y-4">
            <p className="text-muted-foreground">{view.message}</p>
            <div className="flex flex-wrap gap-2">
              <Button variant="outline" onClick={() => void refetch()} disabled={isFetching} focusableWhenDisabled>
                <RotateCwIcon
                  aria-hidden="true"
                  data-icon="inline-start"
                  className={isFetching ? 'motion-safe:animate-spin' : undefined}
                />
                {isFetching ? 'Retrying…' : 'Try again'}
              </Button>
              <BackToListLink variant="ghost">Back to spaces</BackToListLink>
            </div>
          </div>
        ) : null}
      </article>
    </div>
  )
}
