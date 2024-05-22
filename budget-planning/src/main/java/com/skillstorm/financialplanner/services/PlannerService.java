package com.skillstorm.financialplanner.services;

import com.skillstorm.financialplanner.models.Expense;
import com.skillstorm.financialplanner.models.Planner;
import com.skillstorm.financialplanner.repositories.PlannerRepository;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PlannerService {
    
    @Autowired
    PlannerRepository plannerRepository;

    // returns all planners
    public List<Planner> getAllPlanners() {
        return plannerRepository.findAll();
    }

    // returns the planner associated with the given email
    public Planner getPlannerByEmail(String email) {
        Optional<Planner> optionalPlanner = plannerRepository.findByEmail(email);
        return optionalPlanner.orElseThrow(() -> new RuntimeException("Planner not found"));
    }

    // Add planner
    public Planner addPlanner(Planner planner) {

        // checks all expenses to make sure that the expense category is already in the planner categories
        for(Expense expense : planner.getExpenses()) {
            if(!planner.getCategories().contains(expense.getCategory())){
                throw new IllegalArgumentException("The " + expense.getExpenseName() + " expense does not have a valid category");
            }
        }

        return plannerRepository.save(planner);
    }

   // Update planner 
   public Planner updatePlanner(Planner updatedPlanner) {
    Planner planner = plannerRepository.findByEmail(updatedPlanner.getEmail()).orElseThrow(() -> new RuntimeException("Planner not found."));    
    planner.setCategories(updatedPlanner.getCategories());

    // checks all expenses to make sure that the expense category is already in the planner categories
    for(Expense expense : updatedPlanner.getExpenses()) {
        if(!updatedPlanner.getCategories().contains(expense.getCategory())) {
            throw new IllegalArgumentException("The " + expense.getExpenseName() + " expense does not have a valid category");
        }
    }
    
    planner.setExpenses(updatedPlanner.getExpenses());
    planner.setMonthlyIncome(updatedPlanner.getMonthlyIncome());

    return plannerRepository.save(planner);    
   }

   // Delete a planner
   public void deletePlanner(Planner planner) {
    plannerRepository.delete(planner);
   }

}
