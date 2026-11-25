import { useCallback, useState, type CSSProperties } from 'react'
import type { SpaceImage as SpaceImageData } from '@/api/types'
import { cdnImageUrl } from '@/lib/cdn'
import { cn } from '@/lib/utils'
import { styleFor } from '../spaceTypeStyle'

/** Must match Tailwind's `md` breakpoint, which switches to `wideRatio`. */
const WIDE_MEDIA = '(min-width: 48rem)'

type LoadStatus = 'loading' | 'loaded' | 'error'

interface SpaceImageProps {
  image: SpaceImageData | null | undefined
  /** The space's type; tints the placeholder and picks the fallback icon. */
  type: unknown
  /** Width / height, e.g. `16 / 10`. */
  ratio: number
  /** Width / height from the `md` breakpoint up. Defaults to `ratio`. */
  wideRatio?: number
  /** Candidate widths for `srcset`, in pixels. */
  widths: readonly number[]
  /** The `sizes` attribute: how wide the image renders at each viewport. */
  sizes: string
  /** Above-the-fold hero: load eagerly at high priority instead of lazily. */
  priority?: boolean
  className?: string
  /** Size of the type icon in the fallback tile. */
  iconClassName?: string
}

function srcSetFor(key: string, widths: readonly number[], ratio: number): string {
  return widths.map((width) => `${cdnImageUrl(key, { width, height: width / ratio })} ${width}w`).join(', ')
}

/**
 * A space's photo, cropped to a fixed aspect ratio so it never shifts the
 * layout. A type-tinted background shows while it loads, then the photo fades
 * in (instantly with reduced motion). With no image, or if it fails to load,
 * it shows a type-coloured tile with the type icon instead.
 */
export function SpaceImage({
  image,
  type,
  ratio,
  wideRatio,
  widths,
  sizes,
  priority = false,
  className,
  iconClassName = 'size-10',
}: SpaceImageProps) {
  const style = styleFor(type)
  const key = image?.key ?? null

  // Keyed by image key, so a different image starts from 'loading' again.
  const [result, setResult] = useState<{ key: string; status: Exclude<LoadStatus, 'loading'> } | null>(null)
  const status: LoadStatus = key !== null && result?.key === key ? result.status : 'loading'

  const settle = useCallback(
    (next: Exclude<LoadStatus, 'loading'>) => {
      if (key === null) return
      setResult((prev) => (prev?.key === key && prev.status === next ? prev : { key, status: next }))
    },
    [key],
  )

  // Catches images that finished (e.g. from cache) before React saw the event.
  const imgRef = useCallback(
    (img: HTMLImageElement | null) => {
      if (img?.complete) settle(img.naturalWidth > 0 ? 'loaded' : 'error')
    },
    [settle],
  )

  const frameStyle = { '--ar': ratio, '--ar-wide': wideRatio ?? ratio } as CSSProperties
  const alt = image?.alt?.trim() ?? ''
  const showPhoto = key !== null && status !== 'error'
  const baseWidth = widths[Math.floor((widths.length - 1) / 2)] ?? 800

  return (
    <div
      style={frameStyle}
      className={cn('relative aspect-(--ar) overflow-hidden md:aspect-(--ar-wide)', style.soft, className)}
    >
      {showPhoto ? (
        <picture>
          {wideRatio !== undefined ? (
            <source media={WIDE_MEDIA} srcSet={srcSetFor(key, widths, wideRatio)} sizes={sizes} />
          ) : null}
          <img
            ref={imgRef}
            src={cdnImageUrl(key, { width: baseWidth, height: baseWidth / ratio })}
            srcSet={srcSetFor(key, widths, ratio)}
            sizes={sizes}
            width={baseWidth}
            height={Math.round(baseWidth / ratio)}
            alt={alt}
            loading={priority ? 'eager' : 'lazy'}
            decoding="async"
            fetchPriority={priority ? 'high' : undefined}
            onLoad={() => settle('loaded')}
            onError={() => settle('error')}
            data-loaded={status === 'loaded' ? '' : undefined}
            className="absolute inset-0 size-full object-cover opacity-0 data-loaded:opacity-100 motion-safe:transition-opacity motion-safe:duration-300"
          />
        </picture>
      ) : (
        <div
          // A failed photo still has a description worth hearing; a missing one is decorative.
          {...(alt ? { role: 'img', 'aria-label': alt } : { 'aria-hidden': true })}
          className={cn('absolute inset-0 flex items-center justify-center', style.tone)}
        >
          <style.Icon aria-hidden="true" className={cn('opacity-80', iconClassName)} />
        </div>
      )}
    </div>
  )
}
