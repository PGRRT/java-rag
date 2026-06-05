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
import type { UUID } from "@/types/global";
import { useEffect, useRef, useState } from "react";

import SkeletonBlock from "@/components/chat/SkeletonBlock";
import ExpandableThoughts from "@/components/chat/ExpandableThoughts";
import MessageFormatter from "@/components/chat/MessageFormatter";
import DemoReasoningFlow from "@/components/chat/DemoReasoningFlow";

export const AiInputHeight = 90;
const chatPadding = 12;
const additionalSpace = 30;
const ChatContainer = ({ chatId }: { chatId: UUID }) => {
  const { messages } = useChat({ chatId });
  const scrollContainerRef = useRef<HTMLDivElement>(null);
  const messagesEndRef = useRef<HTMLDivElement>(null);
  const [demoRunId, setDemoRunId] = useState(0);

  const isDemoMode = import.meta.env.VITE_DEMO_MODE === "true";
  // Auto-scroll to bottom when new messages arrive
  useEffect(() => {
    if (messagesEndRef.current) {
      messagesEndRef.current.scrollIntoView({ behavior: "smooth" });
    }
  }, [messages]);

  useEffect(() => {
    if (!isDemoMode || messages.length === 0) return;
    const lastMessage = messages[messages.length - 1];
    if (lastMessage.sender === Sender.USER) {
      setDemoRunId((prev) => prev + 1);
    }
  }, [isDemoMode, messages]);

  const isResponding =
    messages.length > 0 && messages[messages.length - 1]?.sender != Sender.BOT;

  return (
    <ContentWrapper
      ref={scrollContainerRef}
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
        `,
      )}
      padding="10px 0 0"
    >
      <ContentWrapper
        width="100%"
        flexValue="1 1 auto"
        direction="column"
        gap="45px"
        maxWidth="750px"
        padding={`0 ${chatPadding}px ${AiInputHeight + 20}px`}
      >
        {messages.map((msg: MessageResponse) => (
          <ContentWrapper
            key={msg.id}
            customCss={css`
              border-radius: ${styles.borderRadius.medium};
              width: fit-content;
              max-width: 100%;
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
            <div
              className={css`
                display: flex;
                flex-direction: column;
                width: 100%;
              `}
            >
              {msg.sender === Sender.BOT && (
                <ExpandableThoughts
                  thoughts={msg.thoughts}
                  messages={msg.messages}
                  step={msg.step}
                  user_questions={msg.user_questions}
                />
              )}
              {msg.sender === Sender.BOT ? (
                <MessageFormatter content={msg.final_response || msg.content} />
              ) : (
                <Markdown className="markdown" remarkPlugins={[remarkGfm]}>
                  {msg.content}
                </Markdown>
              )}
            </div>
          </ContentWrapper>
        ))}

        {isDemoMode
          ? demoRunId > 0 && <DemoReasoningFlow key={demoRunId} />
          : isResponding && (
            <ContentWrapper direction="column" gap="20px">
              <SkeletonBlock width="75%" height="60px" />
              <SkeletonBlock width="55%" />
              <SkeletonBlock width="85%" height="60px" />
            </ContentWrapper>
          )}

        {/* Scroll anchor for auto-scrolling to latest message */}
        <div ref={messagesEndRef} />
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
