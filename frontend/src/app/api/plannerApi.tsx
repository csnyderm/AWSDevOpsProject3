import { createApi, fetchBaseQuery } from "@reduxjs/toolkit/query/react";
export type Expense = {
  category: string;
  expenseName: string;
  actualExpense: number;
  desiredExpense: number;
  dueDate: string;
};

export type Planner = {
  email: string;
  categories: string[];
  expenses: Expense[];
  monthlyIncome: number;
};

export const plannerApi = createApi({
  reducerPath: "plannerApi",
  baseQuery: fetchBaseQuery({
    credentials: "include",
    //baseUrl: " http://localhost:8125/planner",
    baseUrl: "https://api.aws-tfbd.com/planner",
  }),
  tagTypes: ["planner"],
  endpoints: (builder) => ({
    getAllPlanners: builder.query<Planner[], void>({
      query: () => "/",
      providesTags: ["planner"],
    }),

    getPlannerByEmail: builder.query<Planner, string>({
      query: (email) => `/email/${email}`,
      providesTags: ["planner"],
    }),

    addPlanner: builder.mutation<Planner, Planner>({
      query: (Planner) => ({
        method: "POST",
        url: "/create",
        body: Planner,
      }),
      invalidatesTags: ["planner"],
    }),

    updatePlanner: builder.mutation<Planner, Planner>({
      query: (updatePlanner) => ({
        method: "PUT",
        url: "/update",
        body: updatePlanner,
      }),
      invalidatesTags: ["planner"],
    }),

    deletePlanner: builder.mutation<VoidFunction, Planner>({
      query: (plannerToDelete) => ({
        method: "DELETE",
        url: "/delete",
        body: plannerToDelete,
      }),
      invalidatesTags: ["planner"],
    }),
  }),
});

export const {
  useGetAllPlannersQuery,
  useGetPlannerByEmailQuery,
  useAddPlannerMutation,
  useUpdatePlannerMutation,
  useDeletePlannerMutation,
} = plannerApi;
