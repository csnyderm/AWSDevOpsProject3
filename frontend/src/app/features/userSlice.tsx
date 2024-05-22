import { createSlice, createSelector } from "@reduxjs/toolkit";
import type { PayloadAction } from "@reduxjs/toolkit";

//Initial setup of the userslice that manages the logged in state and email
//These fields are persisted through the entire application and accessible through useSelector()
interface UserState {
  isLoggedIn: boolean;
  email: string;
}

const initialState: UserState = {
  isLoggedIn: false,
  email: "",
} as UserState;

//creating the reducers to enable login and set the state for both objects
export const userSlice = createSlice({
  name: "user",
  initialState,
  reducers: {
    login: (state, action: PayloadAction<string>) => {
      state.isLoggedIn = true;
      state.email = action.payload;
    },
    logout: (state) => {
      state.isLoggedIn = false;
      state.email = "";
    },
  },
});

export const { login, logout } = userSlice.actions;
export default userSlice.reducer;
// Define a selector to extract the email from the user state
export const selectUser = (state: any) => state.user;

export const selectUserEmail = createSelector(selectUser, (user) => user.email);
