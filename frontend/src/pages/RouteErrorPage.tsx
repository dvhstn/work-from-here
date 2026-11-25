import { useEffect } from 'react'
import { RotateCwIcon, TriangleAlertIcon } from 'lucide-react'
import { isRouteErrorResponse, useRouteError } from 'react-router-dom'
import { Button } from '@/components/ui/button'
import { ButtonLink } from '@/components/ui/button-link'
import { Empty, EmptyContent, EmptyDescription, EmptyHeader, EmptyMedia } from '@/components/ui/empty'

/** Shown when a route throws while rendering, so the app shell stays usable. */
export function RouteErrorPage() {
  const error = useRouteError()

  useEffect(() => {
    console.error(error)
  }, [error])

  const notFound = isRouteErrorResponse(error) && error.status === 404

  return (
    <Empty className="rounded-lg border border-dashed py-16">
      <EmptyHeader>
        <EmptyMedia variant="icon" className="bg-destructive/10 text-destructive">
          <TriangleAlertIcon aria-hidden="true" />
        </EmptyMedia>
        <h1 tabIndex={-1} className="text-xl font-semibold tracking-tight outline-none">
          {notFound ? 'Page not found' : 'Something went wrong'}
        </h1>
        <EmptyDescription>
          {notFound
            ? "The page you're looking for doesn't exist or has moved."
            : 'This page hit an unexpected problem. Reloading usually fixes it.'}
        </EmptyDescription>
      </EmptyHeader>
      <EmptyContent className="flex-row justify-center">
        {notFound ? null : (
          <Button variant="outline" onClick={() => window.location.reload()}>
            <RotateCwIcon aria-hidden="true" data-icon="inline-start" />
            Reload
          </Button>
        )}
        <ButtonLink to="/spaces">Back to spaces</ButtonLink>
      </EmptyContent>
    </Empty>
  )
}
