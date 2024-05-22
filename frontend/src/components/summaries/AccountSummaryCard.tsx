import {
  Button,
  Card,
  CardBody,
  CardHeader,
  Table,
} from "@trussworks/react-uswds";
import React, { useState } from "react";
import { useGetAccountsByEmailQuery } from "../../app/api/accountApi";
import { useSelector } from "react-redux";
import { useAccountData } from "../hooks/useAccountData";
import { Link } from "react-router-dom";
import { useTranslation } from "react-i18next";

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

export default function AccountSummaryCard() {
  const defaultAccount = {
    email: "",
    bankAccounts: [],
    creditCards: [],
    loans: [],
  };

  const userEmail = useSelector((state: any) => state.user.email);
  const { data, error, isLoading } = useGetAccountsByEmailQuery(userEmail);
  const accountData = data || defaultAccount;
  const { expandedRows, toggleRow, totalNetWorth } =
    useAccountData(accountData);
  const { t } = useTranslation();

  if (!isLoading) {
    if (data != null) {
      if (
        accountData.bankAccounts.length != 0 ||
        accountData.creditCards.length != 0 ||
        accountData.loans.length != 0
      ) {
        return (
          <Card
            headerFirst
            //gridLayout={{ tablet: { col: 6}, desktop: { col: 2} }}
            className="goalSummary"
            containerProps={{
              className: "text-center margin-top-2",
            }}
          >
            <CardHeader className="bg-lightest text-left">
              <h2>{t("sideNav.account")}</h2>
              <h3>
                {" "}
                <div>{t("dashboard.networth")}</div>
                <span className="text-mint">$ {totalNetWorth}</span>
              </h3>
            </CardHeader>
            <CardBody className="padding-top-3 ">
              {/**mapping function to populate all users accounts */}
              <Table bordered={false}>
                <tbody>
                  {/* Bank Accounts */}
                  <tr
                    style={{ cursor: "pointer" }}
                    onClick={() => toggleRow(0)} // Toggle bank account category
                  >
                    <td style={{ width: "100%", fontWeight: "bold" }}>
                      Checkings & Savings
                    </td>
                    <td
                      style={{ fontWeight: "bold" }}
                      className="text-right text-mint"
                    >
                      $
                      {data.bankAccounts.reduce(
                        (acc, bankAccount) => acc + bankAccount.balance,
                        0
                      )}
                    </td>
                  </tr>
                  {/* Render bank account details here */}
                  {expandedRows.includes(0) && (
                    <tr>
                      <td colSpan={2}>
                        <ul style={{ listStyle: "none", paddingLeft: 0 }}>
                          {data.bankAccounts.map((bankAccount, index) => (
                            <li key={index} style={{ marginLeft: 10 }}>
                              {bankAccount.bankName}:
                              <span
                                style={{ fontWeight: "bold" }}
                                className="text-mint"
                              >
                                {" "}
                                $ {bankAccount.balance}
                              </span>
                            </li>
                          ))}
                        </ul>
                      </td>
                    </tr>
                  )}
                  {/* Credit Cards */}
                  <tr
                    style={{ cursor: "pointer" }}
                    onClick={() => toggleRow(1)} // Toggle credit card category
                  >
                    <td style={{ width: "100%", fontWeight: "bold" }}>
                      Credit Cards
                    </td>
                    <td
                      style={{ fontWeight: "bold" }}
                      className="text-right text-mint"
                    >
                      $
                      {data.creditCards.reduce(
                        (acc, creditCard) => acc + creditCard.balance,
                        0
                      )}
                    </td>
                  </tr>
                  {/* Render credit card details here */}
                  {expandedRows.includes(1) && (
                    <tr>
                      <td colSpan={2}>
                        <ul style={{ listStyle: "none", paddingLeft: 0 }}>
                          {data.creditCards.map((creditCard, index) => (
                            <li key={index} style={{ marginLeft: 10 }}>
                              {creditCard.bankName}:{" "}
                              <span
                                style={{ fontWeight: "bold" }}
                                className="text-mint"
                              >
                                $ {creditCard.balance}
                              </span>
                            </li>
                          ))}
                        </ul>
                      </td>
                    </tr>
                  )}

                  {/* Loans */}
                  <tr
                    style={{ cursor: "pointer" }}
                    onClick={() => toggleRow(2)} // Toggle loan category
                  >
                    <td style={{ width: "100%", fontWeight: "bold" }}>Loans</td>
                    <td
                      style={{ fontWeight: "bold" }}
                      className="text-right text-mint"
                    >
                      ${data.loans.reduce((acc, loan) => acc + loan.balance, 0)}
                    </td>
                  </tr>
                  {/* Render loan details here */}
                  {expandedRows.includes(2) && (
                    <tr>
                      <td colSpan={2}>
                        <ul style={{ listStyle: "none", paddingLeft: 0 }}>
                          {data.loans.map((loan, index) => (
                            <li key={index} style={{ marginLeft: 10 }}>
                              {loan.bankName}:{" "}
                              <span
                                style={{ fontWeight: "bold" }}
                                className="text-mint"
                              >
                                $ {loan.balance}
                              </span>
                            </li>
                          ))}
                        </ul>
                      </td>
                    </tr>
                  )}
                  {/* Render loan details here */}
                </tbody>
              </Table>
            </CardBody>
            {/* <CardFooter>
        if needed footer goes here
      </CardFooter> */}
          </Card>
        );
      } else {
        return (
          <Card
            headerFirst
            className="goalSummary"
            //gridLayout={{ tablet: { col: 6}, desktop: { col: 2} }}
            containerProps={{
              className: "text-center margin-top-2",
            }}
          >
            <CardHeader className="bg-lightest text-left">
              <h2>{t("sideNav.account")}</h2>
            </CardHeader>
            <CardBody className="padding-top-3">
              <Link style={{ textDecoration: "none" }} to={"/account"}>
                <Button
                  type={"button"}
                  className="bg-mint"
                  style={{
                    padding: 15,
                    borderRadius: "10px",
                    display: "flex",
                    margin: "auto",
                  }}
                >
                  {t("taxStarter.getStarted")}
                </Button>
              </Link>
            </CardBody>
            {/* <CardFooter>
    if needed footer goes here
  </CardFooter> */}
          </Card>
        );
      }
    }
  }
}
