import ChatContainer from "@/components/chat/ChatContainer";
import ChatAIInput from "@/components/chat/ChatAIInput";
import ContentWrapper from "@/components/ui/ContentWrapper";
import { typography } from "@/constants/typography";
import { css, cx } from "@emotion/css";
import { Button } from "@mantine/core";
import { useParams } from "react-router-dom";
import Logo from "@/components/ui/Logo";
import type { UUID } from "@/types/global";
import { useTranslation, Trans } from "react-i18next";

const Chat = () => {
  const {
    chatId,
  }: {
    chatId?: UUID;
  } = useParams();
  const { t } = useTranslation();

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
            <Logo height={60} />
            <h3 className={cx(typography.textTitle, typography.textTitleTai)}>
              MedAI
            </h3>
          </ContentWrapper>

          <ChatAIInput isNewChat />

          <ContentWrapper
            direction="row"
            gap="0.5rem"
            width="100%"
            justify="center"
            wrap="wrap"
          >
            <Button variant="outline">
              {t("chat.quickActions.ingredientChecker")}
            </Button>
            <Button variant="outline">
              {t("chat.quickActions.bmiCalculator")}
            </Button>
            <Button variant="outline">
              {t("chat.quickActions.sleepFixer")}
            </Button>
          </ContentWrapper>

          <ContentWrapper width="100%" justify="center">
            <p
              className={cx(
                typography.textS,
                css`
                  text-align: center;
                `,
              )}
            >
              <Trans
                i18nKey="chat.verifyWarning"
                components={[
                  <span key="0" className={cx(typography.textBold)}></span>,
                ]}
              />
            </p>
          </ContentWrapper>
        </ContentWrapper>
      )}
    </ContentWrapper>
  );
};

export default Chat;
