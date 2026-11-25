import {
  Building2Icon,
  CoffeeIcon,
  HotelIcon,
  LibraryBigIcon,
  MapPinIcon,
  MonitorIcon,
  WineIcon,
  type LucideIcon,
} from 'lucide-react'
import type { SpaceTypeCode } from '@/api/types'
import { isSpaceTypeCode } from './spaceTypes'

export interface TypeStyle {
  Icon: LucideIcon
  /** Soft background plus the matching text colour, for badges and tiles. */
  tone: string
  /** Soft background only, for image placeholders. */
  soft: string
}

// Typed against every code, so adding a type without a style fails the build.
const TYPE_STYLES: Record<SpaceTypeCode, TypeStyle> = {
  CAFE: { Icon: CoffeeIcon, tone: 'bg-cafe-soft text-cafe', soft: 'bg-cafe-soft' },
  HOT_DESK: { Icon: MonitorIcon, tone: 'bg-desk-soft text-desk', soft: 'bg-desk-soft' },
  BAR: { Icon: WineIcon, tone: 'bg-bar-soft text-bar', soft: 'bg-bar-soft' },
  HOTEL: { Icon: HotelIcon, tone: 'bg-hotel-soft text-hotel', soft: 'bg-hotel-soft' },
  LIBRARY: { Icon: LibraryBigIcon, tone: 'bg-library-soft text-library', soft: 'bg-library-soft' },
  COWORKING: { Icon: Building2Icon, tone: 'bg-coworking-soft text-coworking', soft: 'bg-coworking-soft' },
}

const FALLBACK_STYLE: TypeStyle = { Icon: MapPinIcon, tone: 'bg-muted text-muted-foreground', soft: 'bg-muted' }

/** Accepts `unknown` on purpose so an unexpected API value can't crash a render. */
export function styleFor(type: unknown): TypeStyle {
  return isSpaceTypeCode(type) ? TYPE_STYLES[type] : FALLBACK_STYLE
}
