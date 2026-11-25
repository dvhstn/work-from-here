const CC_VERSIONS = new Set(['1.0', '2.0', '2.5', '3.0', '4.0'])

/**
 * Creative Commons deed for a licence name as stored by the image ingest,
 * e.g. "CC BY-SA 4.0" or "CC0". Returns `null` for anything it doesn't
 * recognise, so callers show the name as plain text.
 */
export function licenseUrl(license: string | null | undefined): string | null {
  const name = license?.trim().replace(/\s+/g, ' ').toUpperCase()
  if (!name) return null
  if (/^CC0( 1\.0)?$/.test(name)) return 'https://creativecommons.org/publicdomain/zero/1.0/'

  const match = /^CC (BY(?:-SA|-ND|-NC|-NC-SA|-NC-ND)?) (\d\.\d)$/.exec(name)
  if (!match || !CC_VERSIONS.has(match[2])) return null
  return `https://creativecommons.org/licenses/${match[1].toLowerCase()}/${match[2]}/`
}
