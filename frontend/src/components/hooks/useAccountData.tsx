// Custom Hook: useAccountData
import { useState } from "react";

interface Bank {
  bankName: string;
  accountType: string;
  balance: number;
}

interface CreditCard {
  bankName: string;
  creditLimit: number;
  balance: number;
  interestRate: number;
}

interface Loan {
  bankName: string;
  loanType: string;
  balance: number;
  interestRate: number;
  termLength: number;
  paid: boolean;
}

interface Account {
  email: string;
  bankAccounts: Bank[];
  creditCards: CreditCard[];
  loans: Loan[];
}

function calculateTotalNetWorth(accountData: Account) {
  let totalNetWorth = 0;

  // Calculate the total balance of bank accounts
  const bankAccountsBalance = accountData.bankAccounts.reduce(
    (acc, account) => acc + account.balance,
    0
  );

  // Calculate the total balance of credit cards
  const creditCardsBalance = accountData.creditCards.reduce(
    (acc, card) => acc + card.balance,
    0
  );

  // Calculate the total balance of loans
  const loansBalance = accountData.loans.reduce(
    (acc, loan) => acc + loan.balance,
    0
  );

  // Sum up the total net worth
  totalNetWorth = bankAccountsBalance + creditCardsBalance + loansBalance;

  return totalNetWorth;
}

export function useAccountData(initialData: Account) {
  const [expandedRows, setExpandedRows] = useState<number[]>([]); // Provide a type annotation here
  const totalNetWorth = calculateTotalNetWorth(initialData);

  const toggleRow = (index: number) => {
    // Add a type annotation for 'index'
    const currentIndex = expandedRows.indexOf(index);
    if (currentIndex === -1) {
      setExpandedRows([...expandedRows, index]);
    } else {
      setExpandedRows(expandedRows.filter((i) => i !== index));
    }
  };

  return { expandedRows, toggleRow, totalNetWorth };
}
