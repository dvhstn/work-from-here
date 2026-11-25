import { WifiIcon, WifiOffIcon } from 'lucide-react'
import { cn } from '@/lib/utils'

export function WifiBadge({ hasWifi }: { hasWifi: boolean }) {
  const Icon = hasWifi ? WifiIcon : WifiOffIcon
  return (
    <span
      className={cn(
        'inline-flex h-6 items-center gap-1.5 rounded-full px-2.5 text-xs font-medium',
        hasWifi ? 'bg-wifi-soft text-wifi' : 'bg-muted text-muted-foreground',
      )}
    >
      <Icon aria-hidden="true" className="size-3.5" />
      {hasWifi ? 'Wi-Fi' : 'No Wi-Fi'}
    </span>
  )
}
