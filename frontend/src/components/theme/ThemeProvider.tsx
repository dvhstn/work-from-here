import { useCallback, useEffect, useMemo, useState, useSyncExternalStore, type ReactNode } from 'react'
import { THEME_STORAGE_KEY, ThemeContext, isTheme, type Theme } from './themeContext'

const DARK_QUERY = '(prefers-color-scheme: dark)'

function subscribeToSystemTheme(onChange: () => void) {
  const media = window.matchMedia(DARK_QUERY)
  media.addEventListener('change', onChange)
  return () => media.removeEventListener('change', onChange)
}

function getSystemPrefersDark() {
  return window.matchMedia(DARK_QUERY).matches
}

function readStoredTheme(): Theme {
  try {
    const stored = localStorage.getItem(THEME_STORAGE_KEY)
    return isTheme(stored) ? stored : 'system'
  } catch {
    return 'system'
  }
}

/** Light/dark/system theme. Mirrors the pre-paint script in index.html. */
export function ThemeProvider({ children }: { children: ReactNode }) {
  const [theme, setThemeState] = useState<Theme>(readStoredTheme)
  const systemPrefersDark = useSyncExternalStore(subscribeToSystemTheme, getSystemPrefersDark)
  const resolvedTheme = theme === 'system' ? (systemPrefersDark ? 'dark' : 'light') : theme

  useEffect(() => {
    document.documentElement.classList.toggle('dark', resolvedTheme === 'dark')
  }, [resolvedTheme])

  const setTheme = useCallback((next: Theme) => {
    setThemeState(next)
    try {
      localStorage.setItem(THEME_STORAGE_KEY, next)
    } catch {
      // Storage can be unavailable (private mode); the choice just won't persist.
    }
  }, [])

  const value = useMemo(() => ({ theme, resolvedTheme, setTheme }), [theme, resolvedTheme, setTheme])

  return <ThemeContext.Provider value={value}>{children}</ThemeContext.Provider>
}
