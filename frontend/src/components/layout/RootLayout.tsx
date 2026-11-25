import { CoffeeIcon, PlusIcon } from 'lucide-react'
import { Link, Outlet, ScrollRestoration } from 'react-router-dom'
import { ThemeToggle } from '@/components/theme/ThemeToggle'
import { Button } from '@/components/ui/button'
import { Toaster } from '@/components/ui/sonner'
import { useFocusOnNavigate } from '@/hooks/useFocusOnNavigate'
import { useMediaQuery } from '@/hooks/useMediaQuery'

export function RootLayout() {
  useFocusOnNavigate()
  // Matches Tailwind's `sm` breakpoint.
  const isWide = useMediaQuery('(min-width: 40rem)')

  return (
    <div className="flex min-h-svh flex-col">
      <a
        href="#main"
        className="sr-only z-50 rounded-md bg-primary px-4 py-2 text-sm font-medium text-primary-foreground focus:not-sr-only focus:fixed focus:top-3 focus:left-3"
      >
        Skip to content
      </a>

      <header className="sticky top-0 z-40 border-b bg-background/80 backdrop-blur supports-backdrop-filter:bg-background/70">
        <div className="mx-auto flex h-14 max-w-6xl items-center justify-between gap-4 px-4 sm:px-6">
          <Link
            to="/spaces"
            className="-mx-2 flex items-center gap-2 rounded-md px-2 py-1 font-semibold tracking-tight outline-none focus-visible:ring-[3px] focus-visible:ring-ring"
          >
            <CoffeeIcon className="size-5 text-primary" aria-hidden="true" />
            Work From Here
          </Link>

          <nav aria-label="Primary" className="flex items-center gap-1 sm:gap-2">
            <Button render={<Link to="/spaces/new" />} nativeButton={false} className="hidden sm:inline-flex">
              <PlusIcon aria-hidden="true" data-icon="inline-start" />
              Add space
            </Button>
            <Button
              render={<Link to="/spaces/new" />}
              nativeButton={false}
              size="icon"
              className="sm:hidden"
              aria-label="Add space"
            >
              <PlusIcon aria-hidden="true" />
            </Button>
            <ThemeToggle />
          </nav>
        </div>
      </header>

      <main id="main" tabIndex={-1} className="mx-auto w-full max-w-6xl flex-1 px-4 py-8 outline-none sm:px-6">
        <Outlet />
      </main>

      <Toaster position={isWide ? 'bottom-right' : 'bottom-center'} />
      <ScrollRestoration />
    </div>
  )
}
