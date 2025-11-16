import { useState } from "react";

const useUser = () => {
  // to do
  const [isLoggedIn, setIsLoggedIn] = useState(false);

  return {
    isLoggedIn,
    setIsLoggedIn,
  }
}

export default useUser;

// import { userApi } from "@/lib/api/userApi";
// import useSWR from "swr";

// const fetcher = () => userApi.getUserClient().then((res) => res.data);

// const UseUser = () => {
//   const {
//     data: user,
//     error,
//     isLoading,
//   } = useSWR("user", fetcher, {
//     dedupingInterval: 1000 * 60 * 5, // 5 minutes
//   });

//   return {
//     user,
//     loading: isLoading,
//     error,
//   };
// };

// export default UseUser;
