import ContentWrapper from "@/components/ui/ContentWrapper";
import IconWrapper from "@/components/ui/IconWrapper";
import useChatInput from "@/hooks/useChatInput";
import { css, cx } from "@emotion/css";
import { Button, FileButton, Textarea, Tooltip } from "@mantine/core";
import { ChevronUp, Paperclip, Lock, Globe } from "lucide-react";
import { useState } from "react";
import type { UUID } from "@/types/global";
import type { ChatRoomType } from "@/api/enums/ChatRoom";
import { typography } from "@/constants/typography";
import { useUserSWR } from "@/hooks/useUser";

const ChatAIInput = ({
  chatId,
  isNewChat,
}: {
  chatId?: UUID;
  isNewChat?: boolean;
}) => {
  const [mode, setMode] = useState<ChatRoomType>("GLOBAL");
  const { user } = useUserSWR();

  const toggleMode = () => {
    if (!user && mode === "GLOBAL") {
      return;
    }
    setMode((prev) => (prev === "GLOBAL" ? "PRIVATE" : "GLOBAL"));
  };
  const { setFile, message, setMessage, sendMessage } = useChatInput({
    chatId,
    mode,
    isNewChat,
  });

  const handleKeyDown = (event: React.KeyboardEvent<HTMLTextAreaElement>) => {
    if (event.key === "Enter" && !event.shiftKey) {
      event.preventDefault();
      sendMessage(event as any);
    }
  };

  return (
    <form
      onSubmit={sendMessage}
      className={css`
        width: 100%;
      `}
    >
      <Textarea
        value={message}
        onChange={(e) => setMessage(e.currentTarget.value)}
        onKeyDown={handleKeyDown}
        autosize
        minRows={1}
        maxRows={5}
        className={cx(
          css`
            .mantine-Textarea-section[data-position="left"] {
              width: 50px;
              align-items: flex-end;
              padding-bottom: 8px;
            }

            .mantine-Textarea-section[data-position="right"] {
              align-items: flex-end;
              justify-content: flex-end;
              margin-right: 14px;
              padding-bottom: 8px;
            }

            textarea {
              border-radius: 24px;
              padding: 15px 60px 15px 50px;
            }
          `
        )}
        leftSection={
          <ContentWrapper id="asdz">
            <FileButton onChange={setFile} accept="*">
              {(props) => <IconWrapper {...props} Icon={Paperclip} />}
            </FileButton>
          </ContentWrapper>
        }
        rightSection={
          <ContentWrapper gap="10px" direction="row">
            {isNewChat && (
              <Tooltip
                label={
                  !user && mode === "GLOBAL"
                    ? "Log in to create private chats"
                    : mode === "GLOBAL"
                    ? "Global chat - visible to everyone"
                    : "Private chat - only you can see it"
                }
                position="top"
              >
                <Button
                  onClick={toggleMode}
                  disabled={!user && mode === "GLOBAL"}
                  className={css`
                    padding: 0 !important;
                    background: white;
                    border-radius: 50%;
                    display: flex;
                    align-items: center;
                    justify-content: center;
                    width: 34px !important;
                    height: 34px !important;
                    ${!user && mode === "GLOBAL"
                      ? "opacity: 0.5; cursor: not-allowed;"
                      : ""}
                  `}
                >
                  <IconWrapper
                    Icon={mode === "GLOBAL" ? Globe : Lock}
                    color="black"
                  />
                </Button>
              </Tooltip>
            )}

            <Button
              type="submit"
              className={css`
                padding: 0 !important;
                background: white;
                border-radius: 50%;
                display: flex;
                align-items: center;
                justify-content: center;
                width: 34px !important;
                height: 34px !important;
              `}
            >
              <IconWrapper Icon={ChevronUp} color="black" />
            </Button>
          </ContentWrapper>
        }
        placeholder="What's up, Doc?"
        style={{ flex: 1 }}
      />
    </form>
  );
};
export default ChatAIInput;
