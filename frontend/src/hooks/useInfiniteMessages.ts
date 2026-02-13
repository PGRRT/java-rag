import useSWRInfinite from "swr/infinite";
import { mutate } from "swr";
import { chatApi } from "@/api/chatApi";
import type { PageResponse } from "@/types/backendResponse";
import type { MessageResponse } from "@/types/message";
import type { UUID } from "@/types/global";

const MESSAGES_PER_PAGE = 20;

const fetcher = async ({
  chatId,
  page,
  size,
  sort,
}: {
  chatId: UUID;
  page: number;
  size: number;
  sort: string;
}) => {
  const response = await chatApi.getMessagesForChatPaginated(
    chatId,
    page,
    size,
    sort,
  );
  return response.data;
};

export const revalidateMessages = (chatId: UUID) => {
  mutate(
    (key: any) => {
      return (
        typeof key === "string" &&
        key.includes(`/api/v1/chats/${chatId}/messages`)
      );
    },
    undefined,
    { revalidate: true },
  );
};

export const useInfiniteMessages = ({ chatId }: { chatId?: UUID }) => {
  const getKey = (pageIndex: number, previousPageData: PageResponse | null) => {
    if (!chatId) return null;
    if (previousPageData && previousPageData.last) return null;

    // Return parameters for fetcher function
    return {
      chatId,
      page: pageIndex,
      size: MESSAGES_PER_PAGE,
      sort: "createdAt,desc",
    };
  };

  const {
    data,
    error,
    size,
    setSize,
    isLoading,
    isValidating,
    mutate: localMutate,
  } = useSWRInfinite<PageResponse>(getKey, fetcher, {
    revalidateFirstPage: false,
    persistSize: true,
    revalidateOnFocus: false,
  });

  // Flatten messages and reverse to show oldest first (since we fetch newest first)
  const messages: MessageResponse[] = data
    ? data.flatMap((page) => page.content).reverse()
    : [];

  const isLoadingMore =
    isLoading || (size > 0 && data && typeof data[size - 1] === "undefined");
  const isEmpty = data?.[0]?.content?.length === 0;
  const isReachingEnd =
    isEmpty || (data && data[data?.length - 1]?.last === true);

  const loadMoreMessages = () => {
    if (!isLoadingMore && !isReachingEnd) {
      setSize(size + 1);
    }
  };

  const isErrorInitial = error && !data;

  return {
    messages,
    isLoading,
    isLoadingMore,
    isReachingEnd,
    loadMoreMessages,
    error,
    isErrorInitial,
    refresh: localMutate,
  };
};
