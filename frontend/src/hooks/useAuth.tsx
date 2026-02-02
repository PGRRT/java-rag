import { useCallback, useMemo } from "react";
import { useNavigate } from "react-router-dom";
import { useAppDispatch } from "@/redux/hooks";
import {
  loginUserAction,
  registerUserAction,
  logoutUserAction,
  clearError,
  updateUser,
  resetAuth,
  requestOtpAction,
  deleteAccountAction,
} from "@/redux/slices/authSlice";
import { showToast } from "@/utils/showToast";
import type { Credentials, RegisterData, User } from "@/types/user";
import { useUserSWR } from "@/hooks/useUser";
import { useSWRConfig } from "swr";
import { useTranslation } from "react-i18next";

interface UseAuthReturn {
  // State
  user: User | null;
  isLoading: boolean;
  error: string | null;

  // Actions
  login: (credentials: Credentials) => Promise<any>;
  register: (userData: RegisterData) => Promise<any>;
  logout: () => Promise<void>;
  deleteAccount: () => Promise<void>;
  updateProfile: (updates: Partial<User>) => void;
  clearAuthError: () => void;

  createOtp: (email: string) => Promise<any>;

  // Utilities
  hasRole: (role: string | string[]) => boolean;
  hasPermission: (permission: string) => boolean;
}

export const useAuth = (): UseAuthReturn => {
  const dispatch = useAppDispatch();
  const navigate = useNavigate();
  const { t } = useTranslation();

  const { user, loading: isLoading, error } = useUserSWR();
  const { mutate } = useSWRConfig();

  const login = useCallback(
    async (credentials: Credentials) => {
      const response = await showToast.async.withLoading(
        () => dispatch(loginUserAction(credentials)).unwrap(),
        {
          loadingMessage: t("toasts.loggingIn"),
          successMessage: t("toasts.loginSuccess"),
          errorMessage: (err) => err || t("toasts.loginFailed"),
        },
      );

      await mutate(() => true, undefined, { revalidate: false });

      return response;
    },
    [dispatch, mutate, t],
  );

  const register = useCallback(
    async (data: RegisterData) => {
      const response = await showToast.async.withLoading(
        () => dispatch(registerUserAction(data)).unwrap(),
        {
          loadingMessage: t("toasts.registering"),
          successMessage: t("toasts.registerSuccess"),
          errorMessage: (err) => err || t("toasts.registerFailed"),
        },
      );
      await mutate(() => true, undefined, { revalidate: false });

      return response;
    },
    [dispatch, mutate, t],
  );

  const createOtp = useCallback(
    async (email: string) => {
      const response = await showToast.async.withLoading(
        () => dispatch(requestOtpAction(email)).unwrap(),
        {
          loadingMessage: t("toasts.sendingOtp"),
          successMessage: t("toasts.otpSentSuccess"),
          errorMessage: (err) => err || t("toasts.otpSendFailed"),
        },
      );

      return response;
    },
    [dispatch, t],
  );

  const logout = useCallback(async (): Promise<void> => {
    const response = await showToast.async.withLoading(
      () => dispatch(logoutUserAction()).unwrap(),
      {
        loadingMessage: t("toasts.loggingOut"),
        successMessage: t("toasts.logoutSuccess"),
        errorMessage: (err) => err || t("toasts.logoutFailed"),
      },
    );

    await mutate(() => true, undefined, { revalidate: false });

    const { data, error } = response ?? {};

    if (data) {
      navigate("/sign-in");
    } else if (error) {
      dispatch(resetAuth());
      navigate("/sign-in");
    }
  }, [dispatch, navigate, mutate, t]);

  const deleteAccount = useCallback(async (): Promise<void> => {
    const response = await showToast.async.withLoading(
      () => dispatch(deleteAccountAction()).unwrap(),
      {
        loadingMessage: t("toasts.deletingAccount"),
        successMessage: t("toasts.deleteAccountSuccess"),
        errorMessage: (err) => err || t("toasts.deleteAccountFailed"),
      },
    );

    await mutate(() => true, undefined, { revalidate: false });

    const { data, error } = response ?? {};

    if (data) {
      navigate("/sign-in");
    } else if (error) {
      dispatch(resetAuth());
      navigate("/sign-in");
    }
  }, [dispatch, navigate, mutate, t]);

  const updateProfile = useCallback(
    (updates: Partial<User>): void => {
      dispatch(updateUser(updates));
    },
    [dispatch],
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
    [user],
  );

  const hasPermission = useCallback(
    (permission: string): boolean => {
      if (!user) return false;
      // Adjust based on your User type - remove attributes check if not needed
      return false;
    },
    [user],
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
      deleteAccount,
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
      deleteAccount,
      updateProfile,
      clearAuthError,
      hasRole,
      hasPermission,
    ],
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
