import { StrictMode } from "react";
import { createRoot } from "react-dom/client";
import { StoreProvider } from "@/redux/StoreProvider";
import { RouterProvider } from "react-router-dom";
import { router } from "@/router.tsx";
import { MantineProvider } from "@mantine/core";
import { mantineTheme } from "@/config/mantineConfigStyles";

import "@mantine/core/styles.css";
import "./styles/main.scss";
import "@/i18n";

const container = document.getElementById("root");

if (container) {
  const root = createRoot(container);

  root.render(
    <StrictMode>
      <StoreProvider>
        <MantineProvider theme={mantineTheme}>
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
