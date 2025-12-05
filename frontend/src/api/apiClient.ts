import axios from "axios";
import { store } from "@/redux/store";
import { useAppStore } from "@/redux/hooks";

const backendApi = axios.create({
  baseURL: import.meta.env.VITE_BACKEND_URL,
  timeout: 10000,
  withCredentials: true,
});

backendApi.interceptors.request.use((config) => {
  const state = store.getState();
  const token = state.auth.accessToken;

  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});

backendApi.interceptors.response.use(
  (res) => res,
  (err) => {
    if (err.response?.status === 401) {
      // window.location.href = "/login";
    }
    return Promise.reject(err);
  }
);

export default backendApi;
