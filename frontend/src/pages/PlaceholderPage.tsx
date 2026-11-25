import { ArrowLeftIcon } from 'lucide-react'
import { Link } from 'react-router-dom'
import { PageHeader } from '@/components/layout/PageHeader'
import { Button } from '@/components/ui/button'

/** Temporary stand-in for routes that haven't been built yet. */
export function PlaceholderPage({ title }: { title: string }) {
  return (
    <div className="space-y-6">
      <Button render={<Link to="/spaces" />} nativeButton={false} variant="ghost" size="sm" className="-ml-3">
        <ArrowLeftIcon aria-hidden="true" data-icon="inline-start" />
        All spaces
      </Button>
      <PageHeader title={title} description="This page is coming soon." />
    </div>
  )
}
