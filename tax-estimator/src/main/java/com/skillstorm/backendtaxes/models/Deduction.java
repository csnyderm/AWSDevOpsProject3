package com.skillstorm.backendtaxes.models;


public class Deduction {
    
    private String deductionType;
    private double deductionAmount;

    public Deduction() {
    }

    public Deduction(String deductionType, double deductionAmount) {
        this.deductionType = deductionType;
        this.deductionAmount = deductionAmount;
    }

    public String getDeductionType() {
        return deductionType;
    }

    public void setDeductionType(String deductionType) {
        this.deductionType = deductionType;
    }

    public double getDeductionAmount() {
        return deductionAmount;
    }

    public void setDeductionAmount(double deductionAmount) {
        this.deductionAmount = deductionAmount;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((deductionType == null) ? 0 : deductionType.hashCode());
        long temp;
        temp = Double.doubleToLongBits(deductionAmount);
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
        Deduction other = (Deduction) obj;
        if (deductionType == null) {
            if (other.deductionType != null)
                return false;
        } else if (!deductionType.equals(other.deductionType))
            return false;
        if (Double.doubleToLongBits(deductionAmount) != Double.doubleToLongBits(other.deductionAmount))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "Deduction [deductionType=" + deductionType + ", deductionAmount=" + deductionAmount + "]";
    }

    

    
}
