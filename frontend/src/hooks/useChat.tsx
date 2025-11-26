import { useAppDispatch, useAppSelector } from "@/redux/hooks";
import { addMessage, fetchMessagesAction } from "@/redux/slices/messageSlice";
import axios from "axios";
import { useEffect, useState } from "react";
import { ChatEvent } from "@/api/enums/ChatEvent";
import type { MessageResponse } from "@/api/schemas/message";
import { Sender, type SenderType } from "@/api/enums/Sender";

const formMessage = ({
  id,
  content,
  sender,
}: {
  id: string;
  content: string;
  sender: SenderType;
}): MessageResponse => ({
  id,
  content,
  sender,
});

const useChat = ({ chatId }: { chatId?: string }) => {
  const messages = useAppSelector((state) => state.message.messages);
  const dispatch = useAppDispatch();

  useEffect(() => {
    if (!chatId) return;

    let sse: EventSource | null = null;
    const loadMessages = async () => {
      dispatch(fetchMessagesAction(chatId));

      console.log("Starting loading messages");

      sse = new EventSource(
        "http://localhost:8080/api/v1/chats/" + chatId + "/stream"
      );

      console.log("sse", sse);

      sse.addEventListener(ChatEvent.USER_MESSAGE, (event) => {
        console.log("User message received:", event);
        const message: MessageResponse = formMessage({
          id: event.lastEventId,
          content: event.data,
          sender: Sender.USER,
        });

        dispatch(addMessage(message));
      });

      sse.addEventListener(ChatEvent.BOT_MESSAGE, (event) => {
        console.log("Bot message received:", event);
        const message: MessageResponse = formMessage({
          id: event.lastEventId,
          content: event.data,
          sender: Sender.BOT,
        });

        dispatch(addMessage(message));
      });

      sse.addEventListener(ChatEvent.ERROR, (event) => {
        console.error("SSE error:", event);
        sse?.close();
      });
    };

    loadMessages();

    return () => {
      if (sse) {
        sse.close();
      }
    };
  }, [chatId, dispatch]);
  return {
    messages,
  };
};

export default useChat;
