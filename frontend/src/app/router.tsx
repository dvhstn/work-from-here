import { Navigate, createBrowserRouter } from 'react-router-dom'
import { RootLayout } from '@/components/layout/RootLayout'
import { SpacesListPage } from '@/features/spaces/pages/SpacesListPage'
import { NotFoundPage } from '@/pages/NotFoundPage'
import { PlaceholderPage } from '@/pages/PlaceholderPage'
import { RouteErrorPage } from '@/pages/RouteErrorPage'

export const router = createBrowserRouter([
  {
    element: <RootLayout />,
    // Last resort if the shell itself fails to render.
    errorElement: (
      <main className="mx-auto max-w-6xl px-4 py-16 sm:px-6">
        <RouteErrorPage />
      </main>
    ),
    children: [
      {
        // Page-level errors render inside the shell, so the header stays usable.
        errorElement: <RouteErrorPage />,
        children: [
          { index: true, element: <Navigate to="/spaces" replace /> },
          { path: 'spaces', element: <SpacesListPage /> },
          { path: 'spaces/new', element: <PlaceholderPage title="Add a space" /> },
          { path: 'spaces/:id', element: <PlaceholderPage title="Space details" /> },
          { path: 'spaces/:id/edit', element: <PlaceholderPage title="Edit space" /> },
          { path: '*', element: <NotFoundPage /> },
        ],
      },
    ],
  },
])
