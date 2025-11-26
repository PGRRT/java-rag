export const Sender = {
  USER: "USER",
  BOT: "BOT",
} as const;

export type SenderType = typeof Sender[keyof typeof Sender];