import { Navigate, createBrowserRouter } from 'react-router-dom'
import { RootLayout } from '@/components/layout/RootLayout'
import { SpacesListPage } from '@/features/spaces/pages/SpacesListPage'
import { NotFoundPage } from '@/pages/NotFoundPage'
import { PlaceholderPage } from '@/pages/PlaceholderPage'

export const router = createBrowserRouter([
  {
    element: <RootLayout />,
    children: [
      { index: true, element: <Navigate to="/spaces" replace /> },
      { path: 'spaces', element: <SpacesListPage /> },
      { path: 'spaces/new', element: <PlaceholderPage title="Add a space" /> },
      { path: 'spaces/:id', element: <PlaceholderPage title="Space details" /> },
      { path: 'spaces/:id/edit', element: <PlaceholderPage title="Edit space" /> },
      { path: '*', element: <NotFoundPage /> },
    ],
  },
])
