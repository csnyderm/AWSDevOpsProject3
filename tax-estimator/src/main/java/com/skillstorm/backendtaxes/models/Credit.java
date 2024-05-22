package com.skillstorm.backendtaxes.models;


public class Credit {
    private String creditType;
    private double creditAmount;
    
    public Credit() {
    }

    public Credit(String creditType, double creditAmount) {
        this.creditType = creditType;
        this.creditAmount = creditAmount;
    }

    public String getCreditType() {
        return creditType;
    }

    public void setCreditType(String creditType) {
        this.creditType = creditType;
    }

    public double getCreditAmount() {
        return creditAmount;
    }

    public void setCreditAmount(double creditAmount) {
        this.creditAmount = creditAmount;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((creditType == null) ? 0 : creditType.hashCode());
        long temp;
        temp = Double.doubleToLongBits(creditAmount);
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
        Credit other = (Credit) obj;
        if (creditType == null) {
            if (other.creditType != null)
                return false;
        } else if (!creditType.equals(other.creditType))
            return false;
        if (Double.doubleToLongBits(creditAmount) != Double.doubleToLongBits(other.creditAmount))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "Credit [creditType=" + creditType + ", creditAmount=" + creditAmount + "]";
    }

    
}
