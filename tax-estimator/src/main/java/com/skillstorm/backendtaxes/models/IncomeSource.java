package com.skillstorm.backendtaxes.models;


public class IncomeSource {

    private int empID;
    private String name;
    private String incomeType;
    private String state;
    private double income;
    private double fedWithheld;
    private double stateWithheld;

    public IncomeSource() {
    }

    public IncomeSource(int empID, String name, String incomeType, String state, double income, double fedWithheld,
            double stateWithheld) {
        this.empID = empID;
        this.name = name;
        this.incomeType = incomeType;
        this.state = state;
        this.income = income;
        this.fedWithheld = fedWithheld;
        this.stateWithheld = stateWithheld;
    }

    public int getEmpID() {
        return empID;
    }

    public void setEmpID(int empID) {
        this.empID = empID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIncomeType() {
        return incomeType;
    }

    public void setIncomeType(String incomeType) {
        this.incomeType = incomeType;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public double getIncome() {
        return income;
    }

    public void setIncome(double income) {
        this.income = income;
    }

    public double getFedWithheld() {
        return fedWithheld;
    }

    public void setFedWithheld(double fedWithheld) {
        this.fedWithheld = fedWithheld;
    }

    public double getStateWithheld() {
        return stateWithheld;
    }

    public void setStateWithheld(double stateWithheld) {
        this.stateWithheld = stateWithheld;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + empID;
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        result = prime * result + ((incomeType == null) ? 0 : incomeType.hashCode());
        result = prime * result + ((state == null) ? 0 : state.hashCode());
        long temp;
        temp = Double.doubleToLongBits(income);
        result = prime * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(fedWithheld);
        result = prime * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(stateWithheld);
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
        IncomeSource other = (IncomeSource) obj;
        if (empID != other.empID)
            return false;
        if (name == null) {
            if (other.name != null)
                return false;
        } else if (!name.equals(other.name))
            return false;
        if (incomeType == null) {
            if (other.incomeType != null)
                return false;
        } else if (!incomeType.equals(other.incomeType))
            return false;
        if (state == null) {
            if (other.state != null)
                return false;
        } else if (!state.equals(other.state))
            return false;
        if (Double.doubleToLongBits(income) != Double.doubleToLongBits(other.income))
            return false;
        if (Double.doubleToLongBits(fedWithheld) != Double.doubleToLongBits(other.fedWithheld))
            return false;
        if (Double.doubleToLongBits(stateWithheld) != Double.doubleToLongBits(other.stateWithheld))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "IncomeSource [empID=" + empID + ", name=" + name + ", incomeType=" + incomeType + ", state=" + state
                + ", income=" + income + ", fedWithheld=" + fedWithheld + ", stateWithheld=" + stateWithheld + "]";
    }

}
