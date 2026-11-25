import { Fragment, type ReactNode } from 'react'
import type { SpaceImage } from '@/api/types'
import { cn } from '@/lib/utils'
import { licenseUrl } from '../licenses'

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

function CreditLink({ href, children }: { href: string | null; children: ReactNode }) {
  if (!href) return <>{children}</>
  return (
    <a
      href={href}
      rel="noopener noreferrer"
      className="rounded-xs underline decoration-muted-foreground/60 underline-offset-2 outline-none hover:text-foreground hover:decoration-foreground focus-visible:ring-[3px] focus-visible:ring-ring"
    >
      {children}
    </a>
  )
}

/**
 * "Photo: {credit}, {license}, cropped". The credit links to the source page
 * and the licence to its Creative Commons deed: CC BY and CC BY-SA require the
 * author, a link to the licence and a note that the work was changed (photos
 * are always cropped to fit).
 */
export function PhotoCredit({ image, className }: { image: SpaceImage | null | undefined; className?: string }) {
  if (!image) return null
  const credit = image.credit?.trim()
  const license = image.license?.trim()
  const sourceHref = safeHttpUrl(image.sourceUrl)
  if (!credit && !license) return null

  const parts: ReactNode[] = []
  if (credit) parts.push(<CreditLink href={sourceHref}>{credit}</CreditLink>)
  else if (sourceHref) parts.push(<CreditLink href={sourceHref}>source</CreditLink>)
  if (license) parts.push(<CreditLink href={licenseUrl(license)}>{license}</CreditLink>)
  parts.push('cropped')

  return (
    <p className={cn('text-xs text-muted-foreground', className)}>
      Photo:{' '}
      {parts.map((part, index) => (
        <Fragment key={index}>
          {index > 0 ? ', ' : null}
          {part}
        </Fragment>
      ))}
    </p>
  )
}
