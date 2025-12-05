import type { NavigateFunction } from "react-router-dom";
import { toast } from "sonner";

interface ToastOptions {
  duration?: number;
  action?: {
    label: string;
    onClick: () => void;
  };
}

export const showToastAndRedirect = (
  message: string,
  url: string,
  duration = 2000,
  router: any
) => {
  showToast.success(message, { duration });
  setTimeout(() => {
    router.push(url);
  }, duration);
};

export const showAsyncToastAndRedirect = async (
  message: string,
  url: string,
  duration = 2000,
  navigate: NavigateFunction
) => {
  return new Promise<void>((resolve) => {
    showToast.success(message, { duration });
    setTimeout(() => {
      navigate(url);
      resolve();
    }, duration);
  });
};

export const showToast = {
  success: (message: string, options?: ToastOptions) => {
    return toast.success(message, options);
  },

  error: (message: string, options?: ToastOptions) => {
    return toast.error(message, options);
  },

  info: (message: string, options?: ToastOptions) => {
    return toast.info(message, options);
  },

  warning: (message: string, options?: ToastOptions) => {
    return toast.warning(message, options);
  },

  loading: (message: string) => {
    return toast.loading(message);
  },

  dismiss: (toastId: string | number) => {
    toast.dismiss(toastId);
  },

  dismissAll: () => {
    toast.dismiss();
  },

  // Specialized functions for common use cases
  // auth: {
  //   loginSuccess: (email?: string) => {
  //     return toast.success(`Welcome back${email ? `, ${email.split('@')[0]}` : ''}! 👋`);
  //   },

  //   loginError: (message?: string) => {
  //     return toast.error(message || "Invalid email or password");
  //   },

  //   registerSuccess: () => {
  //     return toast.success("Account created successfully! Welcome to Signaro! 🎉", {
  //       duration: 5000,
  //     });
  //   },

  //   emailSent: (email: string) => {
  //     return toast.success(`Verification email sent to ${email}!`);
  //   },

  //   emailError: () => {
  //     return toast.error("Failed to send verification email");
  //   },

  //   invalidEmail: () => {
  //     return toast.error("Please enter a valid email address");
  //   },

  //   logoutSuccess: () => {
  //     return toast.success("Logged out successfully");
  //   },
  // },

  // API utility functions
  async: {
    promise: <T>(
      promise: Promise<T>,
      messages: {
        loading: string;
        success: string | ((data: T) => string);
        error: string | ((error: any) => string);
      }
    ) => {
      return toast.promise(promise, messages);
    },

    withLoading: async <T>(
      asyncFn: () => Promise<T>,
      messages: {
        loadingMessage?: string;
        successMessage?: string | ((data: T) => string);
        errorMessage?: string | ((error: any) => string);
      }
    ): Promise<
      { data: T; error?: undefined } | { data?: undefined; error: any }
    > => {
      const { loadingMessage, successMessage, errorMessage } = messages ?? {};
      const loadingToast = toast.loading(loadingMessage);

      try {
        const result = await asyncFn();

        toast.dismiss(loadingToast);

        if (successMessage) {
          const message =
            typeof successMessage === "function"
              ? successMessage(result)
              : successMessage;
          toast.success(message);
        }

        return { data: result };
      } catch (error: string | any) {
        toast.dismiss(loadingToast);

        if (errorMessage) {
          const message = errorMessage
            ? typeof errorMessage === "function"
              ? errorMessage(error)
              : errorMessage
            : "An error occurred";

          toast.error(message);
        } else {
          toast.error(error);
        }

        return { error };
      }
    },
  },
};
