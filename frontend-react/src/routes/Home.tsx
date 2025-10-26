/** @jsxImportSource @emotion/react */
import React, { useState } from "react";
import {
  Tooltip,
  UnstyledButton,
  Text,
  ActionIcon,
  Button,
} from "@mantine/core";
import { css } from "@emotion/react";
import { Menu, TextAlignJustify, X } from "lucide-react";
import colorPalette from "@/constants/colorPalette";
import IconWrapper from "@/components/ui/IconWrapper";

export default function CollapsibleSidebar() {
  const [expanded, setExpanded] = useState(false);
  const sidebarWidth = expanded ? 240 : 70;

  const menuItems = [
    // to be added
  ];

  return (
    <div
      css={css`
        width: ${sidebarWidth}px;
        transition: width 0.3s;
        background-color: ${colorPalette.backgroundSecondary};
        border-right: 1px solid ${colorPalette.primary};
        color: white;
        min-height: 100vh;
        display: flex;
        flex-direction: column;
        padding: 15px;
      `}
    >
      <div
        css={css`
          width: 100%;
          position: relative;
        `}
      >
        <div
          css={css`
            display: flex;
            align-items: center;
            justify-content: flex-start;
            transition: all 0.1s;

            position: absolute;
            left: 50%;
            transform: translateX(-50%);
            top: 0;
            opacity: 1;
            ${expanded &&
            css`
              opacity: 0;
            `}
          `}
        >
          <IconWrapper
            onClick={() => setExpanded(!expanded)}
            size={26}
            Icon={TextAlignJustify}
            color={colorPalette.accent}
            hoverColor={colorPalette.textActive}
          />
        </div>

        <div
          css={css`
            display: flex;
            align-items: center;
            justify-content: flex-end;

            transition: all 0.1s;
            position: absolute;
            right: 0;
            top: 0;
            opacity: 0;
            ${expanded &&
            css`
              opacity: 1;
            `}
          `}
        >
          <IconWrapper
            onClick={() => setExpanded(!expanded)}
            size={26}
            Icon={X}
            color={colorPalette.accent}
            hoverColor={colorPalette.textActive}
          />
        </div>
      </div>

      {/* Menu items */}
      {menuItems.map((item) => (
        <Tooltip
          key={item.label}
          label={item.label}
          position="right"
          disabled={expanded}
          withArrow
        >
          <UnstyledButton
            onClick={() => console.log(item.label)}
            css={css`
              width: 100%;
              display: flex;
              align-items: center;
              gap: 12px;
              padding: 8px;
              border-radius: 6px;
              cursor: pointer;
              &:hover {
                background-color: rgba(255, 255, 255, 0.1);
              }
            `}
          >
            {item.icon}
            {expanded && <Text>{item.label}</Text>}
          </UnstyledButton>
        </Tooltip>
      ))}
    </div>
  );
}
