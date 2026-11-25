import { useEffect, useId, useRef, useState } from 'react'
import { CheckIcon, CopyIcon, EyeIcon, EyeOffIcon, WifiIcon, WifiOffIcon } from 'lucide-react'
import { toast } from 'sonner'
import { Button } from '@/components/ui/button'

/** Fixed-length mask, so the hidden value doesn't reveal the password's length. */
const MASK = '••••••••••'
const COPIED_RESET_MS = 2000

function PasswordValue({ password }: { password: string }) {
  const [revealed, setRevealed] = useState(false)
  const [copied, setCopied] = useState(false)
  const resetTimer = useRef<number | undefined>(undefined)

  useEffect(() => () => window.clearTimeout(resetTimer.current), [])

  const copy = async () => {
    try {
      if (!navigator.clipboard) throw new Error('Clipboard unavailable')
      await navigator.clipboard.writeText(password)
      setCopied(true)
      toast.success('Password copied')
      window.clearTimeout(resetTimer.current)
      resetTimer.current = window.setTimeout(() => setCopied(false), COPIED_RESET_MS)
    } catch {
      toast.error("Couldn't copy the password", {
        description: 'Show it and copy it manually instead.',
      })
    }
  }

  return (
    <div className="flex flex-col gap-3 sm:flex-row sm:items-center sm:justify-between">
      <dl className="min-w-0 space-y-1">
        <dt className="text-sm font-medium text-muted-foreground">Password</dt>
        <dd className="font-mono text-base tracking-wide break-all">
          {revealed ? (
            password
          ) : (
            <>
              <span aria-hidden="true">{MASK}</span>
              <span className="sr-only">Hidden</span>
            </>
          )}
        </dd>
      </dl>

      <div className="flex shrink-0 items-center gap-2">
        {/*
          The label itself says what the button does next ("Show" / "Hide"), so
          there's no aria-pressed: both together would describe the state twice.
        */}
        <Button variant="outline" size="sm" onClick={() => setRevealed((value) => !value)}>
          {revealed ? (
            <EyeOffIcon aria-hidden="true" data-icon="inline-start" />
          ) : (
            <EyeIcon aria-hidden="true" data-icon="inline-start" />
          )}
          {revealed ? 'Hide' : 'Show'}
          <span className="sr-only"> password</span>
        </Button>
        <Button variant="outline" size="sm" onClick={() => void copy()}>
          {copied ? (
            <CheckIcon aria-hidden="true" data-icon="inline-start" className="text-wifi" />
          ) : (
            <CopyIcon aria-hidden="true" data-icon="inline-start" />
          )}
          {copied ? 'Copied' : 'Copy'}
          <span className="sr-only"> password</span>
        </Button>
      </div>
    </div>
  )
}

/**
 * Wi-Fi section of the detail page. A string is shown as a password, `null`
 * means no Wi-Fi is listed, and `undefined` (field omitted by the API) means
 * we don't know, so nothing is offered to reveal or copy.
 */
export function WifiPasswordPanel({ password }: { password: string | null | undefined }) {
  const headingId = useId()
  const hasWifi = typeof password === 'string'
  const Icon = hasWifi ? WifiIcon : WifiOffIcon

  return (
    <section aria-labelledby={headingId} className="rounded-lg border bg-card p-5 shadow-xs">
      <div className="flex items-start gap-3">
        <span
          className={
            hasWifi
              ? 'flex size-9 shrink-0 items-center justify-center rounded-md bg-wifi-soft text-wifi'
              : 'flex size-9 shrink-0 items-center justify-center rounded-md bg-muted text-muted-foreground'
          }
        >
          <Icon aria-hidden="true" className="size-4" />
        </span>
        <div className="min-w-0 flex-1 space-y-3">
          <h2 id={headingId} className="pt-1.5 text-base font-semibold tracking-tight">
            Wi-Fi
          </h2>
          {hasWifi ? (
            <PasswordValue password={password} />
          ) : (
            <p className="text-sm text-muted-foreground">
              {password === null ? 'No Wi-Fi listed for this space.' : "Wi-Fi details aren't available."}
            </p>
          )}
        </div>
      </div>
    </section>
  )
}
