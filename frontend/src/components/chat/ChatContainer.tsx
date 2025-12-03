import { Sender } from "@/api/enums/Sender";
import type { MessageResponse } from "@/types/message";
import ChatAIInput from "@/components/chat/ChatAIInput";
import ContentWrapper from "@/components/ui/ContentWrapper";
import colorPalette from "@/constants/colorPalette";
import { styles } from "@/constants/styles";
import useChat from "@/hooks/useChat";
import { navbarHeight } from "@/layouts/Navbar";
import { css, cx } from "@emotion/css";
import Markdown from "react-markdown";
import remarkGfm from "remark-gfm";

export const AiInputHeight = 90;
const chatPadding = 12;
const additionalSpace = 30;
const ChatContainer = ({ chatId }: { chatId: string }) => {
  const { messages } = useChat({ chatId });
  console.log("messages", messages);

  return (
    <ContentWrapper
      justify="center"
      position="relative"
      height="100%"
      width="100%"
      customCss={cx(
        "nice-scroll",
        css`
          max-height: calc(
            100vh - ${navbarHeight}px - ${AiInputHeight}px -
              ${additionalSpace}px
          );
          overflow-y: auto;
        `
      )}
      padding="10px 0 0"
    >
      <ContentWrapper
        width="100%"
        flexValue="1 1 auto"
        direction="column"
        gap="3rem"
        maxWidth="750px"
        padding={`0 ${chatPadding}px`}
      >
        {messages.map((msg: MessageResponse) => (
          <ContentWrapper
            key={msg.id}
            customCss={css`
              border-radius: ${styles.borderRadius.medium};
              width: fit-content;
              align-self: ${msg.sender === Sender.USER
                ? "flex-end"
                : "flex-start"};

              // Bot will have default markdown styles
              ${msg.sender === Sender.BOT &&
              css`
                .markdown,
                .markdown * {
                  all: revert;
                }
              `}

              ${msg.sender === Sender.USER &&
              css`
                padding: ${styles.padding.small} ${styles.padding.medium};
                background-color: ${colorPalette.backgroundBright};
              `}
            `}
          >
            <Markdown className="markdown" remarkPlugins={[remarkGfm]}>
              {msg.content}
            </Markdown>
          </ContentWrapper>
        ))}
      </ContentWrapper>
      <ContentWrapper
        maxWidth="750px"
        customCss={css`
          position: fixed;
          bottom: 0;
          padding: 5px ${chatPadding}px 0;
          width: 100%;
          background-color: ${colorPalette.background};
          height: ${AiInputHeight}px;
        `}
      >
        <ChatAIInput chatId={chatId} />
      </ContentWrapper>
    </ContentWrapper>
  );
};

export default ChatContainer;
