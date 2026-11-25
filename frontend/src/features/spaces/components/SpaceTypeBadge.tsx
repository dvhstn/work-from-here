import { CoffeeIcon, MonitorIcon, type LucideIcon } from 'lucide-react'
import { cn } from '@/lib/utils'
import type { SpaceTypeCode } from '@/api/types'
import { SPACE_TYPES } from '../spaceTypes'

const TYPE_STYLES: Record<SpaceTypeCode, { Icon: LucideIcon; tone: string }> = {
  CAFE: { Icon: CoffeeIcon, tone: 'bg-cafe-soft text-cafe' },
  HOT_DESK: { Icon: MonitorIcon, tone: 'bg-desk-soft text-desk' },
}

export function SpaceTypeIcon({ type, className }: { type: SpaceTypeCode; className?: string }) {
  const { Icon } = TYPE_STYLES[type]
  return <Icon aria-hidden="true" className={className} />
}

/** Square coloured tile holding the type glyph, used on cards and detail headers. */
export function SpaceTypeTile({ type, className }: { type: SpaceTypeCode; className?: string }) {
  return (
    <span className={cn('flex size-10 shrink-0 items-center justify-center rounded-md', TYPE_STYLES[type].tone, className)}>
      <SpaceTypeIcon type={type} className="size-5" />
    </span>
  )
}

export function SpaceTypeBadge({ type }: { type: SpaceTypeCode }) {
  return (
    <span
      className={cn(
        'inline-flex h-6 items-center rounded-sm px-2 text-xs font-medium',
        TYPE_STYLES[type].tone,
      )}
    >
      {SPACE_TYPES[type].label}
    </span>
  )
}
