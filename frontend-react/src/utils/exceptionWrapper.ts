import { showToast } from "@/utils/showToast";

const exceptionWrapper = async (fn: () => Promise<void>) => {
  try {
    const data = await fn();
    showToast.info("Message sent successfully");

    return data;
  } catch (error: unknown) {
    if (error instanceof Error) {
      showToast.error(`Failed to send message: ${error.message}`);
    } else {
      showToast.error("Failed to send message");
    }
  }
};

export default exceptionWrapper;
