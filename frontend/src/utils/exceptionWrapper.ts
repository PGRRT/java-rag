import { showToast } from "@/utils/showToast";

const exceptionWrapper = async <T>(fn: () => Promise<T>, message: string) => {
  try {
    const data = await fn();
    if (message) {
      showToast.info(message);
    }

    return data;
  } catch (error: unknown) {
    if (error instanceof Error) {
      showToast.error(`Failed: ${error.message}`);
    } else {
      showToast.error("Operation failed due to an unknown error.");
    }
  }
};

export default exceptionWrapper;
