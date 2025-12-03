// import { apiClientBrowser } from "@/lib/api/apiClient";
// import { Credentials, RegisterData, User } from "@/types/user";

import apiClient from "@/api/apiClient";
import type { ChatRoomType } from "@/api/enums/ChatRoom";
import type { SenderType } from "@/api/enums/Sender";
import type { ChatResponse, CreateChatResponse } from "@/types/chat";
import type { CreateMessageResponse, MessageResponse } from "@/types/message";
import type { AxiosResponse } from "axios";

export const chatApi = {
  getChats: async (): Promise<AxiosResponse<ChatResponse[]>> =>
    apiClient.get<ChatResponse[]>("/api/v1/chats"),
  
  createChat: async (
    title: string,
    chatType: ChatRoomType
  ): Promise<AxiosResponse<CreateChatResponse>> =>
    apiClient.post<CreateChatResponse>("/api/v1/chats", { title, chatType }),
  deleteChat: async (chatId: string): Promise<AxiosResponse<void>> =>
    apiClient.delete(`/api/v1/chats/${chatId}`),
  // updateChat: async (chatId: string, title: string): Promise<any> =>
  //   apiClient.put(`/api/v1/chats/${chatId}`, { title }),

  getMessagesForChat: async (
    chatId: string
  ): Promise<AxiosResponse<MessageResponse[]>> =>
    apiClient.get<MessageResponse[]>(`/api/v1/chats/${chatId}/messages`),
  postMessageForChat: async (
    chatId: string,
    content: string,
    sender: SenderType
  ): Promise<AxiosResponse<CreateMessageResponse>> =>
    apiClient.post<CreateMessageResponse>(`/api/v1/chats/${chatId}/messages`, {
      content,
      sender,
    }), // userId
  deleteMessageForChat: async (
    chatId: string,
    messageId: string
  ): Promise<AxiosResponse<void>> =>
    apiClient.delete(`/api/v1/chats/${chatId}/messages/${messageId}`),
};
