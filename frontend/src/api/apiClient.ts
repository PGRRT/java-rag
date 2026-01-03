import axios from "axios";
import { store } from "@/redux/store";
import { setAccessToken, setUser } from "@/redux/slices/authSlice";

const backendApi = axios.create({
  baseURL: "http://localhost:8080",
  // baseURL: import.meta.env.VITE_BACKEND_URL,
  timeout: 10000,
  withCredentials: true,
});

// Request queue and state flags
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

// Initialize auth on app startup
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
    isInitialized = true; // Always set to true to avoid blocking other requests
    processQueue(); // Release queue regardless of result
    isRefreshing = false;
  }
};

// Request interceptor - adds token and waits for initialization
backendApi.interceptors.request.use(
  async (config) => {
    // Skip initialization for refresh and auth endpoints
    const skipInit =
      config.url?.includes("/auth/refresh") ||
      config.url?.includes("/auth/login") ||
      config.url?.includes("/auth/register");

    // Wait only if refresh is currently in progress
    if (!skipInit && isRefreshing) {
      console.log("[Auth] Request queued, waiting for refresh to complete...");
      await new Promise((resolve, reject) => {
        failedQueue.push({ resolve, reject });
      });
    }

    // Add token if it exists
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

// Response interceptor - automatic refresh on 401
backendApi.interceptors.response.use(
  (res) => res,
  async (err) => {
    const originalRequest = err.config;

    if (originalRequest?.url?.includes("/auth/refresh")) {
      return Promise.reject(err);
    }

    // If 401 and not already retrying
    if (err.response?.status === 401 && !originalRequest._retry) {
      if (
        window.location.pathname === "/sign-in" ||
        window.location.pathname === "/sign-up"
      ) {
        return Promise.reject(err);
      }

      console.log("[Auth] 401 Unauthorized - attempting token refresh");

      // If refresh is already in progress, add to queue
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

        // Save new token in store (response.data is AuthResponse)
        store.dispatch(setAccessToken(newAccessToken));

        // Add new token to original request
        originalRequest.headers.Authorization = `Bearer ${newAccessToken}`;

        processQueue();
        console.log("[Auth] Token refreshed successfully, retrying request");

        // Retry original request
        return backendApi(originalRequest);
      } catch (refreshError) {
        console.error("[Auth] Token refresh failed:", refreshError);

        // Reject all requests in queue
        processQueue(refreshError);

        // Reset state
        store.dispatch(setAccessToken(null));
        store.dispatch(setUser(null));
        isInitialized = false;

        // Redirect to login (optional)
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

    // Log other errors to console only
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
