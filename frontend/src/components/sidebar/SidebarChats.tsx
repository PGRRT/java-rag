import SidebarChatItem from "@/components/sidebar/SidebarChatItem";
import ContentWrapper from "@/components/ui/ContentWrapper";
import { css, cx } from "@emotion/css";
import { Loader, SegmentedControl } from "@mantine/core";
import { SearchIcon, SquarePen } from "lucide-react";
import { useState, useRef, useCallback, useEffect } from "react";
import { useVirtualizer } from "@tanstack/react-virtual";
import { useInfiniteChats } from "@/hooks/useInfiniteChats";
import { SidebarOption } from "@/types/sidebarOptions";
import { showToast, showToastAndRedirect } from "@/utils/showToast";

const SidebarChats = ({ mode }) => {
  const {
    chats,
    isLoading,
    isLoadingMore,
    loadMore,
    isReachingEnd,
    error,
    isErrorInitial,
  } = useInfiniteChats({
    mode,
  });

  const rowVirtualizer = useVirtualizer({
    count: chats?.length ?? 0,
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

  useEffect(() => {
    if (isErrorInitial || error) {
      showToast.error("Error loading chats.");
    }
  }, [error, isErrorInitial]);
  

  const parentRef = useRef<HTMLDivElement>(null);

  return (
    <>
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
          {!isErrorInitial &&
            rowVirtualizer.getVirtualItems().map((virtualItem) => {
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
    </>
  );
};

export default SidebarChats;
