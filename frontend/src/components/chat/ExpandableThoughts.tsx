import { useState } from "react";
import { Collapse, UnstyledButton, Text } from "@mantine/core";
import { css } from "@emotion/css";
import colorPalette from "@/constants/colorPalette";
import { styles } from "@/constants/styles";
import Markdown from "react-markdown";
import remarkGfm from "remark-gfm";

interface ExpandableThoughtsProps {
  thoughts?: string;
  messages?: any[];
  step?: string;
  user_questions?: string | string[];
}

const ExpandableThoughts = ({
  thoughts,
  messages,
  step,
  user_questions,
}: ExpandableThoughtsProps) => {
  const [opened, setOpened] = useState(false);

  if (!thoughts && !messages && !step && !user_questions) return null;

  const questions = Array.isArray(user_questions)
    ? user_questions
    : user_questions
    ? [user_questions]
    : [];

  const renderMessage = (msg: any, index: number) => {
    const isTool = msg.type === "tool";
    const isAi = msg.type === "ai";
    const hasToolCalls = msg.tool_calls && msg.tool_calls.length > 0;

    return (
      <div key={index} style={{ marginBottom: "10px" }}>
        <Text fw={700} size="xs" c="dimmed" style={{ textTransform: "uppercase" }}>
          {isTool ? `Tool Output (${msg.tool_name})` : isAi ? "AI Thought" : msg.type}
        </Text>
        {msg.content && (
          <Markdown className="markdown" remarkPlugins={[remarkGfm]}>
            {msg.content}
          </Markdown>
        )}
        {hasToolCalls && (
          <div style={{ marginTop: "4px", paddingLeft: "10px", borderLeft: `2px solid ${colorPalette.primary}` }}>
            <Text size="xs" fw={600} c="dimmed">Tool Calls:</Text>
            {msg.tool_calls.map((tc: any, i: number) => (
              <div key={i}>
                <Text size="sm" fw={500}>{tc.name}</Text>
                <Text size="xs" style={{ fontStyle: "italic" }}>{JSON.stringify(tc.args)}</Text>
              </div>
            ))}
          </div>
        )}
      </div>
    );
  };

  return (
    <div
      className={css`
        margin-bottom: 10px;
        width: 100%;
      `}
    >
      <UnstyledButton
        onClick={() => setOpened((o) => !o)}
        className={css`
          display: flex;
          align-items: center;
          gap: 8px;
          color: ${colorPalette.textMuted};
          font-size: 14px;
          padding: 4px 0;
          transition: color 0.2s;
          &:hover {
            color: ${colorPalette.primary};
          }
        `}
      >
        <div
          className={css`
            width: 16px;
            height: 16px;
            display: flex;
            align-items: center;
            justify-content: center;
            transform: ${opened ? "rotate(90deg)" : "rotate(0deg)"};
            transition: transform 0.2s;
          `}
        >
          <svg
            width="12"
            height="12"
            viewBox="0 0 24 24"
            fill="none"
            stroke="currentColor"
            strokeWidth="2"
            strokeLinecap="round"
            strokeLinejoin="round"
          >
            <polyline points="9 18 15 12 9 6"></polyline>
          </svg>
        </div>
        <Text size="sm" fw={500}>
          {opened ? "Ukryj przebieg rozumowania" : "Pokaż przebieg rozumowania"}
        </Text>
      </UnstyledButton>

      <Collapse in={opened}>
        <div
          className={css`
            margin-top: 8px;
            padding: 12px;
            background-color: ${colorPalette.backgroundSecondary};
            border-left: 3px solid ${colorPalette.primary};
            border-radius: ${styles.borderRadius.small};
            font-size: 13px;
            color: ${colorPalette.text};

            .markdown,
            .markdown * {
              all: revert;
              font-size: 13px;
            }
          `}
        >
          {step && (
            <div style={{ marginBottom: "8px" }}>
              <Text
                fw={700}
                size="xs"
                c="dimmed"
                style={{ textTransform: "uppercase" }}
              >
                Current Step
              </Text>
              <Text size="sm">{step}</Text>
            </div>
          )}

          {messages && messages.length > 0 ? (
            <div style={{ marginBottom: questions.length > 0 ? "8px" : "0" }}>
              {messages.map((msg, i) => renderMessage(msg, i))}
            </div>
          ) : thoughts ? (
            <div style={{ marginBottom: questions.length > 0 ? "8px" : "0" }}>
              <Text
                fw={700}
                size="xs"
                c="dimmed"
                style={{ textTransform: "uppercase" }}
              >
                Thoughts
              </Text>
              <Markdown className="markdown" remarkPlugins={[remarkGfm]}>
                {thoughts}
              </Markdown>
            </div>
          ) : null}

          {questions.length > 0 && (
            <div>
              <Text
                fw={700}
                size="xs"
                c="dimmed"
                style={{ textTransform: "uppercase" }}
              >
                Clarifying Questions
              </Text>
              <ul style={{ margin: "4px 0 0 20px", padding: 0 }}>
                {questions.map((q, i) => (
                  <li key={i}>
                    <Text size="sm">{q}</Text>
                  </li>
                ))}
              </ul>
            </div>
          )}
        </div>
      </Collapse>
    </div>
  );
};

export default ExpandableThoughts;
