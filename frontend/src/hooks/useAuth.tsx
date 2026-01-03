import { useCallback, useEffect, useMemo, useRef } from "react";
import { useNavigate } from "react-router-dom";
import { useAppDispatch, useAppSelector } from "@/redux/hooks";
import {
  loginUser,
  registerUser,
  logoutUser,
  refreshToken,
  clearError,
  updateUser,
  resetAuth,
  requestOtp,
} from "@/redux/slices/authSlice";
import { showToast } from "@/utils/showToast";
import type { Credentials, RegisterData, User } from "@/types/user";
import { useUserSWR } from "@/hooks/useUser";
import { useSWRConfig } from "swr";

interface UseAuthReturn {
  // State
  user: User | null;
  isLoading: boolean;
  error: string | null;

  // Actions
  login: (credentials: Credentials) => Promise<void>;
  register: (userData: RegisterData) => Promise<void>;
  logout: () => Promise<void>;
  updateProfile: (updates: Partial<User>) => void;
  clearAuthError: () => void;

  createOtp: (email: string) => Promise<void>;

  // Utilities
  hasRole: (role: string | string[]) => boolean;
  hasPermission: (permission: string) => boolean;
}

export const useAuth = (): UseAuthReturn => {
  const dispatch = useAppDispatch();
  const navigate = useNavigate();

  const { user, loading: isLoading, error } = useUserSWR();
  const { cache } = useSWRConfig();

  const login = useCallback(
    async (credentials: Credentials) => {
      const response = await showToast.async.withLoading(
        () => dispatch(loginUser(credentials)).unwrap(),
        {
          loadingMessage: "Logging in...",
          successMessage: "Logged in successfully",
          errorMessage: (err) => err || "Login failed",
        }
      );
      return response;
    },
    [dispatch]
  );

  const register = useCallback(
    async (data: RegisterData) => {
      const response = await showToast.async.withLoading(
        () => dispatch(registerUser(data)).unwrap(),
        {
          loadingMessage: "Registering...",
          successMessage: "Registered successfully",
          errorMessage: (err) => err || "Registration failed",
        }
      );
      return response;
    },
    [dispatch]
  );

  const createOtp = useCallback(
    async (email: string) => {
      const response = await showToast.async.withLoading(
        () => dispatch(requestOtp(email)).unwrap(),
        {
          loadingMessage: "Sending OTP...",
          successMessage: "OTP sent successfully",
          errorMessage: (err) => err || "Failed to send OTP",
        }
      );

      return response;
    },
    [dispatch]
  );

  const logout = useCallback(async (): Promise<void> => {
    if (cache instanceof Map) {
      cache.clear();
    }

    const response = await showToast.async.withLoading(
      () => dispatch(logoutUser()).unwrap(),
      {
        loadingMessage: "Logging out...",
        successMessage: "Logged out successfully",
        errorMessage: (err) => err || "Logout failed",
      }
    );

    const { data, error } = response ?? {};

    if (data) {
      navigate("/sign-in");
    } else if (error) {
      dispatch(resetAuth());
      navigate("/sign-in");
    }
  }, [dispatch, navigate, cache]);

  const updateProfile = useCallback(
    (updates: Partial<User>): void => {
      dispatch(updateUser(updates));
    },
    [dispatch]
  );

  const clearAuthError = useCallback((): void => {
    dispatch(clearError());
  }, [dispatch]);

  const hasRole = useCallback(
    (role: string | string[]): boolean => {
      if (!user) return false;

      const roles = Array.isArray(role) ? role : [role];
      return roles.includes(user.role);
    },
    [user]
  );

  const hasPermission = useCallback(
    (permission: string): boolean => {
      if (!user) return false;
      // Adjust based on your User type - remove attributes check if not needed
      return false;
    },
    [user]
  );

  const authState = useMemo(
    () => ({
      user,
      isLoading,
      error,
      login,
      register,
      createOtp,
      logout,
      updateProfile,
      clearAuthError,
      hasRole,
      hasPermission,
    }),
    [
      user,
      isLoading,
      error,
      login,
      register,
      createOtp,
      logout,
      updateProfile,
      clearAuthError,
      hasRole,
      hasPermission,
    ]
  );

  return authState;
};

// Convenience hooks for specific use cases
export const useCurrentUser = () => {
  const { user } = useAuth();
  return user;
};

export const useUserRole = () => {
  const { user, hasRole } = useAuth();
  return { role: user?.role, hasRole };
};
