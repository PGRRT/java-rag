import { StrictMode } from "react";
import { createRoot } from "react-dom/client";
import App from "./App.tsx";
import { StoreProvider } from "@/redux/StoreProvider";
import "./styles/main.scss";
import { RouterProvider } from "react-router-dom";
import { router } from "@/router.tsx";
import { MantineProvider } from "@mantine/core";

const container = document.getElementById("root");

if (container) {
  const root = createRoot(container);

  root.render(
    <StrictMode>
      <StoreProvider>
        <MantineProvider>
          <RouterProvider router={router} />
        </MantineProvider>
      </StoreProvider>
    </StrictMode>
  );
} else {
  throw new Error(
    "Root element with ID 'root' was not found in the document. Ensure there is a corresponding HTML element with the ID 'root' in your HTML file."
  );
}
