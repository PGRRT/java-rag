import type { SenderType } from "@/api/enums/Sender";
import type { UUID } from "@/types/global";

export interface MessageResponse {
  id: UUID;
  content: string;
  sender: SenderType;
}

export interface CreateMessageResponse {
  id: UUID;
  content: string;
}
