import { useAppDispatch, useAppSelector } from "@/redux/hooks";
import { fetchMessagesAction } from "@/redux/slices/messageSlice";
import { useEffect, useState } from "react";

const useChat = ({ chatId }: { chatId?: string }) => {
  const messages = useAppSelector((state) => state.message.messages);
  const dispatch = useAppDispatch();

  useEffect(() => {
    if (!chatId) return;

    const loadMessages = async () => {
      dispatch(fetchMessagesAction(chatId));
    };

    loadMessages();
  }, [chatId, dispatch]);
  return {
    messages,
  };
};

export default useChat;
