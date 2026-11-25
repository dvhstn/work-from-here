import type { ReactNode } from 'react'
import { useLocation, useNavigate } from 'react-router-dom'
import { ButtonLink } from '@/components/ui/button-link'
import { cameFromList, listHrefFromState } from '../navigation'

interface BackToListLinkProps {
  children: ReactNode
  variant?: 'default' | 'ghost' | 'outline'
  size?: 'default' | 'sm'
  className?: string
}

/**
 * A real link to the list (keeping its filters and page). When the user came
 * from the list, a plain click goes back in history instead, so the browser
 * restores the list's scroll position. Modified clicks (new tab etc.) and
 * direct arrivals use the href.
 */
export function BackToListLink({ children, variant = 'default', size = 'default', className }: BackToListLinkProps) {
  const location = useLocation()
  const navigate = useNavigate()
  const fromList = cameFromList(location.state)

  return (
    <ButtonLink
      variant={variant}
      size={size}
      className={className}
      to={listHrefFromState(location.state)}
      onClick={(event) => {
        const plainClick =
          event.button === 0 && !event.metaKey && !event.ctrlKey && !event.shiftKey && !event.altKey
        if (fromList && plainClick) {
          event.preventDefault()
          navigate(-1)
        }
      }}
    >
      {children}
    </ButtonLink>
  )
}
