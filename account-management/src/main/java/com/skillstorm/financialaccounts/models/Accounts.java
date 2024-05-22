package com.skillstorm.financialaccounts.models;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "accounts")
public class Accounts {

    @Id
    private String email;

    private List<BankAccount> bankAccounts = new ArrayList<>();

    private List<CreditCard> creditCards = new ArrayList<>();

    private List<Loan> loans = new ArrayList<>();

    public Accounts() {}

    public Accounts(String email, List<BankAccount> bankAccounts, List<CreditCard> creditCards, List<Loan> loans) {
        this.email = email;
        this.bankAccounts = bankAccounts;
        this.creditCards = creditCards;
        this.loans = loans;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public List<BankAccount> getBankAccounts() {
        return bankAccounts;
    }

    public void setBankAccounts(List<BankAccount> bankAccounts) {
        this.bankAccounts = bankAccounts;
    }

    public List<CreditCard> getCreditCards() {
        return creditCards;
    }

    public void setCreditCards(List<CreditCard> creditCards) {
        this.creditCards = creditCards;
    }

    public List<Loan> getLoans() {
        return loans;
    }

    public void setLoans(List<Loan> loans) {
        this.loans = loans;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((email == null) ? 0 : email.hashCode());
        result = prime * result + ((bankAccounts == null) ? 0 : bankAccounts.hashCode());
        result = prime * result + ((creditCards == null) ? 0 : creditCards.hashCode());
        result = prime * result + ((loans == null) ? 0 : loans.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Accounts other = (Accounts) obj;
        if (email == null) {
            if (other.email != null)
                return false;
        } else if (!email.equals(other.email))
            return false;
        if (bankAccounts == null) {
            if (other.bankAccounts != null)
                return false;
        } else if (!bankAccounts.equals(other.bankAccounts))
            return false;
        if (creditCards == null) {
            if (other.creditCards != null)
                return false;
        } else if (!creditCards.equals(other.creditCards))
            return false;
        if (loans == null) {
            if (other.loans != null)
                return false;
        } else if (!loans.equals(other.loans))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "Accounts [email=" + email + ", bankAccounts=" + bankAccounts + ", creditCards=" + creditCards
                + ", loans=" + loans + "]";
    }
}
