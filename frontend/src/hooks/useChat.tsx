import { useAppDispatch, useAppSelector } from "@/redux/hooks";
import { addMessage, fetchMessagesAction } from "@/redux/slices/messageSlice";
import axios from "axios";
import { useEffect, useState } from "react";
import { ChatEvent } from "@/api/enums/ChatEvent";
import type { MessageResponse } from "@/types/message";
import { Sender, type SenderType } from "@/api/enums/Sender";
import type { UUID } from "@/types/global";

const formMessage = ({
  id,
  content,
  sender,
  user_questions,
  final_response,
  step,
  thoughts,
}: {
  id: string;
  content: string;
  sender: SenderType;
  user_questions?: string | string[];
  final_response?: string;
  step?: string;
  thoughts?: string;
}): MessageResponse => ({
  id,
  content,
  sender,
  user_questions,
  final_response,
  step,
  thoughts,
});

const connectSse = ({
  chatId,
  addMessage,
}: {
  chatId: string;
  addMessage: (message: MessageResponse) => void;
}) => {
  const url = `http://localhost:8080/api/v1/chats/${chatId}/stream`;
  let sse: EventSource | null = null;
  const retries = 0;
  const maxRetries = 4;
  let retryTimer: number | null = null;
  let closedByClient = false;

  const create = () => {
    if (closedByClient) return;

    if (sse) {
      try {
        sse.close();
      } catch {
        console.error("Error closing existing SSE connection");
      }
      sse = null;
    }
    sse = new EventSource(url);

    console.log("sse", sse);

    sse.addEventListener(ChatEvent.USER_MESSAGE, (event) => {
      console.log("User message received:", event);
      const message: MessageResponse = formMessage({
        id: event.lastEventId,
        content: event.data,
        sender: Sender.USER,
      });

      addMessage(message);
    });

    sse.addEventListener(ChatEvent.BOT_MESSAGE, (event) => {
      console.log("Bot message received:", event);

      let parsedData: any = { content: event.data };
      try {
        const json = JSON.parse(event.data);
        if (json && typeof json === "object") {
          parsedData = {
            content: json.final_response || event.data,
            final_response: json.final_response,
            thoughts: json.thoughts,
            step: json.step,
            user_questions: json.user_questions,
          };
        }
      } catch (e) {
        // Not JSON, use as standard text content
      }

      const message: MessageResponse = formMessage({
        id: event.lastEventId,
        content: parsedData.content,
        sender: Sender.BOT,
        ...parsedData,
      });

      addMessage(message);
    });

    sse.addEventListener(ChatEvent.ERROR, (event) => {
      console.error("SSE error:", event);
      sse?.close();
    });

    sse.onerror = (error) => {
      console.error("SSE connection error:", error);
      sse?.close();

      // setTimeout(() => {
      //   console.log("Reconnecting SSE...");
      //   safeClose();

      //   if (!closedByClient && retries < maxRetries) {
      //     retries++;
      //     retryTimer = window.setTimeout(() => {
      //       retryTimer = null;
      //       create();
      //     }, 3000);
      //   } else {
      //     console.warn("SSE no more retries (chatId=", chatId, ")");
      //   }
      // }, 3000); // Retry connection after 3 seconds
    };
  };

  const safeClose = () => {
    if (sse) {
      try {
        sse.close();
      } catch {
        console.error("Error closing SSE connection");
      }
      sse = null;
    }
  };

  const close = () => {
    closedByClient = true;
    if (retryTimer) {
      clearTimeout(retryTimer);
      retryTimer = null;
    }
    safeClose();
  };

  create();

  return { close };
};

const useChat = ({ chatId }: { chatId?: UUID }) => {
  const messages = useAppSelector((state) => state.message.messages);
  const dispatch = useAppDispatch();

  useEffect(() => {
    if (!chatId) return;

    let manager: { close: () => void } | null = null;
    const loadMessages = async () => {
      dispatch(fetchMessagesAction(chatId));

      console.log("Starting loading messages");

      manager = connectSse({
        chatId,
        addMessage: (message: MessageResponse) => dispatch(addMessage(message)),
      });
    };

    loadMessages();

    return () => {
      manager?.close();
    };
  }, [chatId, dispatch]);
  return {
    messages,
  };
};

export default useChat;
