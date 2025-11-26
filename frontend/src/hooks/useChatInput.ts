import { userApi } from "@/api/userApi";
import useUser from "@/hooks/useUser";
import exceptionWrapper from "@/utils/exceptionWrapper";
import { showToast } from "@/utils/showToast";
import { useState } from "react";
import type { SenderType } from "@/api/enums/Sender";
import { useNavigate } from "react-router-dom";
import { useDispatch } from "react-redux";
import { createChatAction } from "@/redux/slices/chatSlice";
import { useAppDispatch } from "@/redux/hooks";
import {
  fetchMessagesAction,
  postMessagesAction,
} from "@/redux/slices/messageSlice";

const useChatInput = ({ chatId = "" }: { chatId?: string }) => {
  const [file, setFile] = useState<File | null>(null);
  const [message, setMessage] = useState<string>("");
  const user = useUser();
  const navigate = useNavigate();
  const dispatch = useAppDispatch();
  const sendMessage = async (e: React.FormEvent<HTMLFormElement>) => {
    e.preventDefault();

    let tempChatId: string = chatId;

    if (!message.trim()) {
      return;
    }

    if (!chatId) {
      // Create new chat if chatId is not provided
      const res = await dispatch(
        createChatAction({
          message,
          chatType: user.isLoggedIn ? "PRIVATE" : "GLOBAL",
        })
      );

      if (createChatAction.rejected.match(res)) {
        // toast is shown in the thunk
        console.error("Error creating chat:", res.payload);
        cleanUp();
        return;
      }

      const chatId = res.payload.id;
      tempChatId = chatId;
      setTimeout(() => {
        navigate(`/chat/${chatId}`, {
          replace: false,
        });
      }, 1000);
    }

    const messageResponse = await dispatch(
      postMessagesAction({ chatId: tempChatId, content: message })
    );

    if (postMessagesAction.rejected.match(messageResponse)) {
      // toast is shown in the thunk
      console.error("Error posting message:", messageResponse.payload);
      cleanUp();
      return;
    }

    cleanUp();
  };

  const cleanUp = () => {
    setMessage("");
    setFile(null);
  };

  return {
    file,
    setFile,
    message,
    setMessage,
    sendMessage,
  };
};

export default useChatInput;
