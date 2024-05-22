import React, { useState, useEffect, useRef } from "react";
import { Button, Grid } from "@trussworks/react-uswds";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import {
  faCreditCard,
  faUniversity,
  faChartLine,
  faHandHoldingUsd,
  faPiggyBank,
  faEllipsisH,
  faBalanceScale,
  faDollarSign,
} from "@fortawesome/free-solid-svg-icons";
import AddModal from "../components/AddModal";
import EditModal from "../components/EditModal";
import PaymentModal from "../components/PaymentModal";
import { useTranslation } from "react-i18next";
import { useSelector } from "react-redux";
import { selectUserEmail } from "../app/features/userSlice";
import {
  Bank,
  CreditCard,
  Loan,
  useGetAccountsByEmailQuery,
  useCreateAccountsMutation,
  useUpdateAccountsMutation,
  useCreateBankAccountMutation,
  useCreateCreditCardMutation,
  useCreateLoanMutation,
  useUpdateBankAccountMutation,
  useUpdateCreditCardMutation,
  useUpdateLoanMutation,
  useDeleteBankAccountMutation,
  useDeleteCreditCardMutation,
  useDeleteLoanMutation,
  useGetAllBankAccountsQuery,
  useGetAllCreditCardsQuery,
  useGetAllLoansQuery,
} from "../app/api/accountApi";

const AccountPage: React.FC = () => {
  const buttonStyle: React.CSSProperties = {
    margin: "10px",
    height: "100px",
    width: "300px",
    display: "flex",
    flexDirection: "column",
    justifyContent: "center",
    alignItems: "center",
    backgroundColor: "white",
    borderRadius: "10px",
    boxShadow: "0px 4px 6px rgba(0, 0, 0, 0.2)",
    border: "2px white",
  };

  const creditCardsRef = useRef<HTMLDivElement>(null);
  const bankInfoRef = useRef<HTMLDivElement>(null);
  const loansRef = useRef<HTMLDivElement>(null);

  const { t } = useTranslation();
  const userEmail = useSelector(selectUserEmail);

  const {
    data: userAccounts,
    error: accountsError,
    isLoading: accountsLoading,
  } = useGetAccountsByEmailQuery(userEmail);
  const defaultAccount = {
    email: "",
    bankAccounts: [],
    creditCards: [],
    loans: [],
  };
  const accountData = userAccounts || defaultAccount;

  //console logs to check
  console.log("user email:", useGetAccountsByEmailQuery(userEmail));
  console.log("creditCards: ", userAccounts?.creditCards);
  console.log("banks: ", userAccounts?.bankAccounts);
  console.log("loans: ", userAccounts?.loans);

  const [createAccount] = useCreateAccountsMutation();
  const [updateAccount] = useUpdateAccountsMutation();

  const [createCreditCard] = useCreateCreditCardMutation();
  const [createBankAccount] = useCreateBankAccountMutation();
  const [createLoan] = useCreateLoanMutation();

  const [deleteCreditCard] = useDeleteCreditCardMutation();
  const [deleteBankAccount] = useDeleteBankAccountMutation();
  const [deleteLoan] = useDeleteLoanMutation();

  const [updateCreditCard] = useUpdateCreditCardMutation();
  const [updateBankAccount] = useUpdateBankAccountMutation();
  const [updateLoan] = useUpdateLoanMutation();

  const [creditCardEntries, setCreditCardEntries] = useState<CreditCard[]>(
    accountData?.creditCards
  );
  const [bankInfoEntries, setBankInfoEntries] = useState<Bank[]>(
    accountData?.bankAccounts
  );
  const [loanEntries, setLoanEntries] = useState<Loan[]>(accountData?.loans);

  const rowContainerStyle: React.CSSProperties = {
    display: "flex",
    justifyContent: "center",
  };

  const [activeCollection, setActiveCollection] = useState<string | null>(null);

  const handleButtonClick = (collection: string) => {
    setActiveCollection(activeCollection === collection ? null : collection);
    if (collection === "creditCards" && creditCardsRef.current) {
      creditCardsRef.current.scrollIntoView({ behavior: "smooth" });
    } else if (collection === "bankInfo" && bankInfoRef.current) {
      bankInfoRef.current.scrollIntoView({ behavior: "smooth" });
    } else if (collection === "loans" && loansRef.current) {
      loansRef.current.scrollIntoView({ behavior: "smooth" });
    }
  };

  const [isModalOpen, setIsModalOpen] = useState(false);
  const [modalFields, setModalFields] = useState<any[]>([]); // New state for modal fields
  const [modalType, setModalType] = useState(""); // New state for modal type
  const [paymentAmount, setPaymentAmount] = useState(0);
  const [isEditModalOpen, setIsEditModalOpen] = useState(false);

  const handleEditModalOpen = () => {
    setIsEditModalOpen(true);
  };

  const handleEditModalClose = () => {
    setIsEditModalOpen(false);
  };

  const handlePaymentSubmit = async (index: number) => {
    const updatedLoanEntries = [...loanEntries];
    const selectedLoan = updatedLoanEntries[index];

    const remainingBalance = selectedLoan.balance - paymentAmount;

    if (remainingBalance <= 0) {
      // Set balance to 0 and mark the loan as paid
      const updatedLoan = {
        ...selectedLoan,
        balance: 0,
        paid: true,
      };
      updatedLoanEntries[index] = updatedLoan;
    } else {
      const updatedLoan = {
        ...selectedLoan,
        balance: remainingBalance,
      };
      updatedLoanEntries[index] = updatedLoan;
    }

    // Update the loan entries
    setLoanEntries(updatedLoanEntries);

    // Construct an updated accountData object with the modified loans
    const updatedUserAccounts = {
      ...accountData,
      loans: updatedLoanEntries,
    };

    // Use the updateAccount mutation to update the loans
    await updateAccount({
      email: userEmail,
      creditCards: creditCardEntries,
      bankAccounts: bankInfoEntries,
      loans: updatedLoanEntries,
    });

    // Reset the payment amount
    setPaymentAmount(0);
  };

  const openModal = (type: string) => {
    setIsModalOpen(true);
    setModalType(type);

    if (type === "creditCards") {
      setModalFields([
        { name: "bankName", placeholder: t("Account.bank-name") },
        { name: "creditLimit", placeholder: t("Account.credit-limit") },
        { name: "balance", placeholder: t("Account.balance") },
        { name: "interestRate", placeholder: t("Account.interest") },
      ]);
    }
    if (type === "bankInfo") {
      setModalFields([
        { name: "bankName", placeholder: t("Account.bank-name") },
        { name: "accountType", placeholder: t("Account.acc-type") },
        { name: "balance", placeholder: t("Account.balance") },
      ]);
    }
    if (type === "loans") {
      setModalFields([
        { name: "bankName", placeholder: t("Account.bank-name") },
        { name: "loanType", placeholder: t("Account.loan-type") },
        { name: "balance", placeholder: t("Account.balance") },
        { name: "interestRate", placeholder: t("Account.interest") },
        { name: "termLength", placeholder: t("Account.term") },
      ]);
    }
  };

  const closeModal = () => {
    setIsModalOpen(false);
    setModalType("");
    setModalFields([]);
  };

  const saveNewItem = async (newItem: any) => {
    closeModal();

    if (activeCollection === "creditCards") {
      const newCreditCard: CreditCard = {
        ...newItem,
      };

      const updatedCreditCardEntries = [...creditCardEntries, newCreditCard];
      setCreditCardEntries(updatedCreditCardEntries);

      await createCreditCard({
        email: userEmail,
        creditCard: newCreditCard,
      });

      const updatedUserAccounts = {
        ...accountData,
        creditCards: updatedCreditCardEntries,
      };

      await updateAccount({
        email: userEmail,
        creditCards: updatedCreditCardEntries,
        bankAccounts: bankInfoEntries,
        loans: loanEntries,
      });
    }

    if (activeCollection === "bankInfo") {
      const newBankAccount: Bank = {
        ...newItem,
      };

      const updatedBankInfoEntries = [...bankInfoEntries, newBankAccount];
      setBankInfoEntries(updatedBankInfoEntries);
      await createBankAccount({
        email: userEmail,
        bankAccount: newBankAccount,
      });

      await createBankAccount({
        email: userEmail,
        bankAccount: newBankAccount,
      });

      const updatedUserAccounts = {
        ...accountData,
        banckAccounts: updatedBankInfoEntries,
      };

      await updateAccount({
        email: userEmail,
        creditCards: creditCardEntries,
        bankAccounts: updatedBankInfoEntries,
        loans: loanEntries,
      });
    }

    if (activeCollection === "loans") {
      const newLoan: Loan = {
        paid: false,
        ...newItem,
      };

      const updatedLoanEntries = [...loanEntries, newLoan];
      setLoanEntries(updatedLoanEntries);
      await createLoan({
        email: userEmail,
        loan: newLoan,
      });

      await createLoan({
        email: userEmail,
        loan: newLoan,
      });

      const updatedUserAccounts = {
        ...accountData,
        loans: updatedLoanEntries,
      };

      await updateAccount({
        email: userEmail,
        creditCards: creditCardEntries,
        bankAccounts: bankInfoEntries,
        loans: updatedLoanEntries,
      });
    }
  };

  const handleDeleteClick = async (collection: string, index: number) => {
    if (collection === "creditCards") {
      const cardToDelete = creditCardEntries[index];
      const updatedCreditCardEntries = creditCardEntries.filter(
        (_, i) => i !== index
      );
      setCreditCardEntries(updatedCreditCardEntries);

      const updatedUserAccounts = {
        ...accountData,
        creditCards: updatedCreditCardEntries,
      };

      await updateAccount({
        email: userEmail,
        creditCards: updatedCreditCardEntries,
        bankAccounts: bankInfoEntries,
        loans: loanEntries,
      });

      await deleteCreditCard({
        email: userEmail,
        creditCardID: cardToDelete.id,
      });
    }

    if (collection === "bankInfo") {
      const bankToDelete = bankInfoEntries[index];
      const updatedBankInfoEntries = bankInfoEntries.filter(
        (_, i) => i !== index
      );
      setBankInfoEntries(updatedBankInfoEntries);

      const updatedUserAccounts = {
        ...accountData,
        banckAccounts: updatedBankInfoEntries,
      };

      await updateAccount({
        email: userEmail,
        creditCards: creditCardEntries,
        bankAccounts: updatedBankInfoEntries,
        loans: loanEntries,
      });

      await deleteBankAccount({
        email: userEmail,
        bankAccountID: bankToDelete.id,
      });
    }

    if (collection === "loans") {
      const laonToDelete = loanEntries[index];
      const updatedLoanEntries = loanEntries.filter((_, i) => i !== index);
      setLoanEntries(updatedLoanEntries);

      const updatedUserAccounts = {
        ...accountData,
        loans: updatedLoanEntries,
      };

      await updateAccount({
        email: userEmail,
        creditCards: creditCardEntries,
        bankAccounts: bankInfoEntries,
        loans: updatedLoanEntries,
      });

      await deleteLoan({
        email: userEmail,
        loanID: laonToDelete.id,
      });
    }
  };

  const [editItem, setEditItem] = useState<any>(null);
  const handleEditClick = (collection: string, index: number) => {
    let itemToUpdate;

    if (collection === "creditCards") {
      itemToUpdate = creditCardEntries[index];
    } else if (collection === "bankInfo") {
      itemToUpdate = bankInfoEntries[index];
    } else if (collection === "loans") {
      itemToUpdate = loanEntries[index];
    }

    setEditItem(itemToUpdate);
    setIsEditModalOpen(true);
    setModalType(collection);
    if (collection === "creditCards") {
      setModalFields([
        { name: "bankName", placeholder: t("Account.bank-name") },
        { name: "creditLimit", placeholder: t("Account.credit-limit") },
        { name: "balance", placeholder: t("Account.balance") },
        { name: "interestRate", placeholder: t("Account.interest") },
      ]);
    }
    if (collection === "bankInfo") {
      setModalFields([
        { name: "bankName", placeholder: t("Account.bank-name") },
        { name: "accountType", placeholder: t("Account.acc-type") },
        { name: "balance", placeholder: t("Account.balance") },
      ]);
    }
    if (collection === "loans") {
      setModalFields([
        { name: "bankName", placeholder: t("Account.bank-name") },
        { name: "loanType", placeholder: t("Account.loan-type") },
        { name: "balance", placeholder: t("Account.balance") },
        { name: "interestRate", placeholder: t("Account.interest") },
        { name: "termLength", placeholder: t("Account.term") },
      ]);
    }
  };

  const handleEditModalUpdate = async (updatedData: any) => {
    if (modalType === "creditCards") {
      const updatedCreditCard = {
        ...editItem,
        ...updatedData,
      };
      // Use updateCreditCard mutation to update the credit card
      await updateCreditCard({
        email: userEmail,
        creditCardID: updatedCreditCard.id,
        updatedCreditCard,
      });

      // Update state and close modal
      const updatedCreditCardEntries = creditCardEntries.map((card) =>
        card.id === updatedCreditCard.id ? updatedCreditCard : card
      );
      setCreditCardEntries(updatedCreditCardEntries);
      setIsEditModalOpen(false);
    }

    if (modalType === "bankInfo") {
      const updatedBankAccount = {
        ...editItem,
        ...updatedData,
      };
      // Use updateCreditCard mutation to update the credit card
      await updateBankAccount({
        email: userEmail,
        bankAccountID: updatedBankAccount.id,
        updatedBankAccount,
      });

      // Update state and close modal
      const updatedBankInfoEntries = bankInfoEntries.map((bank) =>
        bank.id === updatedBankAccount.id ? updatedBankAccount : bank
      );
      setBankInfoEntries(updatedBankInfoEntries);
      setIsEditModalOpen(false);
    }

    if (modalType === "loans") {
      const updatedLoan = {
        ...editItem,
        ...updatedData,
      };
      // Use updateLoans mutation to update the credit card
      await updateLoan({
        email: userEmail,
        loanID: updatedLoan.id,
        updatedLoan,
      });

      // Update state and close modal
      const updatedLoansEntries = loanEntries.map((loan) =>
        loan.id === updatedLoan.id ? updatedLoan : loan
      );
      setLoanEntries(updatedLoansEntries);
      setIsEditModalOpen(false);
    }
  };

  useEffect(() => {
    if (accountData?.creditCards) {
      setCreditCardEntries(accountData.creditCards);
    }
  }, [accountData]);
  useEffect(() => {
    if (accountData?.bankAccounts) {
      setBankInfoEntries(accountData.bankAccounts);
    }
  }, [accountData]);
  useEffect(() => {
    if (accountData?.loans) {
      setLoanEntries(accountData.loans);
    }
  }, [accountData]);

  const totalBankAccountBalance = (bankInfoEntries || []).reduce(
    (sum, bankAccount) => sum + (bankAccount.balance ?? 0),
    0
  );

  //Calculate total balance for credit cards
  const totalCreditCardBalance = (creditCardEntries || []).reduce(
    (sum, creditCard) => sum + (creditCard.balance ?? 0),
    0
  );

  // Calculate total balance for loans
  const totalLoanBalance = (loanEntries || []).reduce(
    (sum, loan) => sum + (loan.balance ?? 0),
    0
  );
  // Calculate net worth
  const netWorth =
    totalBankAccountBalance - totalCreditCardBalance - totalLoanBalance;

  return (
    <div style={{ textAlign: "center", padding: "20px" }}>
      <h1 style={{ marginBottom: "20px" }}>{t("Account.welcome")}</h1>
      {/* Container for Account Balances */}
      <div
        style={{
          display: "flex",
          flexDirection: "column",
          alignItems: "center",
          marginTop: "20px",
        }}
      >
        <h2>{t("Account.acc-balances")}</h2>
        <div
          style={{
            display: "flex",
            flexWrap: "wrap",
            justifyContent: "center",
          }}
        >
          <div style={{ margin: "10px", minWidth: 200 }}>
            <FontAwesomeIcon icon={faDollarSign} size="2x" color="#04C585" />
            <p>
              {t("Account.bank-acc")}: ${totalBankAccountBalance}
            </p>
          </div>
          <div style={{ margin: "10px", minWidth: 200 }}>
            <FontAwesomeIcon icon={faCreditCard} size="2x" color="#04C585" />
            <p>
              {t("Account.cc")}: -${totalCreditCardBalance}
            </p>
          </div>
          <div style={{ margin: "10px", minWidth: 200 }}>
            <FontAwesomeIcon
              icon={faHandHoldingUsd}
              size="2x"
              color="#04C585"
            />
            <p>
              {t("Account.loan")}: -${totalLoanBalance}
            </p>
          </div>
          <div style={{ margin: "10px", minWidth: 200 }}>
            <FontAwesomeIcon icon={faBalanceScale} size="2x" color="#04C585" />
            <p style={{ marginLeft: "10px" }}>
              {t("Account.net")}: ${netWorth}
            </p>
          </div>
        </div>
      </div>

      {/* First Row */}
      <div ref={creditCardsRef}>
        <Grid row style={rowContainerStyle}>
          <button
            style={buttonStyle}
            type="button"
            onClick={() => handleButtonClick("creditCards")}
          >
            <FontAwesomeIcon icon={faCreditCard} size="2x" color="#04C585" />
            {t("Account.cc")}
          </button>

          <button
            style={buttonStyle}
            type="button"
            onClick={() => handleButtonClick("bankInfo")}
          >
            <FontAwesomeIcon icon={faUniversity} size="2x" color="#04C585" />
            {t("Account.bank-acc")}
          </button>
          <button
            style={buttonStyle}
            type="button"
            onClick={() => handleButtonClick("loans")}
          >
            <FontAwesomeIcon
              icon={faHandHoldingUsd}
              size="2x"
              color="#04C585"
            />
            {t("Account.loan")}
          </button>
        </Grid>
      </div>

      {/* Collections */}
      {activeCollection === "creditCards" && (
        <div
          style={{
            display: "flex",
            flexDirection: "column",
            alignItems: "center",
            marginTop: "20px",
            backgroundColor: "white",
            padding: "20px",
            borderRadius: "10px",
            boxShadow: "0px 4px 6px rgba(0, 0, 0, 0.2)",
            maxWidth: "1000px", 
            margin: "0 auto", 
          }}
        >
          {/* Credit Card Collection */}
          <h2 style={{ marginBottom: "20px" }}>{t("Account.your-cc")}</h2>
          <Button
            type="button"
            style={{ borderRadius: "10px" }}
            className="bg-mint margin-bottom-3"
            onClick={() => openModal("creditCards")}
          >
            {t("Account.add-cc")}
          </Button>
          {creditCardEntries.length > 0 && (
          <table
            style={{
              borderCollapse: "collapse",
              width: "100%",
              marginBottom: "20px",
            }}
          >
            <thead>
              <tr>
                <th>{t("Account.bank-name")}</th>
                <th>{t("Account.credit-limit")}</th>
                <th>{t("Account.balance")}</th>
                <th>{t("Account.interest")}</th>
              </tr>
            </thead>
            <tbody>
              {creditCardEntries.map((card, index) => (
                <tr
                  key={index}
                  style={{
                    backgroundColor: index % 2 === 0 ? "#c4eeeb" : "#e0f7f6",
                  }}
                >
                  <td>{card.bankName}</td>
                  <td>{card.creditLimit}</td>
                  <td>${card.balance}</td>
                  <td>{card.interestRate}%</td>
                  <td>
                    <Button
                      type="button"
                      className=" bg-mint open-modal-button"
                      style={{ borderRadius: "10px" }}
                      onClick={() => handleEditClick("creditCards", index)}
                    >
                      Edit
                    </Button>
                    <Button
                      type="button"
                      className="bg-mint open-modal-button"
                      onClick={() => handleDeleteClick("creditCards", index)}
                      style={{ borderRadius: "10px" }}
                    >
                      Delete
                    </Button>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
          )}
        </div>
      )}

      {activeCollection === "bankInfo" && (
        <div
          style={{
            display: "flex",
            flexDirection: "column",
            alignItems: "center",
            marginTop: "20px",
            backgroundColor: "white",
            padding: "20px",
            borderRadius: "10px",
            boxShadow: "0px 4px 6px rgba(0, 0, 0, 0.2)",
            maxWidth: "1000px", 
            margin: "0 auto",
          }}
        >
          {/* Bank Info Collection */}
          <h2 style={{ marginBottom: "20px" }}>{t("Account.your-bank")}</h2>
          <Button
            type="button"
            className="bg-mint margin-bottom-3"
            style={{ borderRadius: "10px" }}
            onClick={() => openModal("bankInfo")}
          >
            {t("Account.add-bank")}
          </Button>
          {bankInfoEntries.length > 0 && (
          <table
            style={{
              borderCollapse: "collapse",
              width: "100%",
              marginBottom: "20px",
            }}
          >
            <thead>
              <tr>
                <th>{t("Account.bank-name")}</th>
                <th>{t("Account.acc-type")}</th>
                <th>{t("Account.balance")}</th>
              </tr>
            </thead>
            <tbody>
              {bankInfoEntries.map((bank, index) => (
                <tr
                  key={index}
                  style={{
                    backgroundColor: index % 2 === 0 ? "#c4eeeb" : "#e0f7f6",
                  }}
                >
                  <td>{bank.bankName}</td>
                  <td>{bank.accountType}</td>
                  <td>${bank.balance}</td>
                  <td>
                    <Button
                      type="button"
                      className="bg-mint open-modal-button"
                      style={{ borderRadius: "10px" }}
                      onClick={() => handleEditClick("bankInfo", index)}
                    >
                      Edit
                    </Button>
                    <Button
                      type="button"
                      className="bg-mint open-modal-button"
                      style={{ borderRadius: "10px" }}
                      onClick={() => handleDeleteClick("bankInfo", index)}
                    >
                      Delete
                    </Button>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
          )}
        </div>
      )}

      {activeCollection === "loans" && (
        <div
          style={{
            display: "flex",
            flexDirection: "column",
            alignItems: "center",
            marginTop: "20px",
            backgroundColor: "white",
            padding: "20px",
            borderRadius: "10px",
            boxShadow: "0px 4px 6px rgba(0, 0, 0, 0.2)",
            maxWidth: "1000px", 
            margin: "0 auto",
          }}
        >
          {/* Loans Collection */}
          <h2 style={{ marginBottom: "20px" }}>{t("Account.your-loan")}</h2>
          <Button
            type="button"
            className="bg-mint margin-bottom-3"
            style={{ borderRadius: "10px" }}
            onClick={() => openModal("loans")}
          >
            {t("Account.add-loan")}
          </Button>
          {loanEntries.length > 0 && (
          <table
            style={{
              borderCollapse: "collapse",
              width: "80%",
              marginBottom: "20px",
            }}
          >
            <thead>
              <tr>
                <th>{t("Account.bank-name")}</th>
                <th>{t("Account.loan-type")}</th>
                <th>{t("Account.balance")}</th>
                <th>{t("Account.interest")}</th>
                <th>{t("Account.term")}</th>
              </tr>
            </thead>
            <tbody>
              {loanEntries.map((loan, index) => (
                <tr
                  key={index}
                  style={{
                    backgroundColor: index % 2 === 0 ? "#c4eeeb" : "#e0f7f6",
                  }}
                >
                  <td>{loan.bankName}</td>
                  <td>{loan.loanType}</td>
                  <td>${loan.balance}</td>
                  <td>{loan.interestRate}%</td>
                  <td>
                    {loan.termLength} {t("Account.month")}
                  </td>
                  <td>
                    {!loan.paid && (
                      <>
                        <PaymentModal
                          amount={paymentAmount}
                          onAmountChange={setPaymentAmount}
                          onSubmit={() => handlePaymentSubmit(index)}
                        />
                      </>
                    )}
                    {loan.paid && (
                      <>
                        <span style={{ color: "green" }}>
                          {t("Account.paid")}
                          &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
                        </span>
                      </>
                    )}
                    <Button
                      type="button"
                      className="bg-mint open-modal-button"
                      style={{ borderRadius: "10px" }}
                      onClick={() => handleEditClick("loans", index)}
                    >
                      Edit
                    </Button>
                    <Button
                      type="button"
                      className="bg-mint open-modal-button"
                      style={{ borderRadius: "10px" }}
                      onClick={() => handleDeleteClick("loans", index)}
                    >
                      Delete
                    </Button>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
          )}
        </div>
      )}

      {isModalOpen && (
        <AddModal
          onClose={closeModal}
          onSave={saveNewItem}
          fields={modalFields}
        />
      )}

      {isEditModalOpen && (
        <EditModal
          onClose={handleEditModalClose}
          onUpdate={handleEditModalUpdate}
          fields={modalFields}
          initialValues={editItem}
          collectionType={modalType}
        />
      )}
    </div>
  );
};

export default AccountPage;
function useMediaQuery(arg0: string, arg1: () => void) {
  throw new Error("Function not implemented.");
}
