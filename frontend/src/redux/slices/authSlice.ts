// src/redux/slices/authSlice.ts
import { createSlice, createAsyncThunk } from "@reduxjs/toolkit";
import type { PayloadAction } from "@reduxjs/toolkit";
import { authApi } from "@/api/authApi";
import { userApi } from "@/api/userApi";
import type { User, AuthState, Credentials, RegisterData } from "@/types/user";

const initialState: AuthState = {
  user: null,
  accessToken: null,
  isLoading: false,
  error: null,
};

export const loginUser = createAsyncThunk(
  "auth/login",
  async (credentials: Credentials, { rejectWithValue }) => {
    try {
      console.log("Logging in with credentials:", credentials);
      const response = await authApi.login(credentials);
      return response.data;
    } catch (error: any) {
      return rejectWithValue(error.response?.data?.message || "Login failed");
    }
  }
);

export const registerUser = createAsyncThunk(
  "auth/register",
  async (userData: RegisterData, { rejectWithValue }) => {
    try {
      const response = await authApi.register(userData);
      return response.data;
    } catch (error: any) {
      return rejectWithValue(
        error.response?.data?.message || "Registration failed"
      );
    }
  }
);

export const requestOtp = createAsyncThunk(
  "auth/createOtp",
  async (email: string, { rejectWithValue }) => {
    try {
      const response = await userApi.createOtp(email);
      return response.data;
    } catch (error: any) {
      return rejectWithValue(
        error.response?.data?.message || "Failed to send OTP"
      );
    }
  }
);

export const refreshToken = createAsyncThunk(
  "auth/refreshToken",
  async (_, { rejectWithValue }) => {
    try {
      const response = await authApi.refresh();
      return response.data;
    } catch (error: any) {
      return rejectWithValue(
        error.response?.data?.message || "Token refresh failed"
      );
    }
  }
);


// export const getCurrentUser = createAsyncThunk(
//   "auth/getCurrentUser",
//   async (_, { rejectWithValue }) => {
//     try {
//       const response = await userApi.getProfile();
//       console.log("asdsadasd", response.data);
      
//       return response.data;
//     } catch (error: any) {
//       console.log("Error while getting current user", error);
      
//       return rejectWithValue(
//         error.response?.data?.message || "Failed to get user"
//       );
//     }
//   }
// );

export const logoutUser = createAsyncThunk(
  "auth/logout",
  async (_, { rejectWithValue }) => {
    try {
      await authApi.logout();
      return true;
    } catch (error: any) {
      return true;
    }
  }
);

const authSlice = createSlice({
  name: "auth",
  initialState,
  reducers: {
    clearError: (state) => {
      state.error = null;
    },
    updateUser: (state, action: PayloadAction<Partial<User>>) => {
      if (state.user) {
        state.user = { ...state.user, ...action.payload };
      }
    },
    resetAuth: () => initialState,
  },
  extraReducers: (builder) => {
    // Login
    builder
      .addCase(loginUser.pending, (state) => {
        state.isLoading = true;
        state.error = null;
      })
      .addCase(loginUser.fulfilled, (state, action) => {
        console.log("Login redux", action.payload);

        state.isLoading = false;
        state.user = action.payload.user;
        state.error = null;
        state.accessToken = action.payload.accessToken || null;
      })
      .addCase(loginUser.rejected, (state, action) => {
        state.isLoading = false;
        state.user = null;
        state.error = action.payload as string;
      });

    // Register
    builder
      .addCase(registerUser.pending, (state) => {
        state.isLoading = true;
        state.error = null;
      })
      .addCase(registerUser.fulfilled, (state, action) => {
        state.isLoading = false;
        state.user = action.payload.user;
        state.error = null;
        state.accessToken = action.payload.accessToken || null;
      })
      .addCase(registerUser.rejected, (state, action) => {
        state.isLoading = false;
        state.error = action.payload as string;
      });

    // // Get Current User
    // builder
    //   .addCase(getCurrentUser.pending, (state) => {
    //     state.isLoading = true;
    //   })
    //   .addCase(getCurrentUser.fulfilled, (state, action) => {
    //     state.isLoading = false;
    //     state.user = action.payload.user || action.payload;
    //     state.error = null;
    //   })
    //   .addCase(getCurrentUser.rejected, (state, action) => {
    //     state.isLoading = false;
    //     state.user = null;
    //     state.error = action.payload as string;
    //   });

    // Refresh Token
    builder
      .addCase(refreshToken.fulfilled, (state, action) => {
        state.user = action.payload.user;
        state.accessToken = action.payload.accessToken;
      })
      .addCase(refreshToken.rejected, (state) => {
        state.user = null;
      });

    // Logout
    builder.addCase(logoutUser.fulfilled, () => initialState);
  },
});

export const { clearError, updateUser, resetAuth } = authSlice.actions;
export default authSlice.reducer;
