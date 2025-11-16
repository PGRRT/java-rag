import type { SenderType } from "@/api/enums/SenderType";

export interface MessageResponse {
  id: string;        // UUID jako string
  content: string;
  sender: SenderType;
}

export interface CreateMessageResponse {
  id: string;
  content: string;
}