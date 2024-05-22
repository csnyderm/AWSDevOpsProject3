package com.skillstorm.financialplanner.controllers;

import com.skillstorm.financialplanner.models.Goal;
import com.skillstorm.financialplanner.services.GoalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping(value = "/goals")
public class GoalController {

    @Autowired
    GoalService goalService;

    @GetMapping
    public ResponseEntity<List<Goal>> getAllGoals() {
        List<Goal> goal = goalService.getAllGoals();

        return new ResponseEntity<List<Goal>>(goal, HttpStatus.OK);
    }

    @GetMapping("id/{id}")
    public ResponseEntity<Goal> getGoalById(@PathVariable String id) {
        Goal goal = goalService.getGoalById(id);

        return new ResponseEntity<Goal>(goal, HttpStatus.OK);
    }

    @GetMapping("/email/{email}")
    public ResponseEntity<List<Goal>> getGoalsByEmail(@PathVariable String email) {
        List<Goal> goals = goalService.getGoalsByEmail(email);

        return new ResponseEntity<List<Goal>>(goals, HttpStatus.OK);
    }

    //ADD A GOAL
    @PostMapping(value = "create")
    public ResponseEntity<Goal> addGoal(@RequestBody @Valid Goal goal) {
        Goal newGoal = goalService.addGoal(goal);

        return new ResponseEntity<Goal>(newGoal, HttpStatus.CREATED);
    }



    // EDIT A GOAL
    @PutMapping("/update")
    public ResponseEntity<Goal> updateGoal(@RequestBody @Valid Goal goal) {
        Goal existingGoal = goalService.updateGoal(goal);
        if (existingGoal == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(existingGoal);
    }

    // DELETE A GOAL
    @DeleteMapping("/delete")
    public ResponseEntity<Void> deleteGoal(@RequestBody Goal goal) {
        goalService.deleteGoal(goal);

        return ResponseEntity.noContent().build();
    }
}
