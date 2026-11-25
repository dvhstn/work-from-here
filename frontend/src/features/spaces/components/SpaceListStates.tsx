import { MapPinnedIcon, PlusIcon, RotateCwIcon, SearchXIcon, TriangleAlertIcon } from 'lucide-react'
import { Link } from 'react-router-dom'
import { Button } from '@/components/ui/button'
import {
  Empty,
  EmptyContent,
  EmptyDescription,
  EmptyHeader,
  EmptyMedia,
} from '@/components/ui/empty'

const frame = 'rounded-lg border border-dashed py-12'
const titleClass = 'text-base font-semibold tracking-tight'

export function NoSpacesState() {
  return (
    <Empty className={frame}>
      <EmptyHeader>
        <EmptyMedia variant="icon">
          <MapPinnedIcon aria-hidden="true" />
        </EmptyMedia>
        <h2 className={titleClass}>
          No spaces yet
        </h2>
        <EmptyDescription>Add the first cafe or hot desk you like working from.</EmptyDescription>
      </EmptyHeader>
      <EmptyContent>
        <Button render={<Link to="/spaces/new" />} nativeButton={false}>
          <PlusIcon aria-hidden="true" data-icon="inline-start" />
          Add space
        </Button>
      </EmptyContent>
    </Empty>
  )
}

export function NoMatchesState({ summary, onClear }: { summary: string; onClear: () => void }) {
  return (
    <Empty className={frame}>
      <EmptyHeader>
        <EmptyMedia variant="icon">
          <SearchXIcon aria-hidden="true" />
        </EmptyMedia>
        <h2 className={titleClass}>
          No spaces match these filters
        </h2>
        <EmptyDescription>{summary}</EmptyDescription>
      </EmptyHeader>
      <EmptyContent>
        <Button variant="secondary" onClick={onClear}>
          Clear filters
        </Button>
      </EmptyContent>
    </Empty>
  )
}

interface ErrorStateProps {
  message: string
  onRetry: () => void
  retrying: boolean
}

export function SpacesErrorState({ message, onRetry, retrying }: ErrorStateProps) {
  return (
    <Empty className={frame} role="alert">
      <EmptyHeader>
        <EmptyMedia variant="icon" className="bg-destructive/10 text-destructive">
          <TriangleAlertIcon aria-hidden="true" />
        </EmptyMedia>
        <h2 className={titleClass}>
          Couldn't load spaces
        </h2>
        <EmptyDescription>{message}</EmptyDescription>
      </EmptyHeader>
      <EmptyContent>
        <Button variant="outline" onClick={onRetry} disabled={retrying}>
          <RotateCwIcon aria-hidden="true" data-icon="inline-start" className={retrying ? 'motion-safe:animate-spin' : undefined} />
          {retrying ? 'Retrying…' : 'Try again'}
        </Button>
      </EmptyContent>
    </Empty>
  )
}
