import React, { lazy } from 'react';
import { createBrowserRouter } from 'react-router-dom';
import App from './App';
// import ErrorPage from './routes/ErrorPage';

const Home = lazy(() => import('./routes/Home'));

export const router = createBrowserRouter([
  {
    path: '/',
    element: <App />,
    // errorElement: <ErrorPage />,
    children: [
      { index: true, element: <Home /> },

      // {
      //   path: "auth",
      //   Component: AuthLayout,
      //   children: [
      //     { path: "login", Component: Login },
      //     { path: "register", Component: Register },
      //   ],
      // },
    ],
  },
]);