import useSWR from "swr";
import { useAppDispatch, useAppSelector } from "@/redux/hooks";
import { updateUser } from "@/redux/slices/authSlice";
import { userApi } from "@/api/userApi";

const fetchUser = () => userApi.getProfile().then((res) => res.data);

export const useUserSWR = () => {
  const dispatch = useAppDispatch();
  const reduxUser = useAppSelector((state) => state.auth.user);
  const accessToken = useAppSelector((state) => state.auth.accessToken);

  const { data, error, isLoading } = useSWR(
    accessToken ? "user" : null,
    fetchUser,
    {
      dedupingInterval: 1000 * 60 * 5, // cache 5 minutes
      onSuccess: (user) => {
        dispatch(updateUser(user)); // save result to Redux
      },
    }
  );

  return {
    user: data || reduxUser, // if no data from SWR, fallback to Redux state
    loading: isLoading,
    error,
  };
};
