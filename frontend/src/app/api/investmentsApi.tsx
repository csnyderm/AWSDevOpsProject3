import { createApi, fetchBaseQuery } from "@reduxjs/toolkit/query/react";
import { useSelector } from "react-redux";
export type Investment = {
  id: string;
  email: string;
  stockName: string;
  symbol: string;
  shares: number;
  purchasePrice: number;
};
export type newInvestment = {
  email: string;
  stockName: string;
  symbol: string;
  shares: number;
  purchasePrice: number;
};

export const investmentsApi = createApi({
  reducerPath: "investmentsApi",
  baseQuery: fetchBaseQuery({
    credentials: "include",
    //baseUrl: " http://localhost:8125/investments",
    baseUrl: "https://api.aws-tfbd.com/investments",
  }),
  tagTypes: ["investment"],

  endpoints: (builder) => ({
    getInvestmentsByEmail: builder.query<Investment[], string>({
      query: (email) => `/email/${email}`,
      providesTags: ["investment"],
    }),

    addInvestment: builder.mutation<void, newInvestment>({
      query: (investment) => ({
        method: "POST",
        url: "/new",
        body: investment,
      }),
      invalidatesTags: ["investment"],
    }),
    updateInvestment: builder.mutation<Investment, Investment>({
      query: (updatedInvestment) => ({
        method: "PUT",
        url: "/update",
        body: updatedInvestment,
      }),
      invalidatesTags: ["investment"],
    }),
    deleteInvestment: builder.mutation<void, Investment>({
      query: (investmentToDelete) => ({
        method: "DELETE",
        url: "/delete",
        body: investmentToDelete,
      }),
      invalidatesTags: ["investment"],
    }),
  }),
});

export const {
  useGetInvestmentsByEmailQuery,
  useAddInvestmentMutation,
  useDeleteInvestmentMutation,
  useUpdateInvestmentMutation,
} = investmentsApi;
