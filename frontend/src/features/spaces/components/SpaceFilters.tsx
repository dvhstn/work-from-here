import { useId, type Ref } from 'react'
import { WifiIcon, XIcon } from 'lucide-react'
import type { SpaceTypeCode } from '@/api/types'
import { Button } from '@/components/ui/button'
import { Toggle } from '@/components/ui/toggle'
import { ToggleGroup, ToggleGroupItem } from '@/components/ui/toggle-group'
import { hasActiveFilters, type SpaceListParams } from '../listParams'
import { SPACE_TYPE_LIST, isSpaceTypeCode } from '../spaceTypes'
import { SpaceTypeIcon } from './SpaceTypeBadge'

const ALL = 'ALL'

interface SpaceFiltersProps {
  params: SpaceListParams
  onTypeChange: (type: SpaceTypeCode | undefined) => void
  onWifiChange: (hasWifi: boolean) => void
  onClear: () => void
  /** Receives the "All" type toggle, so callers can return focus to it after clearing. */
  allTypesRef?: Ref<HTMLButtonElement>
}

export function SpaceFilters({ params, onTypeChange, onWifiChange, onClear, allTypesRef }: SpaceFiltersProps) {
  const typeLabelId = useId()

  return (
    <div
      role="group"
      aria-label="Filter spaces"
      className="-mx-4 flex items-center gap-x-4 gap-y-2 overflow-x-auto px-4 py-1 sm:mx-0 sm:flex-wrap sm:overflow-visible sm:px-0"
    >
      <div className="flex shrink-0 items-center gap-2">
        <span id={typeLabelId} className="text-sm font-medium text-muted-foreground">
          Type
        </span>
        <ToggleGroup
          aria-labelledby={typeLabelId}
          variant="outline"
          spacing={0}
          value={[params.type ?? ALL]}
          onValueChange={(value: string[]) => {
            // Single-select: pressing the active item again yields [], which we treat as "All".
            const next = value[0]
            onTypeChange(next && isSpaceTypeCode(next) ? next : undefined)
          }}
        >
          <ToggleGroupItem ref={allTypesRef} value={ALL}>
            All
          </ToggleGroupItem>
          {SPACE_TYPE_LIST.map((type) => (
            <ToggleGroupItem key={type.code} value={type.code}>
              <SpaceTypeIcon type={type.code} />
              {type.label}
            </ToggleGroupItem>
          ))}
        </ToggleGroup>
      </div>

      <Toggle
        variant="outline"
        className="shrink-0"
        pressed={params.hasWifi === true}
        onPressedChange={onWifiChange}
      >
        <WifiIcon aria-hidden="true" />
        Has Wi-Fi
      </Toggle>

      {hasActiveFilters(params) ? (
        <Button variant="ghost" size="sm" className="shrink-0" onClick={onClear}>
          <XIcon aria-hidden="true" data-icon="inline-start" />
          Clear filters
        </Button>
      ) : null}
    </div>
  )
}
