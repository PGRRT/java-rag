import Chat from "@/layouts/Chat";
import React from "react";

export const SidebarOption = {
  NewChat: "NEW_CHAT",
  SearchChats: "SEARCH_CHATS",
  ChatItem: "CHAT_ITEM",
} as const;

export type SidebarOptionType = typeof SidebarOption[keyof typeof SidebarOption];

export interface SidebarItem {
  id: number;
  label: string;
  icon?: React.ElementType;
  isDisabled?: boolean;
  type: SidebarOptionType;
};

export type chatMode = "ALL" | "GLOBAL" | "PRIVATE";