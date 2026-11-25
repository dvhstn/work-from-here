import { Link } from 'react-router-dom'
import type { Space } from '@/api/types'
import { SpaceTypeBadge, SpaceTypeTile } from './SpaceTypeBadge'
import { WifiBadge } from './WifiBadge'

/**
 * A compact text card. The title link is stretched over the whole card, so
 * there's one tab stop and no nested interactive content. The Wi-Fi
 * password is deliberately never shown here.
 */
export function SpaceCard({ space }: { space: Space }) {
  return (
    <article className="group relative flex h-full flex-col gap-4 rounded-lg border bg-card p-5 text-card-foreground shadow-xs transition-colors duration-150 hover:border-foreground/20 has-focus-visible:border-ring has-focus-visible:ring-[3px] has-focus-visible:ring-ring/50">
      <div className="flex items-center gap-3">
        <SpaceTypeTile type={space.type} />
        <SpaceTypeBadge type={space.type} />
      </div>

      <div className="flex-1 space-y-1.5">
        <h2 className="text-base leading-snug font-semibold text-balance">
          <Link
            to={`/spaces/${space.id}`}
            className="outline-none after:absolute after:inset-0 after:rounded-lg after:content-['']"
          >
            {space.name}
          </Link>
        </h2>
        <p className="line-clamp-2 text-sm leading-relaxed text-pretty text-muted-foreground">{space.description}</p>
      </div>

      <div>
        <WifiBadge hasWifi={space.wifiPassword !== null} />
      </div>
    </article>
  )
}
