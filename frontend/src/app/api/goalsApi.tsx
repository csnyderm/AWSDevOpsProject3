import { createApi, fetchBaseQuery } from "@reduxjs/toolkit/query/react";
import { useSelector } from "react-redux";
export type Goal = {
  _id: string;
  email: string;
  name: string;
  goalAmount: number;
  amountSaved: number;
};

export type newGoal = {
  email: string;
  name: string;
  goalAmount: number;
  amountSaved: number;
};

export const goalsApi = createApi({
  reducerPath: "goalsApi",
  baseQuery: fetchBaseQuery({
    //baseUrl: " http://localhost:8125/goals",
    baseUrl: "https://api.skillstormcloud.com/goals", //change //goalsAPI may not work
    credentials: "include",
  }),
  tagTypes: ["goal"],
  endpoints: (builder) => ({
    getAllGoals: builder.query<Goal[], void>({
      query: () => "/",
      providesTags: ["goal"],
    }),

    getGoalById: builder.query<Goal, string>({
      query: (id) => `/id/${id}`,
      providesTags: ["goal"],
    }),

    getGoalsByEmail: builder.query<Goal[], string>({
      query: (email) => `/email/${email}`,
      providesTags: ["goal"],
    }),

    addGoal: builder.mutation<Goal, newGoal>({
      query: (newGoal) => ({
        method: "POST",
        url: "/create",
        body: newGoal,
      }),
      invalidatesTags: ["goal"],
    }),

    updateGoal: builder.mutation<Goal, Goal>({
      query: (updatedGoal) => ({
        method: "PUT",
        url: "/update",
        body: updatedGoal,
      }),
      invalidatesTags: ["goal"],
    }),

    deleteGoal: builder.mutation<void, Goal>({
      query: (goalToDelete) => ({
        method: "DELETE",
        url: "/delete",
        body: goalToDelete,
      }),
      invalidatesTags: ["goal"],
    }),
  }),
});

export const {
  useGetAllGoalsQuery,
  useGetGoalByIdQuery,
  useGetGoalsByEmailQuery,
  useAddGoalMutation,
  useUpdateGoalMutation,
  useDeleteGoalMutation,
} = goalsApi;
