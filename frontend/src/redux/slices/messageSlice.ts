import { createSlice, createAsyncThunk } from "@reduxjs/toolkit";
import { userApi } from "@/api/userApi";
import type { PayloadAction } from "@reduxjs/toolkit";
import exceptionWrapper from "@/utils/exceptionWrapper";
import type { MessageResponse } from "@/api/schemas/message";

interface Message {
  id: string;
  chatId: string;
  sender: "user" | "gpt";
  text: string;
  createdAt: string;
}

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
  async (chatId: string, { rejectWithValue }) => {
    const response = await exceptionWrapper(async () => {
      return userApi.getMessagesForChat(chatId);
    });

    if (!response.success) {
      return rejectWithValue("Failed to fetch chats");
    }
    return response.data;
  }
);

export const postMessagesAction = createAsyncThunk(
  "messages/postMessage",
  async (
    { chatId, content }: { chatId: string; content: string },
    { rejectWithValue }
  ) => {
    const response = await exceptionWrapper(async () => {
      return userApi.postMessageForChat(chatId, content, "USER");
    });

    if (!response.success) {
      return rejectWithValue("Failed to post message");
    }
    return response.data;
  }
);

const messagesSlice = createSlice({
  name: "messages",
  initialState,
  reducers: {
    addMessage: (state, action: PayloadAction<Message>) => {
      state.messages.push(action.payload);
    },
    deleteMessage: (state, action: PayloadAction<string>) => {
      state.messages = state.messages.filter((m) => m.id !== action.payload);
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
        }
      );
  },
});

export const { addMessage, deleteMessage } = messagesSlice.actions;
export default messagesSlice.reducer;
