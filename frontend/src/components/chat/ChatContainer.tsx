import type { MessageResponse } from "@/api/schemas/message";
import ChatAIInput from "@/components/chat/ChatAIInput";
import ContentWrapper from "@/components/ui/ContentWrapper";
import useChat from "@/hooks/useChat";
import { css } from "@emotion/css";

const ChatContainer = ({ chatId }: { chatId: string }) => {
  const { messages } = useChat({ chatId });
  console.log("messages", messages);

  return (
    <>
      <ContentWrapper
        width="100%"
        flexValue="1 1 auto"
        direction="column"
        gap="1rem"
      >
        {messages.map((msg: MessageResponse) => (
          <ContentWrapper
            key={msg.id}
            width="100%"
            customCss={css`
              border-bottom: 1px solid #e0e0e0;
              overflow-y: auto;
              padding-bottom: 1rem;
            `}
          >
            Sender: {msg.sender}
            <br />
            Message: {msg.content}
          </ContentWrapper>
        ))}
      </ContentWrapper>
      <ContentWrapper width="100%">
        <ChatAIInput chatId={chatId} />
      </ContentWrapper>
    </>
  );
};

export default ChatContainer;
