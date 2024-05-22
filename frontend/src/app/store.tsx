import { combineReducers, configureStore } from "@reduxjs/toolkit";
import userReducer from "./features/userSlice";
import storage from "redux-persist/lib/storage";

import {
  FLUSH,
  PAUSE,
  PERSIST,
  PURGE,
  REGISTER,
  REHYDRATE,
  persistReducer,
  persistStore,
} from "redux-persist";
import logger from "redux-logger";
import { goalsApi } from "./api/goalsApi";
import { taxApi } from "./api/taxApi";
import { accountApi } from "./api/accountApi";
import { plannerApi } from "./api/plannerApi";
import { investmentsApi } from "./api/investmentsApi";

//initial redux store setup for persitant data
//using redux logger for more helpful redux debugging
const persistConfig = {
  key: "root",
  storage,
};

const persistedReducer = persistReducer(persistConfig, userReducer);
// Combine reducers from userSlice and all APIs
const rootReducer = combineReducers({
  user: persistedReducer,
  [accountApi.reducerPath]: accountApi.reducer,
  [goalsApi.reducerPath]: goalsApi.reducer, // Combine the goalApi reducer
  [taxApi.reducerPath]: taxApi.reducer,
  [plannerApi.reducerPath]: plannerApi.reducer,
  [investmentsApi.reducerPath]: investmentsApi.reducer
});

export const store = configureStore({
  reducer: rootReducer,
  middleware: (getDefaultMiddleware) =>
    getDefaultMiddleware({
      serializableCheck: {
        ignoredActions: [FLUSH, REHYDRATE, PAUSE, PERSIST, PURGE, REGISTER],
      },
    })
      //.concat(logger)
      //add API middleware
      .concat(goalsApi.middleware)
      .concat(taxApi.middleware)
      .concat(accountApi.middleware)
      .concat(plannerApi.middleware)
      .concat(investmentsApi.middleware)
});

export const persistor = persistStore(store);
//exoported type to use for typecript to set the state in useSelector
export type RootState = ReturnType<typeof store.getState>;
export type AppDispatch = typeof store.dispatch;
