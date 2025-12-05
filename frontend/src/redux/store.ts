import type { Action, ThunkAction } from "@reduxjs/toolkit";
import { combineSlices, configureStore } from "@reduxjs/toolkit";
import chatReducer from "./slices/chatSlice";
import messageReducer from "./slices/messageSlice";
import authReducer from "./slices/authSlice";
// import authReducer from "./slices/authSlice";
// everything is a template from https://github.dev/vercel/next.js/tree/canary/examples/with-redux

// `combineSlices` automatically combines the reducers using
// their `reducerPath`s, therefore we no longer need to call `combineReducers`.
// { auth: authReducer }

// `makeStore` encapsulates the store configuration to allow
// creating unique store instances, which is particularly important for
// server-side rendering (SSR) scenarios. In SSR, separate store instances
// are needed for each request to prevent cross-request state pollution.
const makeStore = () => {
  return configureStore({
    reducer: {
      chat: chatReducer,
      message: messageReducer,
      auth: authReducer,
    },
    // Adding the api middleware enables caching, invalidation, polling,
    // and other useful features of `rtk-query`.
    // middleware: (getDefaultMiddleware) => {
    //   return getDefaultMiddleware().concat(quotesApiSlice.middleware);
    // },
  });
};

export const store = makeStore();

export type RootState = ReturnType<ReturnType<typeof makeStore>["getState"]>;

// Infer the return type of `makeStore`
export type AppStore = ReturnType<typeof makeStore>;
// Infer the `AppDispatch` type from the store itself
export type AppDispatch = AppStore["dispatch"];
export type AppThunk<ThunkReturnType = void> = ThunkAction<
  ThunkReturnType,
  RootState,
  unknown,
  Action
>;
