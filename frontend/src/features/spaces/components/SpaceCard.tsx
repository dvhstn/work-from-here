import { Link, useLocation } from 'react-router-dom'
import type { Space } from '@/api/types'
import { spaceLinkState } from '../navigation'
import { SpaceImage } from './SpaceImage'
import { SpaceTypeBadge } from './SpaceTypeBadge'
import { WifiBadge } from './WifiBadge'

/** Matches the list grid: one column, two from `sm`, three from `lg`, capped by `max-w-6xl`. */
const CARD_IMAGE_SIZES =
  '(min-width: 72rem) 360px, (min-width: 64rem) calc((100vw - 5rem) / 3), (min-width: 40rem) calc((100vw - 4rem) / 2), calc(100vw - 2rem)'
const CARD_IMAGE_WIDTHS = [400, 800, 1200] as const

/**
 * A compact card with the space's photo on top. The title link is stretched over the whole card, so
 * there's one tab stop and no nested interactive content. The Wi-Fi
 * password is deliberately never shown here.
 */
interface SpaceCardProps {
  space: Space
  /**
   * Position in the grid. The first row (up to three columns) is likely above
   * the fold, so its photos load eagerly and the first one at high priority.
   */
  index?: number
}

export function SpaceCard({ space, index }: SpaceCardProps) {
  const aboveFold = index !== undefined && index < 3

  // Remember the list's filters and page so the detail page can link back to them.
  const { search } = useLocation()

  return (
    <article className="group relative flex h-full flex-col overflow-hidden rounded-lg border bg-card text-card-foreground shadow-xs transition-colors duration-150 hover:border-foreground/20 has-focus-visible:border-ring has-focus-visible:ring-[3px] has-focus-visible:ring-ring">
      <SpaceImage
        image={space.image}
        type={space.type}
        ratio={16 / 10}
        widths={CARD_IMAGE_WIDTHS}
        sizes={CARD_IMAGE_SIZES}
        loading={aboveFold ? 'eager' : 'lazy'}
        fetchPriority={index === 0 ? 'high' : undefined}
        // The card title already names the space; the long alt would be read before it.
        decorative
        className="border-b"
      />

      <div className="flex flex-1 flex-col gap-4 p-5">
        <div>
          <SpaceTypeBadge type={space.type} />
        </div>

        <div className="flex-1 space-y-1.5">
          <h2 className="text-base leading-snug font-semibold text-balance">
            <Link
              to={`/spaces/${space.id}`}
              state={spaceLinkState(search)}
              className="outline-none after:absolute after:inset-0 after:rounded-lg after:content-['']"
            >
              {space.name}
            </Link>
          </h2>
          <p className="line-clamp-2 text-sm leading-relaxed text-pretty text-muted-foreground">{space.description}</p>
        </div>

        <div>
          {space.wifiPassword === undefined ? null : <WifiBadge hasWifi={typeof space.wifiPassword === 'string'} />}
        </div>
      </div>
    </article>
  )
}
