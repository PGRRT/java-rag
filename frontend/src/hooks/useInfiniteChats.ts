import useSWRInfinite from "swr/infinite";
import axios from "axios";
import { chatApi } from "@/api/chatApi";
import apiClient from "@/api/apiClient";
import type { UUID } from "@/types/index";

const CHATS_PER_PAGE = 10;

interface Chat {
  id: UUID;
  title: string;
  createdAt?: string;
  updatedAt?: string;
}

interface PageResponse {
  content: Chat[];
  empty: boolean;
  first: boolean;
  last: boolean;
  number: number;
  numberOfElements: number;
  size: number;
  totalElements: number;
  totalPages: number;
}

const fetcher = (url: string) => apiClient.get(url).then((res) => res.data);

export const useInfiniteChats = () => {
  const getKey = (pageIndex: number, previousPageData: PageResponse | null) => {
    if (previousPageData && previousPageData.last) return null;

    return `/api/v1/chats?page=${pageIndex}&size=${CHATS_PER_PAGE}&includeGlobal=true`;
  };

  const { data, error, size, setSize, isLoading, isValidating } =
    useSWRInfinite<PageResponse>(getKey, fetcher, {
      revalidateFirstPage: false, // Don't revalidate the first page on focus
      persistSize: true, // Keep the size when revalidating
      revalidateOnFocus: false, // Disable revalidation on window focus
      dedupingInterval: 60000, // 1 minute deduplication interval
    });

  const chats = data ? data.flatMap((page) => page.content) : [];
  const isLoadingMore =
    isLoading || (size > 0 && data && typeof data[size - 1] === "undefined");
  const isEmpty = data?.[0]?.content.length === 0;
  const isReachingEnd =
    isEmpty || (data && data[data.length - 1]?.last === true);

  const loadMore = () => {
    if (!isLoadingMore && !isReachingEnd) {
      setSize(size + 1);
    }
  };

  return {
    chats,
    isLoading,
    isLoadingMore,
    isReachingEnd,
    loadMore,
    error,
  };
};
