package com.skillstorm.financialaccounts.models;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;

public class CreditCard {

    @Id
    private String id;

    @Transient
    public static final String SEQUENCE_NAME = "credit_card_sequence";
    
    private String bankName;

    private double creditLimit;

    private double balance;

    private double interestRate;

    public CreditCard() {}

    public CreditCard(String bankName, double creditLimit, double balance, double interestRate) {
        this.bankName = bankName;
        this.creditLimit = creditLimit;
        this.balance = balance;
        this.interestRate = interestRate;
    }

    public CreditCard(String id, String bankName, double creditLimit, double balance, double interestRate) {
        this.id = id;
        this.bankName = bankName;
        this.creditLimit = creditLimit;
        this.balance = balance;
        this.interestRate = interestRate;
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

    public double getCreditLimit() {
        return creditLimit;
    }

    public void setCreditLimit(double creditLimit) {
        this.creditLimit = creditLimit;
    }

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

    public double getInterestRate() {
        return interestRate;
    }

    public void setInterestRate(double interestRate) {
        this.interestRate = interestRate;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        result = prime * result + ((bankName == null) ? 0 : bankName.hashCode());
        long temp;
        temp = Double.doubleToLongBits(creditLimit);
        result = prime * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(balance);
        result = prime * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(interestRate);
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
        CreditCard other = (CreditCard) obj;
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
        if (Double.doubleToLongBits(creditLimit) != Double.doubleToLongBits(other.creditLimit))
            return false;
        if (Double.doubleToLongBits(balance) != Double.doubleToLongBits(other.balance))
            return false;
        if (Double.doubleToLongBits(interestRate) != Double.doubleToLongBits(other.interestRate))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "CreditCard [id=" + id + ", bankName=" + bankName + ", creditLimit=" + creditLimit + ", balance="
                + balance + ", interestRate=" + interestRate + "]";
    }
}
