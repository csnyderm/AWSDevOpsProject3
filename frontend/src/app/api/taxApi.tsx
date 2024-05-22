import { createApi, fetchBaseQuery } from "@reduxjs/toolkit/query/react";
export type Deduction = {
  deductionType: string;
  deductionAmount: number;
};

export type Credit = {
  creditType: string;
  creditAmount: number;
};

export type IncomeSource = {
  empID: number;
  incomeType: string;
  state: string;
  income: number;
  fedWithheld: number;
  stateWithheld: number;
};

export type TaxReturn = {
  id: string;
  email: string;
  filingStatus: string;
  totalIncome: number;
  totalStateOwed: number;
  totalFedOwed: number;
  taxableIncome: number;
  agi: number;
  childDependents: number;
  otherDependents: number;
  aotcClaims: number;
  educationalExpenditures: number[];
  incomeSources: IncomeSource[];
  belowLineDeductions: Deduction[];
  aboveLineDeductions: Deduction[];
  credits: Credit[];
};

export type newTaxReturn = {
  email: string;
  filingStatus: string;
  totalIncome: number;
  totalStateOwed: number;
  totalFedOwed: number;
  taxableIncome: number;
  agi: number;
  childDependents: number;
  otherDependents: number;
  aotcClaims: number;
  educationalExpenditures: number[];
  incomeSources: IncomeSource[];
  belowLineDeductions: Deduction[];
  aboveLineDeductions: Deduction[];
  credits: Credit[];
};

export const taxApi = createApi({
  reducerPath: "taxApi",
  baseQuery: fetchBaseQuery({
    //baseUrl: "http://localhost:8125/taxes",
    baseUrl: "https://api.skillstormcloud.com/taxes",
    credentials: "include",
  }),
  tagTypes: ["taxData"],
  endpoints: (builder) => ({
    findAllReturns: builder.query<TaxReturn[], void>({
      query: () => "/",
      providesTags: ["taxData"],
    }),

    findOne: builder.query<TaxReturn, string>({
      query: (email) => `/${email}`,
      providesTags: ["taxData"],
    }),

    // added for Income Source
    findIncomeSource: builder.query<
      IncomeSource,
      { email: string; ein: number }
    >({
      query: ({ email, ein }) => `/${email}/${ein}`,
      providesTags: ["taxData"],
    }),

    deleteTaxReturn: builder.mutation<TaxReturn, TaxReturn>({
      query: () => ({
        url: "/delete",
        method: "DELETE",
      }),
      invalidatesTags: ["taxData"],
    }),

    deleteReturnByEmail: builder.mutation<void, string>({
      query: (email) => ({
        url: `/delete/${email}`,
        method: "DELETE",
      }),
      invalidatesTags: ["taxData"],
    }),

    // added DELETE Income Source
    deleteIncomeSourceByEmailAndEIN: builder.mutation<
      void,
      { email: string; ein: number }
    >({
      query: ({ email, ein }) => ({
        url: `/delete/${email}/${ein}`,
        method: "DELETE",
      }),
      invalidatesTags: ["taxData"],
    }),

    newReturn: builder.mutation<TaxReturn, newTaxReturn>({
      query: (newTaxReturn) => ({
        url: "/new",
        method: "POST",
        body: newTaxReturn,
      }),
      invalidatesTags: ["taxData"],
    }),

    updateTaxReturn: builder.mutation<TaxReturn, TaxReturn>({
      query: (updatedTaxReturn) => ({
        url: "/update",
        method: "PUT",
        body: updatedTaxReturn,
      }),
      invalidatesTags: ["taxData"],
    }),

    // added UPDATE Income Source
    updateTaxReturnIncomeSource: builder.mutation<
      IncomeSource,
      { email: string; updatedIncomeSource: IncomeSource }
    >({
      query: ({ email, updatedIncomeSource }) => ({
        url: `/update/${email}`,
        method: "PUT",
        body: updatedIncomeSource,
      }),
      invalidatesTags: ["taxData"],
    }),
  }),
});

export const {
  useFindAllReturnsQuery,
  useFindOneQuery,
  useFindIncomeSourceQuery,
  useDeleteTaxReturnMutation,
  useDeleteReturnByEmailMutation,
  useDeleteIncomeSourceByEmailAndEINMutation,
  useNewReturnMutation,
  useUpdateTaxReturnMutation,
  useUpdateTaxReturnIncomeSourceMutation,
} = taxApi;
