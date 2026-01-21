import SidebarChatItem from "@/components/sidebar/SidebarChatItem";
import ContentWrapper from "@/components/ui/ContentWrapper";
import colorPalette from "@/constants/colorPalette";
import { typography } from "@/constants/typography";
import { css, cx } from "@emotion/css";
import { Loader, SegmentedControl } from "@mantine/core";
import { SearchIcon, SquarePen } from "lucide-react";
import { useState, useRef, useCallback, useEffect, useMemo } from "react";
import { useVirtualizer } from "@tanstack/react-virtual";
import {
  SidebarOption,
  type chatMode,
  type SidebarItem,
} from "@/types/sidebarOptions";
import SidebarChats from "@/components/sidebar/SidebarChats";
import { useTranslation } from "react-i18next";

const SidebarContent = ({ expanded }) => {
  const [mode, setMode] = useState<chatMode>("ALL");

  const { t } = useTranslation();

  const generalOptions: SidebarItem[] = useMemo(
    () => [
      {
        id: 1,
        label: t("sidebar.newChat"),
        icon: SquarePen,
        isDisabled: false,
        type: SidebarOption.NewChat,
      },
      {
        id: 2,
        label: t("sidebar.searchChats"),
        icon: SearchIcon,
        isDisabled: true,
        type: SidebarOption.SearchChats,
      },
    ],
    [t],
  );

  const sidebarModeOptions = useMemo(
    () => [
      { label: t("sidebar.all"), value: "ALL" },
      { label: t("sidebar.global"), value: "GLOBAL" },
      { label: t("sidebar.private"), value: "PRIVATE" },
    ],
    [t],
  );

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
            `,
          )}
        >
          {t("sidebar.chats")}
        </span>
        <SegmentedControl
          fullWidth
          withItemsBorders={false}
          value={mode}
          onChange={setMode}
          data={sidebarModeOptions}
        />
      </ContentWrapper>

      <SidebarChats key={mode} mode={mode} />
    </ContentWrapper>
  );
};

export default SidebarContent;
