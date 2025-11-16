const SenderType = {
  USER: "USER",
  BOT: "BOT",
} as const;

export type SenderType = typeof SenderType[keyof typeof SenderType];