import { useEffect, useRef } from 'react'
import { useLocation } from 'react-router-dom'

/**
 * Moves focus to the page's h1 when the path changes, so screen readers
 * announce the new page. Search-param changes (filters, paging) are ignored,
 * and the first render keeps the browser's default focus.
 */
export function useFocusOnNavigate() {
  const { pathname } = useLocation()
  const previousPath = useRef(pathname)

  useEffect(() => {
    if (previousPath.current === pathname) return
    previousPath.current = pathname
    const heading = document.querySelector<HTMLElement>('main h1')
    heading?.focus({ preventScroll: true })
  }, [pathname])
}
