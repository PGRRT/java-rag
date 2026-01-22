import i18n from "@/i18n/config";
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

export const loginUserAction = createAsyncThunk(
  "auth/login",
  async (credentials: Credentials, { rejectWithValue }) => {
    try {
      const response = await authApi.login(credentials);
      return response.data;
    } catch (error: any) {
      return rejectWithValue(
        error.response?.data?.message || i18n.t("toasts.loginFailed")
      );
    }
  }
);

export const registerUserAction = createAsyncThunk(
  "auth/register",
  async (userData: RegisterData, { rejectWithValue }) => {
    try {
      const response = await authApi.register(userData);
      return response.data;
    } catch (error: any) {
      return rejectWithValue(
        error.response?.data?.message || i18n.t("toasts.registerFailed")
      );
    }
  }
);

export const requestOtpAction = createAsyncThunk(
  "auth/createOtp",
  async (email: string, { rejectWithValue }) => {
    try {
      const response = await userApi.createOtp(email);
      return response.data;
    } catch (error: any) {
      return rejectWithValue(
        error.response?.data?.message || i18n.t("toasts.otpSendFailed")
      );
    }
  }
);

// export const refreshToken = createAsyncThunk(
//   "auth/refreshToken",
//   async (_, { rejectWithValue }) => {
//     try {
//       const response = await authApi.refresh();
//       return response.data;
//     } catch (error: any) {
//       return rejectWithValue(
//         error.response?.data?.message || "Token refresh failed"
//       );
//     }
//   }
// );

export const logoutUserAction = createAsyncThunk(
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

export const deleteAccountAction = createAsyncThunk(
  "auth/deleteAccount",
  async (_, { rejectWithValue }) => {
    try {
      await userApi.deleteMe();
      return true;
    } catch (error: any) {
      return rejectWithValue(
        error.response?.data?.message || i18n.t("toasts.deleteAccountFailed")
      );
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
    setAccessToken: (state, action: PayloadAction<string | null>) => {
      state.accessToken = action.payload;
    },
    setUser: (state, action: PayloadAction<User | null>) => {
      state.user = action.payload;
    },
    resetAuth: () => initialState,
  },
  extraReducers: (builder) => {
    // Login
    builder
      .addCase(loginUserAction.pending, (state) => {
        state.isLoading = true;
        state.error = null;
      })
      .addCase(loginUserAction.fulfilled, (state, action) => {
        state.isLoading = false;
        state.user = action.payload.user;
        state.error = null;
        state.accessToken = action.payload.accessToken || null;
      })
      .addCase(loginUserAction.rejected, (state, action) => {
        state.isLoading = false;
        state.user = null;
        state.error = action.payload as string;
      });

    // Register
    builder
      .addCase(registerUserAction.pending, (state) => {
        state.isLoading = true;
        state.error = null;
      })
      .addCase(registerUserAction.fulfilled, (state, action) => {
        state.isLoading = false;
        state.user = action.payload.user;
        state.error = null;
        state.accessToken = action.payload.accessToken || null;
      })
      .addCase(registerUserAction.rejected, (state, action) => {
        state.isLoading = false;
        state.error = action.payload as string;
      });

    // Delete Account
    builder
      .addCase(deleteAccountAction.pending, (state) => {
        state.isLoading = true;
        state.error = null;
      })
      .addCase(deleteAccountAction.fulfilled, () => {
        return initialState; // Clear all auth state
      })
      .addCase(deleteAccountAction.rejected, (state, action) => {
        state.isLoading = false;
        state.error = action.payload as string;
      });

    // Refresh Token
    // builder
    //   .addCase(refreshToken.fulfilled, (state, action) => {
    //     state.user = action.payload.user;
    //     state.accessToken = action.payload.accessToken;
    //   })
    //   .addCase(refreshToken.rejected, (state) => {
    //     state.user = null;
    //   });

    // Logout
    builder.addCase(logoutUserAction.fulfilled, () => {
      return initialState;
    });
  },
});

export const { clearError, updateUser, setAccessToken, setUser, resetAuth } =
  authSlice.actions;

export default authSlice.reducer;
