import { QueryClient } from '@tanstack/react-query'
import { ApiError } from '@/api/errors'

const MAX_RETRIES = 2

export const queryClient = new QueryClient({
  defaultOptions: {
    queries: {
      staleTime: 30_000,
      // Only retry failures that might be transient. 4xx responses and bugs
      // (e.g. an unexpected payload shape) fail the same way every time.
      retry: (failureCount, error) =>
        failureCount < MAX_RETRIES &&
        error instanceof ApiError &&
        (error.isUnreachable || error.status >= 500),
    },
  },
})
