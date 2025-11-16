import ContentWrapper from "@/components/ui/ContentWrapper";
import IconWrapper from "@/components/ui/IconWrapper";
import { styles } from "@/constants/styles";
import { typography } from "@/constants/typography";
import useChat from "@/hooks/useChat";
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

const InputAIForm = () => {
  const { file, setFile, message, setMessage, sendMessage } = useChat();

  return (
    <form onSubmit={sendMessage}>
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
          <div
            className={css`
              background: white;
              border-radius: 50%;
              padding: 6px;
              margin-right: 20px;
              display: flex;
              align-items: center;
              justify-content: center;
            `}
            onClick={sendMessage}
          >
            <IconWrapper Icon={ChevronUp} color="black" />
          </div>
        }
        placeholder="What's up, Doc?"
        style={{ flex: 1 }}
      />
    </form>
  );
};

export default InputAIForm;
