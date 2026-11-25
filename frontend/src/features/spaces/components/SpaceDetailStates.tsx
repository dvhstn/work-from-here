import { MapPinOffIcon, RotateCwIcon, TriangleAlertIcon } from 'lucide-react'
import { Link } from 'react-router-dom'
import { Button } from '@/components/ui/button'
import { Empty, EmptyContent, EmptyDescription, EmptyHeader, EmptyMedia } from '@/components/ui/empty'

const frame = 'rounded-lg border border-dashed py-14'
const titleClass = 'text-xl font-semibold tracking-tight outline-none'

export function SpaceNotFound({ backHref }: { backHref: string }) {
  return (
    <Empty className={frame}>
      <EmptyHeader>
        <EmptyMedia variant="icon">
          <MapPinOffIcon aria-hidden="true" />
        </EmptyMedia>
        <h1 tabIndex={-1} className={titleClass}>
          Space not found
        </h1>
        <EmptyDescription>It may have been removed, or the link might be wrong.</EmptyDescription>
      </EmptyHeader>
      <EmptyContent>
        <Button render={<Link to={backHref} />} nativeButton={false}>
          Back to spaces
        </Button>
      </EmptyContent>
    </Empty>
  )
}

interface SpaceLoadErrorProps {
  message: string
  onRetry: () => void
  retrying: boolean
  backHref: string
}

export function SpaceLoadError({ message, onRetry, retrying, backHref }: SpaceLoadErrorProps) {
  return (
    <Empty className={frame} role="alert">
      <EmptyHeader>
        <EmptyMedia variant="icon" className="bg-destructive/10 text-destructive">
          <TriangleAlertIcon aria-hidden="true" />
        </EmptyMedia>
        <h1 tabIndex={-1} className={titleClass}>
          Couldn't load this space
        </h1>
        <EmptyDescription>{message}</EmptyDescription>
      </EmptyHeader>
      <EmptyContent className="flex-row justify-center">
        <Button variant="outline" onClick={onRetry} disabled={retrying} focusableWhenDisabled>
          <RotateCwIcon
            aria-hidden="true"
            data-icon="inline-start"
            className={retrying ? 'motion-safe:animate-spin' : undefined}
          />
          {retrying ? 'Retrying…' : 'Try again'}
        </Button>
        <Button render={<Link to={backHref} />} nativeButton={false} variant="ghost">
          Back to spaces
        </Button>
      </EmptyContent>
    </Empty>
  )
}
