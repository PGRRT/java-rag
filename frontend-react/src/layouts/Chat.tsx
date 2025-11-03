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

const Chat = () => {
  const { file, setFile, message, setMessage, sendMessage } = useChat();
  return (
    <ContentWrapper justify="center" height="100%">
      <ContentWrapper
        direction="column"
        gap="1rem"
        maxWidth="750px"
        width="100%"
        margin="20vh 0 0 0"
      >
        <ContentWrapper width="100%" justify="center">
          <h3 className={cx(typography.textTitle, typography.textTitleTai)}>
            MedAI
          </h3>
        </ContentWrapper>

        <ContentWrapper>
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
        </ContentWrapper>

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
    </ContentWrapper>
  );
};

export default Chat;
