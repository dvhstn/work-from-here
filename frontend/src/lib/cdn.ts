/**
 * The only module that knows how image CDN URLs are built. Everything else
 * passes an object key and the size it wants.
 */

const DEFAULT_CDN_BASE_URL = 'http://localhost:8090'

const CDN_BASE_URL = (import.meta.env.VITE_CDN_BASE_URL?.trim() || DEFAULT_CDN_BASE_URL).replace(/\/+$/, '')

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
 * URL for `key` resized and cropped to fill `width` x `height`, with the crop
 * centred on the most interesting area. The CDN picks AVIF or WebP from the
 * browser's Accept header.
 */
export function cdnImageUrl(key: string, { width, height }: CdnImageSize): string {
  const path = key
    .replace(/^\/+/, '')
    .split('/')
    .map(encodeURIComponent)
    .join('/')
  return `${CDN_BASE_URL}/insecure/rs:fill:${toPixels(width)}:${toPixels(height)}:0/g:sm/plain/${path}`
}
