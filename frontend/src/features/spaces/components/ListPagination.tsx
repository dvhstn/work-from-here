import type { ReactNode } from 'react'
import { ChevronLeftIcon, ChevronRightIcon } from 'lucide-react'
import { Link } from 'react-router-dom'
import { Button } from '@/components/ui/button'

interface ListPaginationProps {
  /** 1-based. */
  page: number
  totalPages: number
  hrefForPage: (page: number) => string
}

interface PageLinkProps {
  to: string
  disabled: boolean
  children: ReactNode
}

/**
 * Always renders the same `<a>`, even at the first or last page, so keyboard
 * focus isn't dropped when the link becomes unavailable. Disabled links are
 * marked `aria-disabled` and ignore activation.
 */
function PageLink({ to, disabled, children }: PageLinkProps) {
  return (
    <Button
      variant="outline"
      nativeButton={false}
      className="aria-disabled:cursor-not-allowed aria-disabled:opacity-50 aria-disabled:hover:bg-background"
      render={
        <Link
          to={to}
          aria-disabled={disabled || undefined}
          onClick={(event) => {
            if (disabled) event.preventDefault()
          }}
        />
      }
    >
      {children}
    </Button>
  )
}

/** Previous / next links driven by the URL. Hidden when there's only one page. */
export function ListPagination({ page, totalPages, hrefForPage }: ListPaginationProps) {
  if (totalPages <= 1) return null

  const hasPrevious = page > 1
  const hasNext = page < totalPages

  return (
    <nav aria-label="Pagination" className="flex items-center justify-center gap-2 sm:gap-4">
      <PageLink to={hrefForPage(hasPrevious ? page - 1 : page)} disabled={!hasPrevious}>
        <ChevronLeftIcon aria-hidden="true" data-icon="inline-start" />
        Previous
      </PageLink>

      <p className="text-sm text-muted-foreground tabular-nums">
        Page {page} of {totalPages}
      </p>

      <PageLink to={hrefForPage(hasNext ? page + 1 : page)} disabled={!hasNext}>
        Next
        <ChevronRightIcon aria-hidden="true" data-icon="inline-end" />
      </PageLink>
    </nav>
  )
}
