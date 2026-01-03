// src/redux/slices/authSlice.ts
import { createSlice, createAsyncThunk } from "@reduxjs/toolkit";
// import { User, AuthState, Credentials, RegisterData } from '@/types/user';
import { chatApi } from "@/api/chatApi";
import type { PayloadAction } from "@reduxjs/toolkit";
import type { ChatRoomType } from "@/api/enums/ChatRoom";
import exceptionWrapper from "@/utils/exceptionWrapper";
import { revalidateChats } from "@/hooks/useInfiniteChats";

const initialState = {
  chats: [] as Array<{ id: string; title: string }>,
  isLoading: false,
  error: null as string | null,
  chatsRefreshTrigger: false as boolean,
};

// export const fetchChatsAction = createAsyncThunk(
//   "chat/fetchChats",
//   async (_, { rejectWithValue }) => {
//     const response = await exceptionWrapper(async () => {
//       return chatApi.getChats();
//     });

//     if (!response.success) {
//       return rejectWithValue("Failed to fetch chats");
//     }
//     return response.data;
//   }
// );

export const createChatAction = createAsyncThunk(
  "chat/createChat",
  async (
    { message, chatType }: { message: string; chatType: ChatRoomType },
    { rejectWithValue }
  ) => {
    console.log("message",message);
    console.log("chatType",chatType);
    
    const response = await exceptionWrapper(async () => {
      return chatApi.createChat(message, chatType);
    }, "Chat created successfully");

    if (!response.success) {
      return rejectWithValue("Failed to create chat");
    }

    // revalidateChats();

    return response.data;
  }
);

const chatSlice = createSlice({
  name: "chat",
  initialState,
  reducers: {
    clearChatsRefreshTrigger: (state) => {
      state.chatsRefreshTrigger = false;
    }
  },
  extraReducers: (builder) => {
    builder.addCase(createChatAction.fulfilled, (state) => {
      state.chatsRefreshTrigger = true;
    });
    // builder
    //   .addCase(fetchChatsAction.pending, (state) => {
    //     state.isLoading = true;
    //     state.error = null;
    //   })
    //   .addCase(fetchChatsAction.rejected, (state, action) => {
    //     state.isLoading = false;
    //     state.error = action.payload as string;
    //   })
    //   .addCase(
    //     fetchChatsAction.fulfilled,
    //     (state, action: PayloadAction<any>) => {
    //       state.chats = action.payload;
    //     }
    //   );
  },
});

export const { clearChatsRefreshTrigger } = chatSlice.actions;
export default chatSlice.reducer;
