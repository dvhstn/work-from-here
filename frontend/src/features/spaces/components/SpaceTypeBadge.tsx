import { CoffeeIcon, MapPinIcon, MonitorIcon, type LucideIcon } from 'lucide-react'
import { cn } from '@/lib/utils'
import { isSpaceTypeCode, spaceTypeLabel } from '../spaceTypes'

interface TypeStyle {
  Icon: LucideIcon
  tone: string
}

const TYPE_STYLES = {
  CAFE: { Icon: CoffeeIcon, tone: 'bg-cafe-soft text-cafe' },
  HOT_DESK: { Icon: MonitorIcon, tone: 'bg-desk-soft text-desk' },
} satisfies Record<string, TypeStyle>

const FALLBACK_STYLE: TypeStyle = { Icon: MapPinIcon, tone: 'bg-muted text-muted-foreground' }

/** Accepts `unknown` on purpose so an unexpected API value can't crash a render. */
function styleFor(type: unknown): TypeStyle {
  return isSpaceTypeCode(type) ? TYPE_STYLES[type] : FALLBACK_STYLE
}

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
