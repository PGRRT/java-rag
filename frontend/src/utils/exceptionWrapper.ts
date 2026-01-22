import i18n from "@/i18n/config";
import { showToast } from "@/utils/showToast";
import type { AxiosResponse } from "axios";

const exceptionWrapper = async <T>(
  fn: () => Promise<AxiosResponse<T>>,
  message?: string
): Promise<{ success: true; data: T } | { success: false; error: unknown }> => {
  try {
    const res = await fn();
    if (message) {
      showToast.info(message);
    }

    const axiosData = res?.data;
    return {
      success: true,
      data: axiosData,
    };
  } catch (error: unknown) {
    if (error instanceof Error) {
      showToast.error(
        i18n.t("errors.operationFailedWithError", { error: error.message })
      );
    } else {
      showToast.error(i18n.t("errors.operationFailed"));
    }
    return {
      success: false,
      error: error,
    };
  }
};

export default exceptionWrapper;
