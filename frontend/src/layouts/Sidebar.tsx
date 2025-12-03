import React, { useState } from "react";
import {
  Tooltip,
  UnstyledButton,
  Text,
  ActionIcon,
  Button,
} from "@mantine/core";
import { css } from "@emotion/css";
import { Menu, TextAlignJustify, X } from "lucide-react";
import colorPalette from "@/constants/colorPalette";
import IconWrapper from "@/components/ui/IconWrapper";
import { styles } from "@/constants/styles";
import useViewport from "@/hooks/useViewport";
import { navbarHeight } from "@/layouts/Navbar";

const buttonsStyle = css`
  display: flex;
  align-items: center;
  justify-content: flex-start;
  transition: all 0.2s;

  position: absolute;

  top: 50%;
  padding: 6px;

  border-radius: ${styles.borderRadius.small};

  &:hover {
    background: ${colorPalette.backgroundTertiary} !important;
  }
`;

export const notActiveSidebarWidth = 70;
export const activeSidebarWidth = 240;

export default function Sidebar() {
  const [expanded, setExpanded] = useState(false);
  const { isMobile } = useViewport();
  const sidebarWidth = expanded ? activeSidebarWidth : notActiveSidebarWidth;

  const menuItems = [
    // to be added
  ];

  return (
    <div
      id="sidebar"
      className={css`
        width: ${sidebarWidth}px;
        transition: width 0.3s;
        background-color: ${colorPalette.backgroundSecondary};
        border-right: 1px solid ${colorPalette.strokePrimary};
        color: white;
        // min-height: 100vh;
        display: flex;
        flex-direction: column;
        // padding: 1rem;

        position: sticky;
        top: 0;

        ${isMobile &&
        css`
          position: fixed;
          left: 0;
          top: 0;
          bottom: 0;
          z-index: 1000;

          background-color: transparent;
          border-right: none;

          ${expanded &&
          css`
            background-color: ${colorPalette.backgroundSecondary};
            border-right: 1px solid ${colorPalette.strokePrimary};
          `}
        `}
      `}
    >
      <div
        className={css`
          position: relative;
          width: 100%;
          height: ${navbarHeight}px;
          display: flex;
          align-items: center;
          justify-content: center;
          background-color: ${colorPalette.backgroundSecondary};

          ${isMobile &&
          !expanded &&
          css`
            background-color: ${colorPalette.background};

            // border-bottom: 1px solid ${colorPalette.strokePrimary};
          `}
        `}
      >
        <div
          onClick={() => setExpanded(!expanded)}
          aria-hidden={expanded}
          className={css`
            ${buttonsStyle}

            left: 50%;
            transform: translate(-50%, -50%);

            opacity: 1;
            pointer-events: auto;
            cursor: pointer;
            ${expanded &&
            css`
              opacity: 0;
              pointer-events: none;
            `}
          `}
        >
          <IconWrapper
            size={24}
            Icon={TextAlignJustify}
            color={colorPalette.accent}
            // hoverColor={colorPalette.textActive}
          />
        </div>

        <div
          onClick={() => setExpanded(!expanded)}
          aria-hidden={!expanded}
          className={css`
            ${buttonsStyle}

            right: 0;
            transform: translateY(-50%);
            opacity: 0;
            margin-right: 15px;

            pointer-events: none;
            cursor: pointer;
            ${expanded &&
            css`
              opacity: 1;
              pointer-events: auto;
            `}
          `}
        >
          <IconWrapper size={24} Icon={X} color={colorPalette.accent} />
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
            className={css`
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
