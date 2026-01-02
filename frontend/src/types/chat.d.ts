import type { MessageResponse } from "@/types/message";
import type { UUID } from "@/types/global";

export interface ChatResponse {
  id: UUID;
  title: string;
}
export interface ChatWithMessagesResponse {
  id: UUID;
  title: string;
  messages: MessageResponse[];
}
export interface CreateChatResponse {
  id: UUID;
  title: string;
}
