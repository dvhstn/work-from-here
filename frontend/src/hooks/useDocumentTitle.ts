import { useEffect } from 'react'

const APP_NAME = 'Work From Here'

/** Sets `<title>` to "{title} · Work From Here" and restores the app name on unmount. */
export function useDocumentTitle(title: string | undefined) {
  useEffect(() => {
    document.title = title ? `${title} · ${APP_NAME}` : APP_NAME
  }, [title])

  useEffect(
    () => () => {
      document.title = APP_NAME
    },
    [],
  )
}
