import axios from "axios";

const backendApi = axios.create({
  baseURL: import.meta.env.VITE_BACKEND_URL,
  timeout: 10000,
});

// globalny interceptor
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