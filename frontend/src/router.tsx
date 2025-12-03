import React, { lazy } from "react";
import { createBrowserRouter } from "react-router-dom";
import App from "./App";
// import ErrorPage from './routes/ErrorPage';

const Home = lazy(() => import("./routes/Home"));
const SignInPage = lazy(() => import("./routes/Auth/SignInPage"));
const SignUpPage = lazy(() => import("./routes/Auth/SignUpPage"));

export const router = createBrowserRouter([
  {
    path: "/",
    element: <App />,
    // errorElement: <ErrorPage />,
    children: [
      { index: true, element: <Home /> },
      { path: "chat/:chatId", element: <Home /> },
      { path: "sign-in", element: <SignInPage /> },
      { path: "sign-up", element: <SignUpPage /> },
    ],
  },
]);
