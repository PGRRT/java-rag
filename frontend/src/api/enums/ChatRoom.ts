export const ChatRoom  = {
  GLOBAL: "GLOBAL",
  PRIVATE: "PRIVATE",
} as const;

export type ChatRoomType = typeof ChatRoom[keyof typeof ChatRoom];