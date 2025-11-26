// import { apiClientBrowser } from "@/lib/api/backendApi";
// import { Credentials, RegisterData, User } from "@/types/user";

import backendApi from "@/api/backendApi";
import type { ChatRoomType } from "@/api/enums/ChatRoom";
import type { SenderType } from "@/api/enums/Sender";
import type { ChatResponse, CreateChatResponse } from "@/api/schemas/chat";
import type {
  CreateMessageResponse,
  MessageResponse,
} from "@/api/schemas/message";
import type { AxiosResponse } from "axios";

export const userApi = {
  getChats: async (): Promise<AxiosResponse<ChatResponse[]>> =>
    backendApi.get<ChatResponse[]>("/api/v1/chats"),
  createChat: async (
    title: string,
    chatType: ChatRoomType
  ): Promise<AxiosResponse<CreateChatResponse>> =>
    backendApi.post<CreateChatResponse>("/api/v1/chats", { title, chatType }),
  deleteChat: async (chatId: string): Promise<AxiosResponse<void>> =>
    backendApi.delete(`/api/v1/chats/${chatId}`),
  // updateChat: async (chatId: string, title: string): Promise<any> =>
  //   backendApi.put(`/api/v1/chats/${chatId}`, { title }),

  getMessagesForChat: async (
    chatId: string
  ): Promise<AxiosResponse<MessageResponse[]>> =>
    backendApi.get<MessageResponse[]>(`/api/v1/chats/${chatId}/messages`),
  postMessageForChat: async (
    chatId: string,
    content: string,
    sender: SenderType
  ): Promise<AxiosResponse<CreateMessageResponse>> =>
    backendApi.post<CreateMessageResponse>(`/api/v1/chats/${chatId}/messages`, {
      content,
      sender,
    }), // userId
  deleteMessageForChat: async (
    chatId: string,
    messageId: string
  ): Promise<AxiosResponse<void>> =>
    backendApi.delete(`/api/v1/chats/${chatId}/messages/${messageId}`),

  // getUserClient: async () => apiClientBrowser.get("/api/v1/auth/me"),

  // loginUserClient: async (data: Credentials) => apiClientBrowser.post("/api/v1/auth/login", data),
  // saveUserClient: async (data: RegisterData) => apiClientBrowser.post("/api/v1/auth/register", data),
  // logoutUserClient: async () => apiClientBrowser.post("/api/v1/auth/logout"),
  // refreshUserClient: async () => apiClientBrowser.get("/api/v1/auth/refresh"),

  // createEmailVerificationPassword: async (email: string) => apiClientBrowser.post("/api/v1/user/create-otp", { email }),
};
