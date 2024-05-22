package com.skillstorm.backendtaxes.controllers;

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

import com.skillstorm.backendtaxes.models.IncomeSource;
import com.skillstorm.backendtaxes.models.TaxReturn;
import com.skillstorm.backendtaxes.services.TaxReturnService;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/taxes")
public class TaxReturnController {

    @Autowired
    TaxReturnService service;

    // Show all returns in the db
    @GetMapping
    public ResponseEntity<List<TaxReturn>> findAllReturns() {
        List<TaxReturn> returns = service.findAllReturns();
        return new ResponseEntity<List<TaxReturn>>(returns, HttpStatus.OK);
    }

    // Show one return by email. With the email in the path
    @GetMapping("/{email}")
    public ResponseEntity<?> findOnPath(@PathVariable String email) {
        try{
            TaxReturn taxReturn = service.findReturnByEmail(email);
            return new ResponseEntity<TaxReturn>(taxReturn, HttpStatus.OK);
        }
        catch(Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // Show one income source (by EIN) for a specific tax return (by email)
    @GetMapping("/{email}/{ein}")
    public ResponseEntity<IncomeSource> findIncomeSource(@PathVariable String email, @PathVariable int ein ) {
        Optional<IncomeSource> findReturn = service.findIncomeSource(email, ein);

        if (findReturn.isPresent()) {
            return new ResponseEntity<IncomeSource>(findReturn.get(), HttpStatus.OK);
        } else {
            return new ResponseEntity<IncomeSource>(HttpStatus.NOT_FOUND);
        }
    }

    // Delete a tax return by passing in the email in the path
    @DeleteMapping("/delete/{email}")
    public ResponseEntity<?> deleteReturnByEmail(@PathVariable String email) {
        try{
            service.deleteReturnByEmail(email);
            return ResponseEntity.noContent().build();
        }
        catch(Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // Post a tax return by passing in the return
    @PostMapping("/new")
    public ResponseEntity<?> newReturn(@RequestBody TaxReturn taxReturn) {
        try{
            TaxReturn createdReturn = service.saveTaxReturn(taxReturn);
            return new ResponseEntity<TaxReturn>(createdReturn, HttpStatus.OK);
        }
        catch(Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // Update a tax return by passing in the return
    @PutMapping("/update")
    public ResponseEntity<?> updateTaxReturn(@RequestBody TaxReturn taxReturn) {
        try{
            TaxReturn createdReturn = service.saveTaxReturn(taxReturn);
            return new ResponseEntity<TaxReturn>(createdReturn, HttpStatus.OK);
        }
        catch(Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // Update a tax return's income source
    @PutMapping("/update/{email}")
    public ResponseEntity<IncomeSource> updateTaxReturnIncomeSource(@PathVariable String email, @RequestBody IncomeSource incomeSource) {
        IncomeSource updatedSource = service.saveTaxReturnIncomeSource(email, incomeSource);
        return new ResponseEntity<IncomeSource>(updatedSource, HttpStatus.OK);
    }

    // Delete an income source by passing in the email and the ein
    @DeleteMapping("/delete/{email}/{ein}")
    public ResponseEntity<TaxReturn> deleteIncomeSourceByEmailAndEIN(@PathVariable String email, @PathVariable int ein) {
        service.deleteIncomeSourceByEmailAndEIN(email, ein);
        return ResponseEntity.noContent().build();
    }
}
