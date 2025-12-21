import SidebarChatItem from "@/components/sidebar/SidebarChatItem";
import ContentWrapper from "@/components/ui/ContentWrapper";
import IconWrapper from "@/components/ui/IconWrapper";
import colorPalette from "@/constants/colorPalette";
import { styles } from "@/constants/styles";
import { typography } from "@/constants/typography";
import { css, cx } from "@emotion/css";
import { Loader } from "@mantine/core";
import { SearchIcon, SquarePen } from "lucide-react";
import { useState, useRef, useCallback } from "react";
import { useVirtualizer } from "@tanstack/react-virtual";
import { useInfiniteChats } from "@/hooks/useInfiniteChats";
import { SidebarOption, type SidebarItem } from "@/types/sidebarOptions";

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

const SidebarContent = ({ expanded }) => {
  const { chats, isLoading, isLoadingMore, loadMore, isReachingEnd } =
    useInfiniteChats();

  const parentRef = useRef<HTMLDivElement>(null);

  const rowVirtualizer = useVirtualizer({
    count: chats.length,
    getScrollElement: () => parentRef.current,
    estimateSize: () => 42,
    overscan: 5,
  });

  // Detect when user scrolls near bottom to trigger loadMore
  const lastItemRef = useCallback(
    (node: HTMLDivElement | null) => {
      if (!node) return;

      const observer = new IntersectionObserver((entries) => {
        if (entries[0].isIntersecting && !isLoadingMore && !isReachingEnd) {
          console.log("Loading more chats...");
          loadMore();
        }
      });

      observer.observe(node);
      return () => observer.disconnect();
    },
    [loadMore, isLoadingMore, isReachingEnd]
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

      <ContentWrapper padding="8px 10px">
        <span
          className={cx(
            typography.textM,
            css`
              display: block;
              color: ${colorPalette.textMuted};
            `
          )}
        >
          Your chats
        </span>
      </ContentWrapper>

      <div
        ref={parentRef}
        className={css`
          flex: 1;
          overflow-y: auto;
          overflow-x: hidden;
        `}
      >
        <div
          style={{
            height: `${rowVirtualizer.getTotalSize()}px`,
            width: "100%",
            position: "relative",
          }}
        >
          {rowVirtualizer.getVirtualItems().map((virtualItem) => {
            const chat = chats[virtualItem.index];
            const isLast = virtualItem.index === chats.length - 1;

            return (
              <div
                key={virtualItem.key}
                ref={isLast ? lastItemRef : undefined}
                style={{
                  position: "absolute",
                  top: 0,
                  left: 0,
                  width: "100%",
                  transform: `translateY(${virtualItem.start}px)`,
                }}
              >
                <SidebarChatItem
                  id={chat.id}
                  type={SidebarOption.ChatItem}
                  label={chat.title}
                />
              </div>
            );
          })}
        </div>

        {isLoadingMore && (
          <ContentWrapper justify="center" padding="10px">
            <Loader size="sm" />
          </ContentWrapper>
        )}
      </div>
    </ContentWrapper>
  );
};

export default SidebarContent;
