import i18n from "@/i18n/config";
import { createSlice, createAsyncThunk } from "@reduxjs/toolkit";
import { chatApi } from "@/api/chatApi";
import type { PayloadAction } from "@reduxjs/toolkit";
import exceptionWrapper from "@/utils/exceptionWrapper";
import type { MessageResponse } from "@/types/message";
import type { UUID } from "@/types/global";

interface MessagesState {
  messages: MessageResponse[];
  isLoading: boolean;
  error: string | null;
}

const initialState: MessagesState = {
  messages: [],
  isLoading: false,
  error: null,
};

export const fetchMessagesAction = createAsyncThunk(
  "messages/fetchMessages",
  async (chatId: UUID, { rejectWithValue }) => {
    const response = await exceptionWrapper(async () => {
      return chatApi.getMessagesForChat(chatId);
    });

    if (!response.success) {
      return rejectWithValue(i18n.t("toasts.fetchMessagesFailed"));
    }
    return response.data;
  },
);

export const postMessagesAction = createAsyncThunk(
  "messages/postMessage",
  async (
    { chatId, content }: { chatId: UUID; content: string },
    { rejectWithValue },
  ) => {
    const response = await exceptionWrapper(async () => {
      return chatApi.postMessageForChat(chatId, content, "USER");
    });

    if (!response.success) {
      return rejectWithValue(i18n.t("toasts.postMessageFailed"));
    }
    return response.data;
  },
);

const messagesSlice = createSlice({
  name: "messages",
  initialState,
  reducers: {
    addMessage: (state, action: PayloadAction<MessageResponse>) => {
      state.messages.push(action.payload);
    },
    setMessages: (state, action: PayloadAction<MessageResponse[]>) => {
      state.messages = action.payload;
    },
    deleteMessage: (state, action: PayloadAction<string>) => {
      state.messages = state.messages.filter((m) => m.id !== action.payload);
    },
    clearMessages: (state) => {
      state.messages = [];
    },
  },
  extraReducers: (builder) => {
    builder
      .addCase(fetchMessagesAction.pending, (state) => {
        state.isLoading = true;
        state.error = null;
      })
      .addCase(fetchMessagesAction.rejected, (state, action) => {
        state.isLoading = false;
        state.error = action.payload as string;
      })
      .addCase(
        fetchMessagesAction.fulfilled,
        (state, action: PayloadAction<any>) => {
          state.isLoading = false;
          state.messages = action.payload;
        },
      );
  },
});

export const { addMessage, setMessages, deleteMessage, clearMessages } =
  messagesSlice.actions;
export default messagesSlice.reducer;
