import { Skeleton } from '@/components/ui/skeleton'

/** Same footprint as `SpaceCard`, so loading doesn't shift the layout. */
export function SpaceCardSkeleton() {
  return (
    <div className="flex h-full flex-col gap-4 rounded-lg border bg-card p-5 shadow-xs">
      <div className="flex items-center gap-3">
        <Skeleton className="size-10 rounded-md" />
        <Skeleton className="h-6 w-16 rounded-sm" />
      </div>
      <div className="flex-1 space-y-2">
        <Skeleton className="h-5 w-3/5" />
        <Skeleton className="h-4 w-full" />
        <Skeleton className="h-4 w-4/5" />
      </div>
      <Skeleton className="h-6 w-20 rounded-full" />
    </div>
  )
}
