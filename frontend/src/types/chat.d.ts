import type { MessageResponse } from "@/types/message";

export interface ChatResponse {
  id: string;
  title: string;
}
export interface ChatWithMessagesResponse {
  id: string;
  title: string;
  messages: MessageResponse[];
}
export interface CreateChatResponse {
  id: string;
  title: string;
}
