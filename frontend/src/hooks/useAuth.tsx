// src/hooks/useAuth.tsx
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
} from "@/redux/slices/authSlice";
import { showToast } from "@/utils/showToast";
import type { Credentials, RegisterData, User } from "@/types/user";
import { useUserSWR } from "@/hooks/useUser";

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

  // Utilities
  hasRole: (role: string | string[]) => boolean;
  hasPermission: (permission: string) => boolean;
}

export const useAuth = (): UseAuthReturn => {
  const dispatch = useAppDispatch();
  const navigate = useNavigate();

  const { user, loading: isLoading, error } = useUserSWR();

  const login = useCallback(
    async (credentials: Credentials): Promise<void> => {
      try {
        await dispatch(loginUser(credentials)).unwrap();
      } catch (error) {
        showToast.error((error as string) || "Login failed");
        throw error;
      }
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

  // const register = useCallback(
  //   async (userData: RegisterData): Promise<void> => {
  //     try {
  //       await dispatch(registerUser(userData)).unwrap();
  //     } catch (error) {
  //       showToast.error((error as string) || "Registration failed");
  //       throw error;
  //     }
  //   },
  //   [dispatch]
  // );

  const logout = useCallback(async (): Promise<void> => {
    try {
      await dispatch(logoutUser()).unwrap();
      navigate("/signin");
    } catch (error) {
      dispatch(resetAuth());
      navigate("/signin");
    }
  }, [dispatch, navigate]);

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
