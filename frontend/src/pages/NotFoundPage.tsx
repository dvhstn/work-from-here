import { CompassIcon } from 'lucide-react'
import { Link } from 'react-router-dom'
import { Button } from '@/components/ui/button'
import { Empty, EmptyContent, EmptyDescription, EmptyHeader, EmptyMedia } from '@/components/ui/empty'

export function NotFoundPage() {
  return (
    <Empty className="py-16">
      <EmptyHeader>
        <EmptyMedia variant="icon">
          <CompassIcon aria-hidden="true" />
        </EmptyMedia>
        <h1 tabIndex={-1} className="text-xl font-semibold tracking-tight outline-none">
          Page not found
        </h1>
        <EmptyDescription>The page you're looking for doesn't exist or has moved.</EmptyDescription>
      </EmptyHeader>
      <EmptyContent>
        <Button render={<Link to="/spaces" />} nativeButton={false}>
          Back to spaces
        </Button>
      </EmptyContent>
    </Empty>
  )
}
