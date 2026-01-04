import { Outlet, useLocation } from "react-router-dom";
import { Toaster } from "sonner";
import { useEffect } from "react";
import { initializeAuth } from "@/api/apiClient";

function App() {
  const location = useLocation();

  useEffect(() => {
    // Do not initialize auth on sign-in or sign-up pages
    const isAuthPage =
      location.pathname === "/sign-in" || location.pathname === "/sign-up";

    if (!isAuthPage) {
      initializeAuth();
    }
  }, [location.pathname]);

  return (
    <>
      <Toaster />
      <Outlet />
    </>
  );
}

export default App;
