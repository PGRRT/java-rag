import { useAppDispatch, useAppSelector } from "@/redux/hooks";
import { fetchMessagesAction } from "@/redux/slices/messageSlice";
import axios from "axios";
import { useEffect, useState } from "react";

const useChat = ({ chatId }: { chatId?: string }) => {
  const messages = useAppSelector((state) => state.message.messages);
  const dispatch = useAppDispatch();

  useEffect(() => {
    if (!chatId) return;

    let sse: EventSource | null = null;
    const loadMessages = async () => {
      dispatch(fetchMessagesAction(chatId));

      console.log("Starting loading messages");
      
      sse = new EventSource("http://localhost:8080/api/v1/chats/" + chatId + "/stream");

      console.log("sse", sse);

      sse.addEventListener("CHAT_MESSAGE", (event) => {
        // const parsedData = JSON.parse(event.data);
        console.log("SSE message received:", event);
        // dispatch(fetchMessagesAction(chatId));
      });

      sse.addEventListener("error", (event) => {
        console.error("SSE error:", event);
        sse?.close();
      });
    };

    loadMessages();

    return () => {
      if (sse) {
        sse.close();
      }
    }
  }, [chatId, dispatch]);
  return {
    messages,
  };
};

export default useChat;
