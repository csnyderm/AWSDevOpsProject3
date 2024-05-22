package com.skillstorm.investments.controllers;

import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.skillstorm.investments.models.Investment;
import com.skillstorm.investments.services.InvestmentService;

@RestController
@RequestMapping(value="/investments")
public class InvestmentController {

    @Autowired
    InvestmentService investmentService;

    // get all investments by user email
    @GetMapping("/email/{email}")
    public ResponseEntity<List<Investment>>getInvestmentsByEmail(@PathVariable String email) {
        List<Investment> investments = investmentService.getInvestmentsByEmail(email);
        return new ResponseEntity<List<Investment>>(investments, HttpStatus.OK);
    }
    
    
    // add a new investment
    @PostMapping(value="/new")
    public ResponseEntity<?> newInvestment(@RequestBody @Valid Investment investment) {
        try{
            return ResponseEntity.status(201).body(investmentService.addInvestment(investment));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // edit an investment
    @PutMapping("/update")
    public ResponseEntity<Investment> updateInvestments(@Valid @RequestBody Investment updatedInvestment) {
        Investment updatedInvestmentResult = investmentService.updateInvestments(updatedInvestment);
        return ResponseEntity.ok(updatedInvestmentResult);
    }

    // delete
    @DeleteMapping("/delete")
    public ResponseEntity<Void> deleteInvestment(@RequestBody Investment investment) {        
        investmentService.deleteInvestment(investment);

        return ResponseEntity.noContent().build();
    }

    // delete all investments
    @DeleteMapping("/deleteAll/{email}")
    public ResponseEntity<Void> deleteAllInvestments(@PathVariable String email) {
        investmentService.deleteInvestments(email);

        return ResponseEntity.noContent().build();
    }
}
