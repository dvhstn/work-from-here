import { cn } from '@/lib/utils'
import { styleFor } from '../spaceTypeStyle'
import { spaceTypeLabel } from '../spaceTypes'

export function SpaceTypeIcon({ type, className }: { type: unknown; className?: string }) {
  const { Icon } = styleFor(type)
  return <Icon aria-hidden="true" className={className} />
}

/** Square coloured tile holding the type glyph, used on cards and detail headers. */
export function SpaceTypeTile({ type, className }: { type: unknown; className?: string }) {
  return (
    <span className={cn('flex size-10 shrink-0 items-center justify-center rounded-md', styleFor(type).tone, className)}>
      <SpaceTypeIcon type={type} className="size-5" />
    </span>
  )
}

export function SpaceTypeBadge({ type }: { type: unknown }) {
  return (
    <span className={cn('inline-flex h-6 items-center rounded-sm px-2 text-xs font-medium', styleFor(type).tone)}>
      {spaceTypeLabel(type)}
    </span>
  )
}
