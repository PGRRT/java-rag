// import { apiClientBrowser } from "@/lib/api/backendApi";
// import { Credentials, RegisterData, User } from "@/types/user";

import backendApi from "@/api/backendApi";

export const userApi = {
  postChatMessage: async (message: string): Promise<void> =>
    backendApi.post("/api/v1/chats/message", { message }),

  // getUserClient: async () => apiClientBrowser.get("/api/v1/auth/me"),

  // loginUserClient: async (data: Credentials) => apiClientBrowser.post("/api/v1/auth/login", data),
  // saveUserClient: async (data: RegisterData) => apiClientBrowser.post("/api/v1/auth/register", data),
  // logoutUserClient: async () => apiClientBrowser.post("/api/v1/auth/logout"),
  // refreshUserClient: async () => apiClientBrowser.get("/api/v1/auth/refresh"),

  // createEmailVerificationPassword: async (email: string) => apiClientBrowser.post("/api/v1/user/create-otp", { email }),
};
