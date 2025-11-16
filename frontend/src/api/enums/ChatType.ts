const ChatType = {
  GLOBAL: "GLOBAL",
  PRIVATE: "PRIVATE",
} as const;

export type ChatType = typeof ChatType[keyof typeof ChatType];