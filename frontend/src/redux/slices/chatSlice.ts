// src/redux/slices/authSlice.ts
import { createSlice, createAsyncThunk } from "@reduxjs/toolkit";
// import { User, AuthState, Credentials, RegisterData } from '@/types/user';
import { chatApi } from "@/api/chatApi";
import type { PayloadAction } from "@reduxjs/toolkit";
import type { ChatRoomType } from "@/api/enums/ChatRoom";
import exceptionWrapper from "@/utils/exceptionWrapper";

const initialState = {
  chats: [] as Array<{ id: string; title: string }>,
  isLoading: false,
  error: null as string | null,
};

export const fetchChatsAction = createAsyncThunk(
  "chat/fetchChats",
  async (_, { rejectWithValue }) => {
    const response = await exceptionWrapper(async () => {
      return chatApi.getChats();
    });

    if (!response.success) {
      return rejectWithValue("Failed to fetch chats");
    }
    return response.data;
  }
);

export const createChatAction = createAsyncThunk(
  "chat/createChat",
  async (
    { message, chatType }: { message: string; chatType: ChatRoomType },
    { rejectWithValue }
  ) => {
    const response = await exceptionWrapper(async () => {
      return chatApi.createChat(message, chatType);
    }, "Chat created successfully");

    if (!response.success) {
      return rejectWithValue("Failed to create chat");
    }
    return response.data;
  }
);

const chatSlice = createSlice({
  name: "chat",
  initialState,
  reducers: {},
  extraReducers: (builder) => {
    builder
      .addCase(fetchChatsAction.pending, (state) => {
        state.isLoading = true;
        state.error = null;
      })
      .addCase(fetchChatsAction.rejected, (state, action) => {
        state.isLoading = false;
        state.error = action.payload as string;
      })
      .addCase(
        fetchChatsAction.fulfilled,
        (state, action: PayloadAction<any>) => {
          state.chats = action.payload;
        }
      );
  },
});

// export const { fetchChats } = chatSlice.actions;
export default chatSlice.reducer;
