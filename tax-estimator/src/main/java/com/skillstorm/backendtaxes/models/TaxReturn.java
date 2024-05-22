package com.skillstorm.backendtaxes.models;

import java.util.Arrays;

import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.MongoId;

@Document(collection = "Tax Returns")
public class TaxReturn {
    
    @MongoId
    private String id;
    private String email;
    private String filingStatus;
    private double totalIncome;
    private double totalStateOwed;
    private double totalFedOwed;
    private double taxableIncome;
    private double agi;
    private int childDependents;
    private int otherDependents;
    private int aotcClaims;
    private double[] educationalExpenditures;
    private IncomeSource[] incomeSources;
    private Deduction[] belowLineDeductions;
    private Deduction[] aboveLineDeductions;
    private Credit[] credits;

    // Not including total income, total StateOwed, totalFedOwed, agi or taxableIncome because it is calculated in the service
    public TaxReturn(String email, String filingStatus, int childDependents, int otherDependents, int aotcClaims,
            double[] educationalExpenditures, IncomeSource[] incomeSources, Deduction[] belowLineDeductions, Deduction[] aboveLineDeductions) {
        this.email = email;
        this.filingStatus = filingStatus;
        this.childDependents = childDependents;
        this.otherDependents = otherDependents;
        this.aotcClaims = aotcClaims;
        this.educationalExpenditures = educationalExpenditures;
        this.incomeSources = incomeSources;
        this.belowLineDeductions = belowLineDeductions;
        this.aboveLineDeductions = aboveLineDeductions;
    }
    
    public TaxReturn(String id, String email, String filingStatus, int childDependents, int otherDependents,
            int aotcClaims, double[] educationalExpenditures, IncomeSource[] incomeSources, Deduction[] belowLineDeductions, Deduction[] aboveLineDeductions,
            Credit[] credits, double agi) {
        this.id = id;
        this.email = email;
        this.filingStatus = filingStatus;
        this.childDependents = childDependents;
        this.otherDependents = otherDependents;
        this.aotcClaims = aotcClaims;
        this.educationalExpenditures = educationalExpenditures;
        this.incomeSources = incomeSources;
        this.belowLineDeductions = belowLineDeductions;
        this.aboveLineDeductions = aboveLineDeductions;
        this.credits = credits;
        this.agi = agi;
    }

    public TaxReturn(String id, String email, String filingStatus, int childDependents, int otherDependents,
            int aotcClaims, IncomeSource[] incomeSources, Deduction[] belowLineDeductions, Deduction[] aboveLineDeductions,
            Credit[] credits) {
        this.id = id;
        this.email = email;
        this.filingStatus = filingStatus;
        this.childDependents = childDependents;
        this.otherDependents = otherDependents;
        this.aotcClaims = aotcClaims;
        this.incomeSources = incomeSources;
        this.belowLineDeductions = belowLineDeductions;
        this.aboveLineDeductions = aboveLineDeductions;
        this.credits = credits;
    }

    public TaxReturn() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFilingStatus() {
        return filingStatus;
    }

    public void setFilingStatus(String filingStatus) {
        this.filingStatus = filingStatus;
    }

    public double getTotalIncome() {
        return totalIncome;
    }

    public void setTotalIncome(double totalIncome) {
        this.totalIncome = totalIncome;
    }

    public double getTotalStateOwed() {
        return totalStateOwed;
    }

    public void setTotalStateOwed(double totalStateOwed) {
        this.totalStateOwed = totalStateOwed;
    }

    public double getTotalFedOwed() {
        return totalFedOwed;
    }

    public void setTotalFedOwed(double totalFedOwed) {
        this.totalFedOwed = totalFedOwed;
    }

    public int getChildDependents() {
        return childDependents;
    }

    public void setChildDependents(int childDependents) {
        this.childDependents = childDependents;
    }

    public int getOtherDependents() {
        return otherDependents;
    }

    public void setOtherDependents(int otherDependents) {
        this.otherDependents = otherDependents;
    }

    public double getTaxableIncome() {
        return taxableIncome;
    }

    public void setTaxableIncome(double taxableIncome) {
        this.taxableIncome = taxableIncome;
    }
    
    public int getAotcClaims() {
        return aotcClaims;
    }

    public void setAotcClaims(int aotcClaims) {
        this.aotcClaims = aotcClaims;
    }
    
    public double[] getEducationalExpenditures() {
        return educationalExpenditures;
    }

    public void setEducationalExpenditures(double[] educationalExpenditures) {
        this.educationalExpenditures = educationalExpenditures;
    }

    public IncomeSource[] getIncomeSources() {
        return incomeSources;
    }

    public void setIncomeSources(IncomeSource[] incomeSources) {
        this.incomeSources = incomeSources;
    }

    public double getAgi() {
        return agi;
    }

    public void setAgi(double agi) {
        this.agi = agi;
    }

    public Deduction[] getBelowLineDeductions() {
        return belowLineDeductions;
    }

    public void setBelowLineDeductions(Deduction[] belowLineDeductions) {
        this.belowLineDeductions = belowLineDeductions;
    }

    public Deduction[] getAboveLineDeductions() {
        return aboveLineDeductions;
    }

    public void setAboveLineDeductions(Deduction[] aboveLineDeductions) {
        this.aboveLineDeductions = aboveLineDeductions;
    }

    public Credit[] getCredits() {
        return credits;
    }

    public void setCredits(Credit[] credits) {
        this.credits = credits;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        result = prime * result + ((email == null) ? 0 : email.hashCode());
        result = prime * result + ((filingStatus == null) ? 0 : filingStatus.hashCode());
        long temp;
        temp = Double.doubleToLongBits(totalIncome);
        result = prime * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(totalStateOwed);
        result = prime * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(totalFedOwed);
        result = prime * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(taxableIncome);
        result = prime * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(agi);
        result = prime * result + (int) (temp ^ (temp >>> 32));
        result = prime * result + childDependents;
        result = prime * result + otherDependents;
        result = prime * result + aotcClaims;
        result = prime * result + Arrays.hashCode(educationalExpenditures);
        result = prime * result + Arrays.hashCode(incomeSources);
        result = prime * result + Arrays.hashCode(belowLineDeductions);
        result = prime * result + Arrays.hashCode(aboveLineDeductions);
        result = prime * result + Arrays.hashCode(credits);
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
        TaxReturn other = (TaxReturn) obj;
        if (id == null) {
            if (other.id != null)
                return false;
        } else if (!id.equals(other.id))
            return false;
        if (email == null) {
            if (other.email != null)
                return false;
        } else if (!email.equals(other.email))
            return false;
        if (filingStatus == null) {
            if (other.filingStatus != null)
                return false;
        } else if (!filingStatus.equals(other.filingStatus))
            return false;
        if (Double.doubleToLongBits(totalIncome) != Double.doubleToLongBits(other.totalIncome))
            return false;
        if (Double.doubleToLongBits(totalStateOwed) != Double.doubleToLongBits(other.totalStateOwed))
            return false;
        if (Double.doubleToLongBits(totalFedOwed) != Double.doubleToLongBits(other.totalFedOwed))
            return false;
        if (Double.doubleToLongBits(taxableIncome) != Double.doubleToLongBits(other.taxableIncome))
            return false;
        if (Double.doubleToLongBits(agi) != Double.doubleToLongBits(other.agi))
            return false;
        if (childDependents != other.childDependents)
            return false;
        if (otherDependents != other.otherDependents)
            return false;
        if (aotcClaims != other.aotcClaims)
            return false;
        if (!Arrays.equals(educationalExpenditures, other.educationalExpenditures))
            return false;
        if (!Arrays.equals(incomeSources, other.incomeSources))
            return false;
        if (!Arrays.equals(belowLineDeductions, other.belowLineDeductions))
            return false;
        if (!Arrays.equals(aboveLineDeductions, other.aboveLineDeductions))
            return false;
        if (!Arrays.equals(credits, other.credits))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "TaxReturn [id=" + id + ", email=" + email + ", filingStatus=" + filingStatus + ", totalIncome="
                + totalIncome + ", totalStateOwed=" + totalStateOwed + ", totalFedOwed=" + totalFedOwed
                + ", taxableIncome=" + taxableIncome + ", agi=" + agi + ", childDependents=" + childDependents
                + ", otherDependents=" + otherDependents + ", aotcClaims=" + aotcClaims + ", educationalExpenditures="
                + Arrays.toString(educationalExpenditures) + ", incomeSources=" + Arrays.toString(incomeSources)
                + ", belowLineDeductions=" + Arrays.toString(belowLineDeductions) + ", aboveLineDeductions="
                + Arrays.toString(aboveLineDeductions) + ", credits=" + Arrays.toString(credits) + "]";
    }

}