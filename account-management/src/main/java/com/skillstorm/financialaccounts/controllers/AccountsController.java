package com.skillstorm.financialaccounts.controllers;

import org.springframework.http.HttpStatus;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.skillstorm.financialaccounts.models.Accounts;
import com.skillstorm.financialaccounts.models.BankAccount;
import com.skillstorm.financialaccounts.models.CreditCard;
import com.skillstorm.financialaccounts.models.Loan;
import com.skillstorm.financialaccounts.services.AccountsService;

@RestController
@RequestMapping("/accounts")
public class AccountsController {

    @Autowired
    AccountsService accountsService;

    // get all accounts
    @GetMapping
    public ResponseEntity<List<Accounts>> getAllAccounts() {
        List<Accounts> accounts = accountsService.getAllAccounts();
        if (accounts.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return new ResponseEntity<>(accounts, HttpStatus.OK);
    }

    // get accounts by email
    @GetMapping("/email/{email}")
    public ResponseEntity<?> getAccountsByEmail(@PathVariable String email) {
        try{
            Accounts accounts = accountsService.findByEmail(email);
            return new ResponseEntity<Accounts>(accounts, HttpStatus.OK);
        }
        catch(Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // create new accounts
    @PostMapping("/newAccounts")
    public ResponseEntity<?> createAccounts(@RequestBody Accounts accounts) {
        try{
            Accounts newAccounts = accountsService.createAccounts(accounts);
            return new ResponseEntity<>(newAccounts, HttpStatus.CREATED);
        }
        catch(Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // update accounts object
    @PutMapping("/updateAccounts")
    public ResponseEntity<?> updateAccounts(@RequestBody Accounts accounts) {
        try{
            Accounts updatedAccounts = accountsService.updateAccounts(accounts);
            return new ResponseEntity<Accounts>(updatedAccounts, HttpStatus.OK);
        }
        catch(Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // delete accounts object
    @DeleteMapping("/deleteAccounts")
    public ResponseEntity<?> deleteAccounts(@RequestBody Accounts accounts) {
        try{
            accountsService.deleteAccounts(accounts);
            return ResponseEntity.noContent().build();
        }
        catch(Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // get all bank accounts by email
    @GetMapping("/{email}/bankAccounts")
    public ResponseEntity<?> getAllBankAccounts(@PathVariable String email) {
        try{
            List<BankAccount> bankAccounts = accountsService.getAllBankAccounts(email);
            return new ResponseEntity<List<BankAccount>>(bankAccounts, HttpStatus.OK);
        }
        catch(Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // get all credit cards by email
    @GetMapping("/{email}/creditCards")
    public ResponseEntity<?> getAllCreditCards(@PathVariable String email) {
        try{
            List<CreditCard> creditCards = accountsService.getAllCreditCards(email);
            return new ResponseEntity<List<CreditCard>>(creditCards, HttpStatus.OK);
        }
        catch(Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // get all loans by email
    @GetMapping("/{email}/loans")
    public ResponseEntity<?> getAllLoans(@PathVariable String email) {
        try{
            List<Loan> loans = accountsService.getAllLoans(email);
            return new ResponseEntity<List<Loan>>(loans, HttpStatus.OK);
        }
        catch(Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // create new bank account
    @PostMapping("/{email}/newBankAccount")
    public ResponseEntity<?> createBankAccount(@PathVariable String email,
            @RequestBody BankAccount bankAccount) {
        try{
            BankAccount newBankAccount = accountsService.createBankAccount(email, bankAccount);
            return new ResponseEntity<>(newBankAccount, HttpStatus.CREATED);
        }
        catch(Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // create new credit card
    @PostMapping("/{email}/newCreditCard")
    public ResponseEntity<?> createCreditCard(@PathVariable String email,
            @RequestBody CreditCard creditCard) {
        try{
            CreditCard newCreditCard = accountsService.createCreditCard(email, creditCard);
            return new ResponseEntity<>(newCreditCard, HttpStatus.CREATED);
        }
        catch(Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // create new loan
    @PostMapping("/{email}/newLoan")
    public ResponseEntity<?> createLoan(@PathVariable String email, @RequestBody Loan loan) {
        try{
            Loan newLoan = accountsService.createLoan(email, loan);
            return new ResponseEntity<>(newLoan, HttpStatus.CREATED);
        }
        catch(Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // update bank account
    @PutMapping("/{email}/bankAccounts/update")
    public ResponseEntity<?> updateBankAccount(
            @PathVariable String email,
            @RequestBody BankAccount updatedBankAccount) {
        try{
            BankAccount bankAccount = accountsService.updateBankAccount(email, updatedBankAccount);
            return ResponseEntity.ok(bankAccount);
        }
        catch(Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // update credit card
    @PutMapping("/{email}/creditCards/update")
    public ResponseEntity<?> updateCreditCard(
            @PathVariable String email,
            @RequestBody CreditCard updatedCreditCard) {
        try{
            CreditCard creditCard = accountsService.updateCreditCard(email, updatedCreditCard);
            return ResponseEntity.ok(creditCard);
        }
        catch(Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // update loan
    @PutMapping("/{email}/loans/update")
    public ResponseEntity<?> updateLoan(
            @PathVariable String email,
            @RequestBody Loan updatedLoan) {
        try{
            Loan loan = accountsService.updateLoan(email, updatedLoan);
            return ResponseEntity.ok(loan);
        }
        catch(Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }


    // delete bank account by id
    @DeleteMapping("/{email}/bankAccounts/deleteById/{bankAccountId}")
    public ResponseEntity<?> deleteBankAccountByIndexById(@PathVariable String email, @PathVariable String bankAccountId) {
        try{
            accountsService.deleteBankAccountById(email, bankAccountId);
            return ResponseEntity.noContent().build();
        }
        catch(Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    } 

    // delete credit card by id
    @DeleteMapping("/{email}/creditCards/deleteById/{creditCardId}")
    public ResponseEntity<?> deleteCreditCardById(@PathVariable String email, @PathVariable String creditCardId) {
        try{
            accountsService.deleteCreditCardById(email, creditCardId);
            return ResponseEntity.noContent().build();
        }
        catch(Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    } 

    // delete loan by id
    @DeleteMapping("/{email}/loans/deleteById/{loanId}")
    public ResponseEntity<?> deleteLoanById(@PathVariable String email, @PathVariable String loanId) {
        try{
            accountsService.deleteLoanById(email, loanId);
            return ResponseEntity.noContent().build();
        }
        catch(Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }


    // update bank account by index
    @PutMapping("/{email}/bankAccounts/updateByIndex/{bankAccountIndex}")
    public ResponseEntity<?> updateBankAccountByIndex(
            @PathVariable String email,
            @PathVariable int bankAccountIndex,
            @RequestBody BankAccount updatedBankAccount) {
        try{
            BankAccount bankAccount = accountsService.updateBankAccountByIndex(email, bankAccountIndex, updatedBankAccount);
            return ResponseEntity.ok(bankAccount);
        }
        catch(Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // update credit card by index
    @PutMapping("/{email}/creditCards/updateByIndex/{creditCardIndex}")
    public ResponseEntity<?> updateCreditCardByIndex(
            @PathVariable String email,
            @PathVariable int creditCardIndex,
            @RequestBody CreditCard updatedCreditCard) {
        try{
            CreditCard creditCard = accountsService.updateCreditCardByIndex(email, creditCardIndex, updatedCreditCard);
            return ResponseEntity.ok(creditCard);
        }
        catch(Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // update loan by index
    @PutMapping("/{email}/loans/updateByIndex/{loanIndex}")
    public ResponseEntity<?> updateLoanByIndex(
            @PathVariable String email,
            @PathVariable int loanIndex,
            @RequestBody Loan updatedLoan) {
        try{
            Loan loan = accountsService.updateLoanByIndex(email, loanIndex, updatedLoan);
            return ResponseEntity.ok(loan);
        }
        catch(Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // delete bank account by index
    @DeleteMapping("/{email}/bankAccounts/deleteByIndex/{bankAccountIndex}")
    public ResponseEntity<?> deleteBankAccountByIndexByIndex(@PathVariable String email, @PathVariable int bankAccountIndex) {
        try{
            accountsService.deleteBankAccountByIndex(email, bankAccountIndex);
            return ResponseEntity.noContent().build();
        }
        catch(Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    } 

    // delete credit card by index
    @DeleteMapping("/{email}/creditCards/deleteByIndex/{creditCardIndex}")
    public ResponseEntity<?> deleteCreditCardByIndex(@PathVariable String email, @PathVariable int creditCardIndex) {
        try{
            accountsService.deleteCreditCardByIndex(email, creditCardIndex);
            return ResponseEntity.noContent().build();
        }
        catch(Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    } 

    // delete loan by index
    @DeleteMapping("/{email}/loans/deleteByIndex/{loanIndex}")
    public ResponseEntity<?> deleteLoanByIndex(@PathVariable String email, @PathVariable int loanIndex) {
        try{
            accountsService.deleteLoanByIndex(email, loanIndex);
            return ResponseEntity.noContent().build();
        }
        catch(Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
