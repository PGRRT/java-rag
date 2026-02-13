import { useEffect, useCallback } from "react";
import { ChatEvent } from "@/api/enums/ChatEvent";
import type { MessageResponse } from "@/types/message";
import { Sender, type SenderType } from "@/api/enums/Sender";
import type { UUID } from "@/types/global";
import { useInfiniteMessages } from "./useInfiniteMessages";

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

const connectSse = ({
  chatId,
  onNewMessage,
}: {
  chatId: string;
  onNewMessage: (message: MessageResponse) => void;
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

      onNewMessage(message);
    });

    sse.addEventListener(ChatEvent.BOT_MESSAGE, (event) => {
      console.log("Bot message received:", event);
      const message: MessageResponse = formMessage({
        id: event.lastEventId,
        content: event.data,
        sender: Sender.BOT,
      });

      onNewMessage(message);
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
  const {
    messages,
    isLoading,
    isLoadingMore,
    isReachingEnd,
    loadMoreMessages,
    error,
    isErrorInitial,
    refresh,
  } = useInfiniteMessages({ chatId });

  const handleNewMessage = useCallback(
    (newMessage: MessageResponse) => {
      if (!chatId) return;

      console.log("New message received:", newMessage);
      refresh();
    },
    [chatId, refresh],
  );

  useEffect(() => {
    if (!chatId) return;

    let manager: { close: () => void } | null = null;
    const setupRealTimeChat = () => {
      console.log("Starting real-time chat connection");

      manager = connectSse({
        chatId,
        onNewMessage: handleNewMessage,
      });
    };

    setupRealTimeChat();

    return () => {
      manager?.close();
    };
  }, [chatId, handleNewMessage]);

  return {
    messages,
    isLoading,
    isLoadingMore,
    isReachingEnd,
    loadMoreMessages,
    error,
    isErrorInitial,
  };
};

export default useChat;
