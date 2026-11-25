import { ArrowLeftIcon } from 'lucide-react'
import { PageHeader } from '@/components/layout/PageHeader'
import { ButtonLink } from '@/components/ui/button-link'

/** Temporary stand-in for routes that haven't been built yet. */
export function PlaceholderPage({ title }: { title: string }) {
  return (
    <div className="space-y-6">
      <ButtonLink to="/spaces" variant="ghost" size="sm" className="-ml-3">
        <ArrowLeftIcon aria-hidden="true" data-icon="inline-start" />
        All spaces
      </ButtonLink>
      <PageHeader title={title} description="This page is coming soon." />
    </div>
  )
}
