import { MonitorIcon, MoonIcon, SunIcon } from 'lucide-react'
import { Button } from '@/components/ui/button'
import {
  DropdownMenu,
  DropdownMenuContent,
  DropdownMenuRadioGroup,
  DropdownMenuRadioItem,
  DropdownMenuTrigger,
} from '@/components/ui/dropdown-menu'
import { isTheme, useTheme } from './themeContext'

const OPTIONS = [
  { value: 'light', label: 'Light', Icon: SunIcon },
  { value: 'dark', label: 'Dark', Icon: MoonIcon },
  { value: 'system', label: 'System', Icon: MonitorIcon },
] as const

export function ThemeToggle() {
  const { theme, resolvedTheme, setTheme } = useTheme()
  const CurrentIcon = resolvedTheme === 'dark' ? MoonIcon : SunIcon
  const currentLabel = OPTIONS.find((option) => option.value === theme)?.label ?? 'System'

  return (
    <DropdownMenu>
      <DropdownMenuTrigger
        render={<Button variant="ghost" size="icon" aria-label={`Theme: ${currentLabel}. Change theme`} />}
      >
        <CurrentIcon aria-hidden="true" />
      </DropdownMenuTrigger>
      <DropdownMenuContent align="end" className="min-w-36">
        <DropdownMenuRadioGroup
          value={theme}
          onValueChange={(value: unknown) => {
            if (isTheme(value)) setTheme(value)
          }}
        >
          {OPTIONS.map(({ value, label, Icon }) => (
            <DropdownMenuRadioItem key={value} value={value}>
              <Icon aria-hidden="true" />
              {label}
            </DropdownMenuRadioItem>
          ))}
        </DropdownMenuRadioGroup>
      </DropdownMenuContent>
    </DropdownMenu>
  )
}
