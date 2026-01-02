import apiClient from "@/api/apiClient";
import type { ChatRoomType } from "@/api/enums/ChatRoom";
import type { SenderType } from "@/api/enums/Sender";
import type { ChatResponse, CreateChatResponse } from "@/types/chat";
import type { CreateMessageResponse, MessageResponse } from "@/types/message";
import type { UUID } from "@/types/global";
import type { AxiosResponse } from "axios";

export const chatApi = {
  getChats: async (
    page: string = "0",
    size: string = "10",
    sortBy = "createdAt"
  ): Promise<AxiosResponse<ChatResponse[]>> =>
    apiClient.get<ChatResponse[]>(
      `/api/v1/chats?page=${page}&size=${size}&sortBy=${sortBy}`
    ),

  createChat: async (
    title: string,
    chatType: ChatRoomType
  ): Promise<AxiosResponse<CreateChatResponse>> =>
    apiClient.post<CreateChatResponse>("/api/v1/chats", { title, chatType }),
  deleteChat: async (chatId: UUID): Promise<AxiosResponse<void>> =>
    apiClient.delete(`/api/v1/chats/${chatId}`),
  // updateChat: async (chatId: UUID, title: string): Promise<any> =>
  //   apiClient.put(`/api/v1/chats/${chatId}`, { title }),

  getMessagesForChat: async (
    chatId: UUID
  ): Promise<AxiosResponse<MessageResponse[]>> =>
    apiClient.get<MessageResponse[]>(`/api/v1/chats/${chatId}/messages`),
  postMessageForChat: async (
    chatId: UUID,
    content: string,
    sender: SenderType
  ): Promise<AxiosResponse<CreateMessageResponse>> =>
    apiClient.post<CreateMessageResponse>(`/api/v1/chats/${chatId}/messages`, {
      content,
      sender,
    }), // userId
  deleteMessageForChat: async (
    chatId: UUID,
    messageId: UUID
  ): Promise<AxiosResponse<void>> =>
    apiClient.delete(`/api/v1/chats/${chatId}/messages/${messageId}`),
};
