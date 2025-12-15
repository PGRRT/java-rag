import ContentWrapper from "@/components/ui/ContentWrapper";
import IconWrapper from "@/components/ui/IconWrapper";
import { styles } from "@/constants/styles";
import { typography } from "@/constants/typography";
import useChatInput from "@/hooks/useChatInput";
import { css, cx } from "@emotion/css";
import {
  Button,
  FileButton,
  FileInput,
  Group,
  Pill,
  TextInput,
} from "@mantine/core";
import { ChevronUp, MoveUp, Paperclip } from "lucide-react";
import { useState } from "react";

const ChatAIInput = ({ chatId }: { chatId?: string }) => {
  const { file, setFile, message, setMessage, sendMessage } = useChatInput({
    chatId,
  });

  return (
    <form
      onSubmit={sendMessage}
      className={css`
        width: 100%;
      `}
    >
      <TextInput
        value={message}
        onChange={(e) => setMessage(e.currentTarget.value)}
        className={css`
          .mantine-TextInput-section[data-position="left"] {
            width: 50px;
          }

          // .mantine-TextInput-section[data-position="right"] {
          //   width: inherit;
          // }

          input {
            border-radius: 30px;
            height: initial;
            padding: 10px 60px 10px 50px;
          }
        `}
        leftSection={
          <FileButton onChange={setFile} accept="*">
            {(props) => <IconWrapper {...props} Icon={Paperclip} />}
          </FileButton>
        }
        rightSection={
          <button
            type="submit"
            className={css`
              background: white;
              border-radius: 50%;
              padding: 6px;
              margin-right: 20px;
              display: flex;
              align-items: center;
              justify-content: center;
              cursor: pointer;
            `}
          >
            <IconWrapper Icon={ChevronUp} color="black" />
          </button>
        }
        placeholder="What's up, Doc?"
        style={{ flex: 1 }}
      />
    </form>
  );
};

export default ChatAIInput;
