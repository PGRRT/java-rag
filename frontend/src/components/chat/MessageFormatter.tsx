import React from "react";
import Markdown from "react-markdown";
import remarkGfm from "remark-gfm";
import { Paper, Text, Stack, Box, ThemeIcon } from "@mantine/core";
import { css } from "@emotion/css";
import { Info, ShieldAlert } from "lucide-react";
import colorPalette from "@/constants/colorPalette";

interface MessageFormatterProps {
  content: string;
}

const MessageFormatter: React.FC<MessageFormatterProps> = ({ content }) => {
  // Regex to match <fakty_z_bazy>...</fakty_z_bazy> and <zastrzezenie>...</zastrzezenie>
  const faktyRegex = /<fakty_z_bazy>([\s\S]*?)<\/fakty_z_bazy>/g;
  const zastrzezenieRegex = /<zastrzezenie>([\s\S]*?)<\/zastrzezenie>/g;

  // Extract content
  const faktyMatch = [...content.matchAll(faktyRegex)];
  const zastrzezenieMatch = [...content.matchAll(zastrzezenieRegex)];

  // Remove the tags from the content to get the "clean" text if there's any other text
  let cleanContent = content
    .replace(faktyRegex, "")
    .replace(zastrzezenieRegex, "")
    .replace(/<odpowiedz>|<\/odpowiedz>/g, "")
    .trim();

  return (
    <Stack gap="md">
      {cleanContent && (
        <div className="markdown-container">
          <Markdown remarkPlugins={[remarkGfm]}>{cleanContent}</Markdown>
        </div>
      )}

      {faktyMatch.map((match, index) => (
        <Paper
          key={`fakty-${index}`}
          withBorder
          p="md"
          radius="md"
          className={css`
            background-color: rgba(64, 192, 87, 0.05);
            border-color: ${colorPalette.primary}44;
          `}
        >
          <Stack gap="xs">
            <Box style={{ display: "flex", alignItems: "center", gap: "8px" }}>
              <ThemeIcon variant="light" color="green" size="sm">
                <Info size={14} />
              </ThemeIcon>
              <Text fw={700} size="sm" c="green.9">
                Fakty z bazy wiedzy
              </Text>
            </Box>
            <div
              className={css`
                font-size: 14px;
                color: ${colorPalette.text};
              `}
            >
              <Markdown remarkPlugins={[remarkGfm]}>{match[1].trim()}</Markdown>
            </div>
          </Stack>
        </Paper>
      ))}

      {zastrzezenieMatch.map((match, index) => (
        <Paper
          key={`zastrzezenie-${index}`}
          withBorder
          p="sm"
          radius="md"
          className={css`
            background-color: rgba(250, 176, 5, 0.05);
            border-color: #fab00544;
            border-style: dashed;
          `}
        >
          <Stack gap="xs">
            <Box style={{ display: "flex", alignItems: "center", gap: "8px" }}>
              <ShieldAlert size={16} color="#e67700" />
              <Text fw={700} size="xs" c="orange.9" style={{ textTransform: "uppercase", letterSpacing: "0.5px" }}>
                Ważne zastrzeżenie
              </Text>
            </Box>
            <Text size="sm" c="dimmed" fs="italic">
              {match[1].trim()}
            </Text>
          </Stack>
        </Paper>
      ))}
    </Stack>
  );
};

export default MessageFormatter;
