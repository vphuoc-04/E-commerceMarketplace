import { createRoot } from 'react-dom/client'
import './index.css'
import './App.css'

import { store } from './redux/store'
import { Provider } from 'react-redux'

import { ToastContainer } from 'react-toastify'

import {
  QueryClient,
  QueryClientProvider
} from 'react-query'

import { createBrowserRouter, RouterProvider } from 'react-router-dom'

// Middlewares
import NoAuthMiddleware from './middlewares/NoAuthMiddleware.tsx'
import AuthMiddleware from './middlewares/AuthMiddleware.tsx'

// Components
import LayoutDashboard from './components/admins/layout.tsx'
import LayoutCustomer from './components/clients/layout.tsx'

// Admin pages
import LoginDashboard from './pages/admins/Login.tsx'
import DashboardPanel from './pages/admins/Dashboard.tsx'
import { View } from './pages/admins/users/user/User.tsx'
import { Catalogue } from './pages/admins/users/catalogue/Catalogue.tsx'
import PermissionView  from './pages/admins/permissions/View.tsx'

// Customer pages
import LoginCustomer from './pages/clients/Login.tsx'
import RegisterCustomer from './pages/clients/Register.tsx'
import Home from './pages/clients/Home.tsx'

const router = createBrowserRouter([
  {
    path: '/admin/login',
    element: (
      <NoAuthMiddleware>
        <LoginDashboard />
      </NoAuthMiddleware>
    )
  },
  {
    path: '/admin',
    element: (
      <AuthMiddleware>
        <LayoutDashboard />
      </AuthMiddleware>
    ),
    children: [
      { path: 'dashboard', element: <DashboardPanel /> },

      // User
      { path: 'user/users', element: <View /> },
      { path: 'user/catalogue', element: <Catalogue /> },

      // Permission
      { path: 'permission/index', element: <PermissionView /> }
    ]
  },

  {
    path: '/login', 
    element: (
      <NoAuthMiddleware>
        <LoginCustomer />
      </NoAuthMiddleware>
    )
  },
  {
    path: '/register',
    element: (
      <NoAuthMiddleware>
        <RegisterCustomer />
      </NoAuthMiddleware>
    )
  },
  {
    path: '/',
    element: (
      <AuthMiddleware>
        <LayoutCustomer />
      </AuthMiddleware>
    ),
    children: [
      { path: '/', element: <Home /> }
    ]
  }
]);

const queryClient = new QueryClient()

createRoot(document.getElementById('root')!).render(
  <Provider store={store}>
      <QueryClientProvider client={queryClient}>
        <RouterProvider router={router} />
        <ToastContainer position='top-center' />
      </QueryClientProvider>
  </Provider>
)
