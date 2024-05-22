package com.skillstorm.financialplanner.controllers;

import com.skillstorm.financialplanner.models.Planner;
import com.skillstorm.financialplanner.services.PlannerService;

import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/planner")
public class PlannerController {

    @Autowired
    PlannerService plannerService;



    @GetMapping
    public ResponseEntity<List<Planner>> getAllPlanners() {
        List<Planner> planners = plannerService.getAllPlanners();

        return new ResponseEntity<List<Planner>>(planners, HttpStatus.OK);
    }

    @GetMapping("/email/{email}")
    public ResponseEntity<Planner> getPlannerByEmail(@PathVariable String email) {
        Planner planner = plannerService.getPlannerByEmail(email);

        return new ResponseEntity<Planner>(planner, HttpStatus.OK);
    }

    //ADD A PLANNER
    @PostMapping(value = "create")
    public ResponseEntity<?> addPlanner(@RequestBody @Valid Planner planner) {
        try{
            return ResponseEntity.status(201).body(plannerService.addPlanner(planner));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }



    // EDIT A PLANNER
    @PutMapping("/update")
    public ResponseEntity<Planner> updatePlanner(@RequestBody @Valid Planner planner) {
        Planner existingPlanner = plannerService.updatePlanner(planner);
        if (existingPlanner == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(existingPlanner);        
    }

    // DELETE A PLANNER
    @DeleteMapping("/delete") 
    public ResponseEntity<Void> deletePlanner(@RequestBody Planner planner) {
        plannerService.deletePlanner(planner);

        return ResponseEntity.noContent().build();
    }
}   
