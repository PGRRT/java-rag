import axios from "axios";

const aiApi = axios.create({
  baseURL: import.meta.env.VITE_AI_URL,
  timeout: 10000,              
});

export default aiApi;

