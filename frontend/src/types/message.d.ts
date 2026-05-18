import type { SenderType } from "@/api/enums/Sender";
import type { UUID } from "@/types/global";

export interface MessageResponse {
  id: UUID;
  content: string;
  sender: SenderType;
  user_questions?: string | string[];
  final_response?: string;
  step?: string;
  thoughts?: string;
  messages?: any[];
}

export interface CreateMessageResponse {
  id: UUID;
  content: string;
}
