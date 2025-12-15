import ChatContainer from "@/components/chat/ChatContainer";
import ChatAIInput from "@/components/chat/ChatAIInput";
import ContentWrapper from "@/components/ui/ContentWrapper";
import IconWrapper from "@/components/ui/IconWrapper";
import { styles } from "@/constants/styles";
import { typography } from "@/constants/typography";
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
import { useParams } from "react-router-dom";
import Logo from "@/components/ui/Logo";
import colorPalette from "@/constants/colorPalette";

const Chat = () => {
  const { chatId } = useParams();

  return (
    <ContentWrapper
      justify="center"
      position="relative"
      height="100%"
      padding="0 20px"
    >
      {chatId ? (
        // We are in a specific chat
        <ChatContainer key={chatId} chatId={chatId} />
      ) : (
        <ContentWrapper
          direction="column"
          gap="1rem"
          maxWidth="750px"
          width="100%"
          margin="20vh 0 0 0"
        >
          <ContentWrapper
            width="100%"
            justify="center"
            align="center"
            gap="10px"
          >
            <Logo size={55} />
            <h3 className={cx(typography.textTitle, typography.textTitleTai)}>
              MedAI
            </h3>
          </ContentWrapper>

          <ChatAIInput />

          <ContentWrapper
            direction="row"
            gap="0.5rem"
            width="100%"
            justify="center"
            wrap="wrap"
          >
            <Button variant="outline">Ingredient Checker</Button>
            <Button variant="outline">BMI Calculator</Button>
            <Button variant="outline">Brain Fixer</Button>
          </ContentWrapper>

          <ContentWrapper width="100%" justify="center">
            <p
              className={cx(
                typography.textS,
                css`
                  text-align: center;
                `
              )}
            >
              Remember to always{" "}
              <span className={cx(typography.textBold)}>
                verify the information
              </span>{" "}
              received from AI.
            </p>
          </ContentWrapper>
        </ContentWrapper>
      )}
    </ContentWrapper>
  );
};

export default Chat;
