import type { SpaceImage } from '@/api/types'
import { cn } from '@/lib/utils'

/** Only plain web links are rendered, so a bad value can't become a `javascript:` URL. */
function safeHttpUrl(value: string | null | undefined): string | null {
  if (!value) return null
  try {
    const url = new URL(value)
    return url.protocol === 'https:' || url.protocol === 'http:' ? url.href : null
  } catch {
    return null
  }
}

/**
 * "Photo: {credit}, {license}", linked to the source page. CC BY and CC BY-SA
 * require the author, the licence and a link back, so this stays visible.
 */
export function PhotoCredit({ image, className }: { image: SpaceImage | null | undefined; className?: string }) {
  if (!image) return null
  const parts = [image.credit, image.license].map((part) => part?.trim()).filter(Boolean)
  if (parts.length === 0) return null

  const text = `Photo: ${parts.join(', ')}`
  const href = safeHttpUrl(image.sourceUrl)

  return (
    <p className={cn('text-xs text-muted-foreground', className)}>
      {href ? (
        <a
          href={href}
          rel="noopener noreferrer"
          className="rounded-xs underline decoration-muted-foreground/60 underline-offset-2 outline-none hover:text-foreground hover:decoration-foreground focus-visible:ring-[3px] focus-visible:ring-ring"
        >
          {text}
        </a>
      ) : (
        text
      )}
    </p>
  )
}
