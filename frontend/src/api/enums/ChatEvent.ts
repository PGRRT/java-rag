export const ChatEvent = {
    USER_MESSAGE: "USER_MESSAGE",
    BOT_MESSAGE: "BOT_MESSAGE",
    ERROR: "ERROR"
} as const;

export type ChatType = typeof ChatEvent[keyof typeof ChatEvent];