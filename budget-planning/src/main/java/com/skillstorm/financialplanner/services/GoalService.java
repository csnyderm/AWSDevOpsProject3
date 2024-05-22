package com.skillstorm.financialplanner.services;


import com.skillstorm.financialplanner.models.Goal;
import com.skillstorm.financialplanner.repositories.GoalRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class GoalService {

    @Autowired
    GoalRepository goalRepository;

    // returns all goals
    public List<Goal> getAllGoals() {
        return goalRepository.findAll();
    }

    // returns the goal associated with the given id
    public Goal getGoalById(String id) {
        Optional<Goal> optionalGoal = goalRepository.findById(id);
        return optionalGoal.orElseThrow(() -> new RuntimeException("Goal not found"));
    }

    // returns the goals associated with the given email
    public List<Goal> getGoalsByEmail(String email) {
        Optional<List<Goal>> optionalGoal = goalRepository.findAllByEmail(email);
        return optionalGoal.orElseThrow(() -> new RuntimeException("Goal not found"));
    }

    // Add goal
    public Goal addGoal(Goal goal) {
        System.out.println(goal.toString());
        return goalRepository.save(goal);
    }

    // Update goal
    public Goal updateGoal(Goal updatedGoal) {
        Goal goal = goalRepository.findById(updatedGoal.get_id()).orElseThrow(() -> new RuntimeException("Goal not found."));
        goal.setName(updatedGoal.getName());
        goal.setGoalAmount(updatedGoal.getGoalAmount());
        goal.setAmountSaved(updatedGoal.getAmountSaved());

        return goalRepository.save(goal);
    }

    // Delete a goal
    public void deleteGoal(Goal goal) {
        goalRepository.delete(goal);
    }

}
