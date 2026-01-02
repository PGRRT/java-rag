import axios from "axios";
import { store } from "@/redux/store";
import { setAccessToken, setUser } from "@/redux/slices/authSlice";

const backendApi = axios.create({
  baseURL: "http://localhost:8080",
  // baseURL: import.meta.env.VITE_BACKEND_URL,
  timeout: 10000,
  withCredentials: true,
});

// Kolejka requestów i flagi stanu
let isRefreshing = false;
let isInitialized = false;
let failedQueue: Array<{
  resolve: (value?: unknown) => void;
  reject: (reason?: any) => void;
}> = [];

const processQueue = (error: any = null) => {
  failedQueue.forEach((prom) => {
    if (error) {
      prom.reject(error);
    } else {
      prom.resolve();
    }
  });
  failedQueue = [];
};

// Inicjalizacja auth przy starcie aplikacji
export const initializeAuth = async (): Promise<boolean> => {
  if (isInitialized) return true;

  isRefreshing = true;
  try {
    console.log("[Auth] Initializing authentication...");
    const response = await backendApi.post("/api/v1/auth/refresh");
    
    store.dispatch(setAccessToken(response.data));

    console.log("[Auth] Authentication initialized successfully");
    return true;
  } catch (error) {
    console.warn(
      "[Auth] Failed to initialize authentication, continuing without token:",
      error
    );
    return false;
  } finally {
    isInitialized = true; // Zawsze ustawiamy na true, aby nie blokować innych requestów
    processQueue(); // Zwolnij kolejkę niezależnie od wyniku
    isRefreshing = false;
  }
};

// Request interceptor - dodaje token i czeka na inicjalizację
backendApi.interceptors.request.use(
  async (config) => {
    // Pomiń inicjalizację dla endpointu refresh i auth
    const skipInit =
      config.url?.includes("/auth/refresh") ||
      config.url?.includes("/auth/login") ||
      config.url?.includes("/auth/register");

    // Czekaj tylko jeśli refresh trwa w tym momencie
    if (!skipInit && isRefreshing) {
      console.log("[Auth] Request queued, waiting for refresh to complete...");
      await new Promise((resolve, reject) => {
        failedQueue.push({ resolve, reject });
      });
    }

    // Dodaj token jeśli istnieje
    const state = store.getState();
    const token = state.auth.accessToken;

    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }

    return config;
  },
  (error) => {
    console.error("[Auth] Request interceptor error:", error);
    return Promise.reject(error);
  }
);

// Response interceptor - automatyczny refresh przy 401
backendApi.interceptors.response.use(
  (res) => res,
  async (err) => {
    const originalRequest = err.config;

    // Jeśli 401 i nie próbujemy już ponownie
    if (err.response?.status === 401 && !originalRequest._retry) {
      if (
        window.location.pathname === "/sign-in" ||
        window.location.pathname === "/sign-up"
      ) {
        return Promise.reject(err);
      }
      
      console.log("[Auth] 401 Unauthorized - attempting token refresh");

      // Jeśli już trwa refresh, dodaj do kolejki
      if (isRefreshing) {
        console.log("[Auth] Refresh in progress, queuing request...");
        return new Promise((resolve, reject) => {
          failedQueue.push({ resolve, reject });
        })
          .then(() => {
            const state = store.getState();
            originalRequest.headers.Authorization = `Bearer ${state.auth.accessToken}`;
            console.log("[Auth] Retrying request after token refresh");
            return backendApi(originalRequest);
          })
          .catch((err) => {
            console.error("[Auth] Failed to retry request:", err);
            return Promise.reject(err);
          });
      }

      originalRequest._retry = true;
      isRefreshing = true;

      try {
        console.log("[Auth] Refreshing access token...");
        const response = await backendApi.post("/api/v1/auth/refresh");
        const newAccessToken = response.data;

        // Zapisz nowy token w store (response.data to AuthResponse)
        store.dispatch(setAccessToken(newAccessToken));

        // Dodaj nowy token do oryginalnego requesta
        originalRequest.headers.Authorization = `Bearer ${newAccessToken}`;

        // Przetwórz kolejkę
        processQueue();
        console.log("[Auth] Token refreshed successfully, retrying request");

        // Powtórz oryginalny request
        return backendApi(originalRequest);
      } catch (refreshError) {
        console.error("[Auth] Token refresh failed:", refreshError);

        // Odrzuć wszystkie w kolejce
        processQueue(refreshError);

        // Reset stanu
        store.dispatch(setAccessToken(null));
        store.dispatch(setUser(null));
        isInitialized = false;

        // Przekieruj do logowania (opcjonalnie)
        if (
          window.location.pathname !== "/sign-in" &&
          window.location.pathname !== "/sign-up"
        ) {
          console.log("[Auth] Redirecting to login...");
          window.location.href = "/sign-in";
        }

        return Promise.reject(refreshError);
      } finally {
        isRefreshing = false;
      }
    }

    // Loguj inne błędy tylko w konsoli
    if (err.response) {
      console.error(
        `[API Error] ${err.config?.method?.toUpperCase()} ${err.config?.url}:`,
        {
          status: err.response.status,
          data: err.response.data,
        }
      );
    } else if (err.request) {
      console.error("[API Error] No response received:", err.request);
    } else {
      console.error("[API Error]", err.message);
    }

    return Promise.reject(err);
  }
);

export default backendApi;
