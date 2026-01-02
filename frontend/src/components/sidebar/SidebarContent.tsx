import SidebarChatItem from "@/components/sidebar/SidebarChatItem";
import ContentWrapper from "@/components/ui/ContentWrapper";
import colorPalette from "@/constants/colorPalette";
import { typography } from "@/constants/typography";
import { css, cx } from "@emotion/css";
import { Loader, SegmentedControl } from "@mantine/core";
import { SearchIcon, SquarePen } from "lucide-react";
import { useState, useRef, useCallback, useEffect } from "react";
import { useVirtualizer } from "@tanstack/react-virtual";
import { useInfiniteChats } from "@/hooks/useInfiniteChats";
import {
  SidebarOption,
  type chatMode,
  type SidebarItem,
} from "@/types/sidebarOptions";
import SidebarChats from "@/components/sidebar/SidebarChats";

const generalOptions: SidebarItem[] = [
  {
    id: 1,
    label: "New chat",
    icon: SquarePen,
    isDisabled: false,
    type: SidebarOption.NewChat,
  },
  {
    id: 2,
    label: "Search chats",
    icon: SearchIcon,
    isDisabled: true,
    type: SidebarOption.SearchChats,
  },
];

const sidebarModeOptions = [
  {
    label: "All",
    value: "ALL",
  },
  {
    label: "Global",
    value: "GLOBAL",
  },
  {
    label: "Private",
    value: "PRIVATE",
  },
];
const SidebarContent = ({ expanded }) => {
  const [mode, setMode] = useState<chatMode>("ALL");

  if (!expanded) {
    return null;
  }
  return (
    <ContentWrapper
      gap="10px"
      direction="column"
      customCss={css`
        padding: 5px;
        height: 100%;
        overflow: hidden;
      `}
    >
      <ContentWrapper>
        {generalOptions.map((item) => (
          <SidebarChatItem
            key={item.id}
            label={item.label}
            icon={item.icon}
            isDisabled={item.isDisabled}
            type={item.type}
            id={item.id}
          />
        ))}
      </ContentWrapper>

      <ContentWrapper padding="8px 10px" gap="10px" direction="column">
        <span
          className={cx(
            typography.textM,
            css`
              display: block;
              color: ${colorPalette.textMuted};
            `
          )}
        >
          Chats
        </span>
        <SegmentedControl
          fullWidth
          withItemsBorders={false}
          value={mode}
          onChange={setMode}
          data={sidebarModeOptions}
        />
      </ContentWrapper>

      <SidebarChats mode={mode} />
    </ContentWrapper>
  );
};

export default SidebarContent;
