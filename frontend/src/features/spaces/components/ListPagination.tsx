import { ChevronLeftIcon, ChevronRightIcon } from 'lucide-react'
import { Link } from 'react-router-dom'
import { Button } from '@/components/ui/button'

interface ListPaginationProps {
  /** 1-based. */
  page: number
  totalPages: number
  hrefForPage: (page: number) => string
}

/** Previous / next links driven by the URL. Hidden when there's only one page. */
export function ListPagination({ page, totalPages, hrefForPage }: ListPaginationProps) {
  if (totalPages <= 1) return null

  const hasPrevious = page > 1
  const hasNext = page < totalPages

  return (
    <nav aria-label="Pagination" className="flex items-center justify-center gap-2 sm:gap-4">
      {hasPrevious ? (
        <Button render={<Link to={hrefForPage(page - 1)} />} nativeButton={false} variant="outline">
          <ChevronLeftIcon aria-hidden="true" data-icon="inline-start" />
          Previous
        </Button>
      ) : (
        <Button variant="outline" disabled>
          <ChevronLeftIcon aria-hidden="true" data-icon="inline-start" />
          Previous
        </Button>
      )}

      <p className="text-sm text-muted-foreground tabular-nums">
        Page {page} of {totalPages}
      </p>

      {hasNext ? (
        <Button render={<Link to={hrefForPage(page + 1)} />} nativeButton={false} variant="outline">
          Next
          <ChevronRightIcon aria-hidden="true" data-icon="inline-end" />
        </Button>
      ) : (
        <Button variant="outline" disabled>
          Next
          <ChevronRightIcon aria-hidden="true" data-icon="inline-end" />
        </Button>
      )}
    </nav>
  )
}
