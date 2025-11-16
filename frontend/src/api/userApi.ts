// import { apiClientBrowser } from "@/lib/api/backendApi";
// import { Credentials, RegisterData, User } from "@/types/user";

import backendApi from "@/api/backendApi";
import type { ChatType } from "@/api/enums/ChatType";
import type { SenderType } from "@/api/enums/SenderType";
import type { ChatResponse, CreateChatResponse } from "@/api/schemas/chat";
import type { CreateMessageResponse, MessageResponse } from "@/api/schemas/message";

export const userApi = {
  getChats: async (): Promise<ChatResponse[]> => backendApi.get("/api/v1/chats"),
  createChat: async (title: string, chatType: ChatType): Promise<CreateChatResponse> => backendApi.post("/api/v1/chats", { title, chatType }),
  deleteChat: async (chatId: string): Promise<void> => backendApi.delete(`/api/v1/chats/${chatId}`),
  // updateChat: async (chatId: string, title: string): Promise<any> =>
  //   backendApi.put(`/api/v1/chats/${chatId}`, { title }),

  getMessages: async (chatId: string): Promise<MessageResponse[]> => backendApi.get(`/api/v1/chats/${chatId}/messages`),
  postMessage: async (chatId: string, content: string, sender: SenderType): Promise<CreateMessageResponse> =>
    backendApi.post(`/api/v1/chats/${chatId}/messages`, { content, sender }), // userId
  deleteMessage: async (chatId: string, messageId: string): Promise<void> =>
    backendApi.delete(`/api/v1/chats/${chatId}/messages/${messageId}`),

  // getUserClient: async () => apiClientBrowser.get("/api/v1/auth/me"),

  // loginUserClient: async (data: Credentials) => apiClientBrowser.post("/api/v1/auth/login", data),
  // saveUserClient: async (data: RegisterData) => apiClientBrowser.post("/api/v1/auth/register", data),
  // logoutUserClient: async () => apiClientBrowser.post("/api/v1/auth/logout"),
  // refreshUserClient: async () => apiClientBrowser.get("/api/v1/auth/refresh"),

  // createEmailVerificationPassword: async (email: string) => apiClientBrowser.post("/api/v1/user/create-otp", { email }),
};
