package com.skillstorm.financialaccounts.models;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;

public class Loan {

    @Id
    private String id;

    @Transient
    public static final String SEQUENCE_NAME = "loans_sequence";

    private String bankName;

    private String loanType; // auto/mortgage/etc.

    private double balance;

    private double interestRate;

    private int termLength; // number of months

    private boolean paid;

    public Loan() {}

    public Loan(String bankName, String loanType, double balance, double interestRate, int termLength, boolean paid) {
        this.bankName = bankName;
        this.loanType = loanType;
        this.balance = balance;
        this.interestRate = interestRate;
        this.termLength = termLength;
        this.paid = paid;
    }

    public Loan(String id, String bankName, String loanType, double balance, double interestRate, int termLength,
            boolean paid) {
        this.id = id;
        this.bankName = bankName;
        this.loanType = loanType;
        this.balance = balance;
        this.interestRate = interestRate;
        this.termLength = termLength;
        this.paid = paid;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public static String getSequenceName() {
        return SEQUENCE_NAME;
    }

    public String getBankName() {
        return bankName;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName;
    }

    public String getLoanType() {
        return loanType;
    }

    public void setLoanType(String loanType) {
        this.loanType = loanType;
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

    public int getTermLength() {
        return termLength;
    }

    public void setTermLength(int termLength) {
        this.termLength = termLength;
    }

    public boolean isPaid() {
        return paid;
    }

    public void setPaid(boolean paid) {
        this.paid = paid;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        result = prime * result + ((bankName == null) ? 0 : bankName.hashCode());
        result = prime * result + ((loanType == null) ? 0 : loanType.hashCode());
        long temp;
        temp = Double.doubleToLongBits(balance);
        result = prime * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(interestRate);
        result = prime * result + (int) (temp ^ (temp >>> 32));
        result = prime * result + termLength;
        result = prime * result + (paid ? 1231 : 1237);
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
        Loan other = (Loan) obj;
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
        if (loanType == null) {
            if (other.loanType != null)
                return false;
        } else if (!loanType.equals(other.loanType))
            return false;
        if (Double.doubleToLongBits(balance) != Double.doubleToLongBits(other.balance))
            return false;
        if (Double.doubleToLongBits(interestRate) != Double.doubleToLongBits(other.interestRate))
            return false;
        if (termLength != other.termLength)
            return false;
        if (paid != other.paid)
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "Loan [id=" + id + ", bankName=" + bankName + ", loanType=" + loanType + ", balance=" + balance
                + ", interestRate=" + interestRate + ", termLength=" + termLength + ", paid=" + paid + "]";
    }
}
