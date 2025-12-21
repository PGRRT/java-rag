import React, { useState } from "react";

import { css } from "@emotion/css";
import { Menu, TextAlignJustify, X } from "lucide-react";
import colorPalette from "@/constants/colorPalette";
import IconWrapper from "@/components/ui/IconWrapper";
import { styles } from "@/constants/styles";
import useViewport from "@/hooks/useViewport";
import { navbarHeight } from "@/layouts/Navbar";
import Logo from "@/components/ui/Logo";
import ContentWrapper from "@/components/ui/ContentWrapper";
import SidebarContent from "@/components/sidebar/SidebarContent";

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

 

  return (
    <div
      id="sidebar"
      className={css`
        width: ${sidebarWidth}px;
        transition: width 0.3s;
        background-color: ${colorPalette.backgroundSecondary};
        border-right: 1px solid ${colorPalette.strokePrimary};
        color: white;
        display: flex;
        flex-direction: column;
        max-height: 100vh;
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

        <ContentWrapper
          customCss={css`
            position: absolute;
            left: 0;
            top: 50%;
            transform: translateY(-50%);
            margin-left: 15px;

            opacity: 0;
            transition: all 0.2s;
            pointer-events: none;
            ${expanded &&
            css`
              display: block;
              opacity: 1;
              pointer-events: auto;
            `}
          `}
        >
          <Logo  height={32} width={"fit-content"} />
        </ContentWrapper>
      </div>

      <SidebarContent expanded={expanded} />
     
    </div>
  );
}
