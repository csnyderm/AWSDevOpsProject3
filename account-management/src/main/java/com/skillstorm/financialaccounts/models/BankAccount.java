package com.skillstorm.financialaccounts.models;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;

public class BankAccount {
    
    @Id
    private String id;

    @Transient
    public static final String SEQUENCE_NAME = "bank_account_sequence";

    private String bankName;

    private String accountType; // checking/savings

    private double balance;

    public BankAccount() {}

    public BankAccount(String bankName, String accountType, double balance) {
        this.bankName = bankName;
        this.accountType = accountType;
        this.balance = balance;
    }

    public BankAccount(String id, String bankName, String accountType, double balance) {
        this.id = id;
        this.bankName = bankName;
        this.accountType = accountType;
        this.balance = balance;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getBankName() {
        return bankName;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName;
    }

    public String getAccountType() {
        return accountType;
    }

    public void setAccountType(String accountType) {
        this.accountType = accountType;
    }

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        result = prime * result + ((bankName == null) ? 0 : bankName.hashCode());
        result = prime * result + ((accountType == null) ? 0 : accountType.hashCode());
        long temp;
        temp = Double.doubleToLongBits(balance);
        result = prime * result + (int) (temp ^ (temp >>> 32));
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
        BankAccount other = (BankAccount) obj;
        if (id == null) {
            if (other.id != null)
                return false;
        } else if (!id.equals(other.id))
            return false;
        if (bankName == null) {
            if (other.bankName != null)
                return false;
        } else if (!bankName.equals(other.bankName))
            return false;
        if (accountType == null) {
            if (other.accountType != null)
                return false;
        } else if (!accountType.equals(other.accountType))
            return false;
        if (Double.doubleToLongBits(balance) != Double.doubleToLongBits(other.balance))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "BankAccount [id=" + id + ", bankName=" + bankName + ", accountType=" + accountType + ", balance="
                + balance + "]";
    }
}
