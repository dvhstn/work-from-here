/// <reference types="vite/client" />

interface ImportMetaEnv {
  /** Base URL of the image CDN (imgproxy). Defaults to http://localhost:8090. */
  readonly VITE_CDN_BASE_URL?: string
}

interface ImportMeta {
  readonly env: ImportMetaEnv
}
