/**
 * The only module that knows how image CDN URLs are built. Everything else
 * passes an object key and the size it wants.
 */

/** Local imgproxy from the dev stack. Never used by production builds. */
const DEV_CDN_BASE_URL = 'http://localhost:8090'

function resolveBaseUrl(): string {
  const configured = import.meta.env.VITE_CDN_BASE_URL?.trim()
  if (configured) return configured.replace(/\/+$/, '')
  if (import.meta.env.DEV) return DEV_CDN_BASE_URL
  // A production build without a CDN must not point visitors at their own
  // localhost. Say so once, then use same-origin paths so a reverse proxy can
  // still serve them.
  console.error(
    'VITE_CDN_BASE_URL is not set for this build, so space photos are requested from this site. Set it at build time.',
  )
  return ''
}

const CDN_BASE_URL = resolveBaseUrl()

export interface CdnImageSize {
  /** Output width in CSS pixels at 1x; rounded to a whole pixel. */
  width: number
  /** Output height; the image is cropped to fill `width` x `height`. */
  height: number
}

function toPixels(value: number): number {
  return Math.max(1, Math.round(value))
}

/**
 * URL for `key` resized and cropped to fill exactly `width` x `height`, with
 * the crop centred on the most interesting area. Enlarging is on, so a source
 * smaller than the request is still cropped and every `srcset` width is real. The CDN picks AVIF or WebP from the
 * browser's Accept header.
 */
export function cdnImageUrl(key: string, { width, height }: CdnImageSize): string {
  const path = key
    .replace(/^\/+/, '')
    .split('/')
    .map(encodeURIComponent)
    .join('/')
  return `${CDN_BASE_URL}/insecure/rs:fill:${toPixels(width)}:${toPixels(height)}:1/g:sm/plain/${path}`
}
