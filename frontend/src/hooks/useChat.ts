import { userApi } from "@/api/userApi";
import useUser from "@/hooks/useUser";
import exceptionWrapper from "@/utils/exceptionWrapper";
import { showToast } from "@/utils/showToast";
import { useState } from "react";
import type { SenderType } from "@/api/enums/SenderType";
import { useNavigate } from "react-router-dom";

const UseChat = () => {
  const [file, setFile] = useState<File | null>(null);
  const [message, setMessage] = useState("");
  const user = useUser();
  const navigate = useNavigate();
  const sendMessage = async (e: React.FormEvent<HTMLFormElement>) => {
    e.preventDefault();

    if (!message.trim()) {
      return;
    }

    const res = await exceptionWrapper(async () => {
      return userApi.createChat(
        message,
        user.isLoggedIn ? "PRIVATE" : "GLOBAL"
      );
    }, "Chat created successfully");

    console.log("created_chat: ", res);
    setTimeout(() => {
      navigate(`/chat/${res?.id}`);
    }, 6000);

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

export default UseChat;
