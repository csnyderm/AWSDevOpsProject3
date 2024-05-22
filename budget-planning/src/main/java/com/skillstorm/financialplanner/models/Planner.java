package com.skillstorm.financialplanner.models;

import java.util.List;

import javax.validation.constraints.Email;
import javax.validation.constraints.PositiveOrZero;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
public class Planner {

    @Id
    @Email(message = "Must be a valid email address")
    private String email;

    private List<String> categories;
    private List<Expense> expenses;

    @PositiveOrZero(message = "Monthly income must be a non-negative number")
    private double monthlyIncome;
    
    public Planner() {
    }

    public Planner(String email, List<String> categories, List<Expense> expenses, double monthlyIncome) {
        this.email = email;
        this.categories = categories;
        this.expenses = expenses;
        this.monthlyIncome = monthlyIncome;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public List<String> getCategories() {
        return categories;
    }

    public void setCategories(List<String> categories) {
        this.categories = categories;
    }

    public List<Expense> getExpenses() {
        return expenses;
    }

    public void setExpenses(List<Expense> expenses) {
        this.expenses = expenses;
    }

    public double getMonthlyIncome() {
        return monthlyIncome;
    }

    public void setMonthlyIncome(double monthlyIncome) {
        this.monthlyIncome = monthlyIncome;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((email == null) ? 0 : email.hashCode());
        result = prime * result + ((categories == null) ? 0 : categories.hashCode());
        result = prime * result + ((expenses == null) ? 0 : expenses.hashCode());
        long temp;
        temp = Double.doubleToLongBits(monthlyIncome);
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
        Planner other = (Planner) obj;
        if (email == null) {
            if (other.email != null)
                return false;
        } else if (!email.equals(other.email))
            return false;
        if (categories == null) {
            if (other.categories != null)
                return false;
        } else if (!categories.equals(other.categories))
            return false;
        if (expenses == null) {
            if (other.expenses != null)
                return false;
        } else if (!expenses.equals(other.expenses))
            return false;
        if (Double.doubleToLongBits(monthlyIncome) != Double.doubleToLongBits(other.monthlyIncome))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "Planner [email=" + email + ", categories=" + categories + ", expenses=" + expenses + ", monthlyIncome="
                + monthlyIncome + "]";
    }
    
}
