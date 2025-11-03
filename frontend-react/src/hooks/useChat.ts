import { userApi } from "@/api/userApi";
import exceptionWrapper from "@/utils/exceptionWrapper";
import { showToast } from "@/utils/showToast";
import { useState } from "react";

const UseChat = () => {
  const [file, setFile] = useState<File | null>(null);
  const [message, setMessage] = useState("");

  const sendMessage = async () => {
    if (!message.trim()) {
      return;
    }

    await exceptionWrapper(() => {
      return userApi.postChatMessage(message);
    });

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
