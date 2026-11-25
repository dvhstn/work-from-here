import { Skeleton } from '@/components/ui/skeleton'

/** Same footprint as `SpaceCard`, so loading doesn't shift the layout. */
export function SpaceCardSkeleton() {
  return (
    <div className="flex h-full flex-col overflow-hidden rounded-lg border bg-card shadow-xs">
      <Skeleton className="aspect-[16/10] rounded-none border-b" />
      <div className="flex flex-1 flex-col gap-4 p-5">
        <Skeleton className="h-6 w-16 rounded-sm" />
        <div className="flex-1 space-y-2">
          <Skeleton className="h-5 w-3/5" />
          <Skeleton className="h-4 w-full" />
          <Skeleton className="h-4 w-4/5" />
        </div>
        <Skeleton className="h-6 w-20 rounded-full" />
      </div>
    </div>
  )
}
