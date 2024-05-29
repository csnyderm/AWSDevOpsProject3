import { createApi, fetchBaseQuery } from "@reduxjs/toolkit/query/react";
export interface Bank {
  id: number;
  bankName: string;
  accountType: string;
  balance: number;
}

export interface CreditCard {
  id: number;
  bankName: string;
  creditLimit: number;
  balance: number;
  interestRate: number;
}

export interface Loan {
  id: number;
  bankName: string;
  loanType: string;
  balance: number;
  interestRate: number;
  termLength: number;
  paid: boolean;
}

export interface Account {
  email: string;
  bankAccounts: Bank[];
  creditCards: CreditCard[];
  loans: Loan[];
}

export const accountApi = createApi({
  reducerPath: "accountApi",
  baseQuery: fetchBaseQuery({
    //baseUrl: "http://localhost:8125/accounts",
    baseUrl: "https://api.skillstormcloud.com/accounts", //base url needs to change
    credentials: "include",
  }),
  tagTypes: ["account"],
  endpoints: (builder) => ({
    getAllAccounts: builder.query<Account[], void>({
      query: () => "/",
      providesTags: ["account"],
    }),
    getAccountsByEmail: builder.query<Account, string>({
      query: (email) => `/email/${email}`,
      providesTags: ["account"],
    }),
    createAccounts: builder.mutation<Account, Account>({
      query: (newAccount) => ({
        method: "POST",
        url: "/newAccounts",
        body: newAccount,
      }),
      invalidatesTags: ["account"],
    }),
    updateAccounts: builder.mutation<Account, Account>({
      query: (updatedAccount) => ({
        method: "PUT",
        url: "/updateAccounts",
        body: updatedAccount,
      }),
      invalidatesTags: ["account"],
    }),
    deleteAccounts: builder.mutation<void, Account>({
      query: (accountToDelete) => ({
        method: "DELETE",
        url: "/deleteAccounts",
        body: accountToDelete,
      }),
      invalidatesTags: ["account"],
    }),
    getAllBankAccounts: builder.query<Bank[], string>({
      query: (email) => `/${email}/bankAccounts`,
    }),

    getAllCreditCards: builder.query<CreditCard[], string>({
      query: (email) => `/${email}/creditCards`,
    }),

    getAllLoans: builder.query<Loan[], string>({
      query: (email) => `/${email}/loans`,
    }),

    createBankAccount: builder.mutation<
      Bank,
      { email: string; bankAccount: Bank }
    >({
      query: ({ email, bankAccount }) => ({
        method: "POST",
        url: `/${email}/newBankAccount`,
        body: bankAccount,
      }),
    }),

    createCreditCard: builder.mutation<
      CreditCard,
      { email: string; creditCard: CreditCard }
    >({
      query: ({ email, creditCard }) => ({
        method: "POST",
        url: `/${email}/newCreditCard`,
        body: creditCard,
      }),
    }),

    createLoan: builder.mutation<Loan, { email: string; loan: Loan }>({
      query: ({ email, loan }) => ({
        method: "POST",
        url: `/${email}/newLoan`,
        body: loan,
      }),
    }),

    updateBankAccount: builder.mutation<
      Bank,
      { email: string; bankAccountID: number; updatedBankAccount: Bank }
    >({
      query: ({ email, bankAccountID, updatedBankAccount }) => ({
        method: "PUT",
        url: `/${email}/bankAccounts/update`,
        body: updatedBankAccount,
      }),
    }),

    updateCreditCard: builder.mutation<
      CreditCard,
      { email: string; creditCardID: number; updatedCreditCard: CreditCard }
    >({
      query: ({ email, creditCardID, updatedCreditCard }) => ({
        method: "PUT",
        url: `/${email}/creditCards/update`,
        body: updatedCreditCard,
      }),
    }),

    updateLoan: builder.mutation<
      Loan,
      { email: string; loanID: number; updatedLoan: Loan }
    >({
      query: ({ email, loanID, updatedLoan }) => ({
        method: "PUT",
        url: `/${email}/loans/update`,
        body: updatedLoan,
      }),
    }),

    deleteBankAccount: builder.mutation<
      void,
      { email: string; bankAccountID: number }
    >({
      query: ({ email, bankAccountID }) => ({
        method: "DELETE",
        url: `/${email}/bankAccounts/${bankAccountID}`,
      }),
    }),

    deleteCreditCard: builder.mutation<
      void,
      { email: string; creditCardID: number }
    >({
      query: ({ email, creditCardID }) => ({
        method: "DELETE",
        url: `/${email}/creditCards/${creditCardID}`,
      }),
    }),

    deleteLoan: builder.mutation<void, { email: string; loanID: number }>({
      query: ({ email, loanID }) => ({
        method: "DELETE",
        url: `/${email}/loans/${loanID}`,
      }),
    }),
  }),
});

export const {
  useGetAllAccountsQuery,
  useGetAccountsByEmailQuery,
  useCreateAccountsMutation,
  useDeleteAccountsMutation,
  useUpdateAccountsMutation,
  useGetAllBankAccountsQuery,
  useGetAllCreditCardsQuery,
  useGetAllLoansQuery,
  useCreateBankAccountMutation,
  useCreateCreditCardMutation,
  useCreateLoanMutation,
  useUpdateBankAccountMutation,
  useUpdateCreditCardMutation,
  useUpdateLoanMutation,
  useDeleteBankAccountMutation,
  useDeleteCreditCardMutation,
  useDeleteLoanMutation,
} = accountApi;
