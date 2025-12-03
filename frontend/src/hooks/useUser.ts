import { userApi } from "@/api/userApi";
import useSWR from "swr";

const fetcher = () => userApi.getProfile().then((res) => res.data);

const UseUser = () => {
  const {
    data: user,
    error,
    isLoading,
  } = useSWR("user", fetcher, {
    dedupingInterval: 1000 * 60 * 5, // 5 minutes
  });

  return {
    user,
    loading: isLoading,
    error,
  };
};

export default UseUser;
