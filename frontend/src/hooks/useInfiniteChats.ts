import useSWRInfinite from "swr/infinite";
import axios from "axios";
import { chatApi } from "@/api/chatApi";
import apiClient from "@/api/apiClient";
import type { UUID } from "@/types/global";
import type { PageResponse } from "@/types/backendResponse";
import type { chatMode } from "@/types/sidebarOptions";
import { useEffect } from "react";

const CHATS_PER_PAGE = 10;

const fetcher = (url: string) => apiClient.get(url).then((res) => res.data);

export const revalidateChats = (mutate: any, mode?: chatMode) => {
  if (mode) {
    // Rewaliduj konkretny mode
    const typeParam = mode === "ALL" ? "" : `&type=${mode}`;
    mutate(
      (key: any) =>
        typeof key === "string" &&
        key.includes("/api/v1/chats") &&
        key.includes(typeParam)
    );
  } else {
    // Rewaliduj wszystkie czaty
    mutate((key: any) => typeof key === "string" && key.includes("/api/v1/chats"));
  }
};


export const useInfiniteChats = ({ mode }: { mode: chatMode }) => {
  const getKey = (pageIndex: number, previousPageData: PageResponse | null) => {
    if (previousPageData && previousPageData.last) return null;

    let url = `/api/v1/chats?page=${pageIndex}&size=${CHATS_PER_PAGE}`;

    if (mode === "GLOBAL") {
      url += "&type=GLOBAL";
    } else if (mode === "PRIVATE") {
      url += "&type=PRIVATE";
    }

    return url;
  };

  const { data, error, size, setSize, isLoading, isValidating } =
    useSWRInfinite<PageResponse>(getKey, fetcher, {
      revalidateFirstPage: false, // Don't revalidate the first page on focus
      persistSize: true, // Keep the size when revalidating
      revalidateOnFocus: false, // Disable revalidation on window focus
      dedupingInterval: 60000, // 1 minute deduplication interval
    });

  useEffect(() => {
    // Reset to first page when mode changes
    setSize(1);
  }, [mode]);

  const chats = data ? data.flatMap((page) => page.content) : [];
  const isLoadingMore =
    isLoading || (size > 0 && data && typeof data[size - 1] === "undefined");
  const isEmpty = data?.[0]?.content?.length === 0;
  const isReachingEnd =
    isEmpty || (data && data[data?.length - 1]?.last === true);

  const loadMore = () => {
    if (!isLoadingMore && !isReachingEnd) {
      setSize(size + 1);
    }
  };

  const isErrorInitial = error && !data;

  return {
    chats,
    isLoading,
    isLoadingMore,
    isReachingEnd,
    loadMore,
    error,
    isErrorInitial,
  };
};
