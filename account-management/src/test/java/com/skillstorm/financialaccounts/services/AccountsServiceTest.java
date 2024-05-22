package com.skillstorm.financialaccounts.services;

import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.skillstorm.financialaccounts.models.Accounts;
import com.skillstorm.financialaccounts.models.BankAccount;
import com.skillstorm.financialaccounts.models.CreditCard;
import com.skillstorm.financialaccounts.models.Loan;
import com.skillstorm.financialaccounts.repositories.AccountsRepository;

@ExtendWith(MockitoExtension.class)
class AccountsServiceTest {

    @Mock
    private AccountsRepository accountsRepository;

    @InjectMocks
    private AccountsService accountsService;

    // JUNIT-ACCOUNTS-005
    // findByEmail test
    @Test
    public void findByEmailTest() {
        String email = "testemail@yahoo.com";

        Accounts expectedAccounts = new Accounts();
        when(accountsRepository.findByEmail(email)).thenReturn(Optional.of(expectedAccounts));
        assertTrue(accountsService.findByEmail(email).equals(expectedAccounts));
    }

    // JUNIT-ACCOUNTS-006
    // getBankAccountsByEmail test
    @Test
    public void getBankAccountsByEmailTest() {
        Accounts accounts = new Accounts("testemail@yahoo.com", new ArrayList<BankAccount>(),
                new ArrayList<CreditCard>(), new ArrayList<Loan>());
        accounts.getBankAccounts().add(new BankAccount("Chase", "Checking", 10000));
        accounts.getCreditCards().add(new CreditCard("Chase", 15000, 500, 9.5));
        accounts.getLoans().add(new Loan("Chase", "Auto", 5000, 5.3, 24, false));
        accounts.getBankAccounts().add(new BankAccount("Test Bank", "Checking", 999999));

        when(accountsRepository.findByEmail(any())).thenReturn(Optional.of(accounts));

        List<BankAccount> bankAccounts = accountsService.getAllBankAccounts("testemail@yahoo.com");
        assertTrue(bankAccounts.get(1).getBankName().equals("Test Bank"));
    }

    // JUNIT-ACCOUNTS-007
    // getCreditCardsByEmail test
    @Test
    public void getCreditCardsByEmailTest() {
        Accounts accounts = new Accounts("testemail@yahoo.com", new ArrayList<BankAccount>(),
                new ArrayList<CreditCard>(), new ArrayList<Loan>());
        accounts.getBankAccounts().add(new BankAccount("Chase", "Checking", 10000));
        accounts.getCreditCards().add(new CreditCard("Chase", 15000, 500, 9.5));
        accounts.getLoans().add(new Loan("Chase", "Auto", 5000, 5.3, 24, false));
        accounts.getCreditCards().add(new CreditCard("Test Bank", 30000, 0, 8.5));

        when(accountsRepository.findByEmail(any())).thenReturn(Optional.of(accounts));

        List<CreditCard> creditCards = accountsService.getAllCreditCards("testemail@yahoo.com");
        assertTrue(creditCards.get(1).getBankName().equals("Test Bank"));
    }

    // JUNIT-ACCOUNTS-008
    // getLoansByEmail test
    @Test
    public void getLoansByEmailTest() {
        Accounts accounts = new Accounts("testemail@yahoo.com", new ArrayList<BankAccount>(),
                new ArrayList<CreditCard>(), new ArrayList<Loan>());
        accounts.getBankAccounts().add(new BankAccount("Chase", "Checking", 10000));
        accounts.getCreditCards().add(new CreditCard("Chase", 15000, 500, 9.5));
        accounts.getLoans().add(new Loan("Chase", "Auto", 5000, 5.3, 24, false));
        accounts.getLoans().add(new Loan("Test Bank", "Mortgage", 50000, 3, 84, false));

        when(accountsRepository.findByEmail(any())).thenReturn(Optional.of(accounts));

        List<Loan> loans = accountsService.getAllLoans("testemail@yahoo.com");
        assertTrue(loans.get(1).getBankName().equals("Test Bank"));
    }

    // JUNIT-ACCOUNTS-009
    // updateBankAccountByIndex test
    @Test
    public void updateBankAccountByIndexTest() {
        Accounts accounts = new Accounts("testemail@yahoo.com", new ArrayList<BankAccount>(),
                new ArrayList<CreditCard>(), new ArrayList<Loan>());
        accounts.getBankAccounts().add(new BankAccount("Chase", "Checking", 10000));
        accounts.getCreditCards().add(new CreditCard("Chase", 15000, 500, 9.5));
        accounts.getLoans().add(new Loan("Chase", "Auto", 5000, 5.3, 24, false));
        accounts.getBankAccounts().add(new BankAccount("Test Bank", "Checking", 999999));

        when(accountsRepository.findByEmail(any())).thenReturn(Optional.of(accounts));

        BankAccount updatedBankAccount = accountsService.getAllBankAccounts("testemail@yahoo.com").get(1);
        updatedBankAccount.setBankName("Updated Bank Name");

        accountsService.updateBankAccountByIndex("testemail@yahoo.com", 1, updatedBankAccount);

        assertTrue(accountsService.getAllBankAccounts("testemail@yahoo.com").get(1).getBankName()
                .equals("Updated Bank Name"));
    }

    // JUNIT-ACCOUNTS-010
    // updateCreditCardByIndex test
    @Test
    public void updateCreditCardByIndexTest() {
        Accounts accounts = new Accounts("testemail@yahoo.com", new ArrayList<BankAccount>(),
                new ArrayList<CreditCard>(), new ArrayList<Loan>());
        accounts.getBankAccounts().add(new BankAccount("Chase", "Checking", 10000));
        accounts.getCreditCards().add(new CreditCard("Chase", 15000, 500, 9.5));
        accounts.getLoans().add(new Loan("Chase", "Auto", 5000, 5.3, 24, false));
        accounts.getCreditCards().add(new CreditCard("Test Bank", 30000, 0, 8.5));

        when(accountsRepository.findByEmail(any())).thenReturn(Optional.of(accounts));

        CreditCard updatedCreditCard = accountsService.getAllCreditCards("testemail@yahoo.com").get(1);
        updatedCreditCard.setBankName("Updated Bank Name");

        accountsService.updateCreditCardByIndex("testemail@yahoo.com", 1, updatedCreditCard);

        assertTrue(accountsService.getAllCreditCards("testemail@yahoo.com").get(1).getBankName()
                .equals("Updated Bank Name"));
    }

    // JUNIT-ACCOUNTS-011
    // updateLoanByIndex test
    @Test
    public void updateLoanByIndexTest() {
        Accounts accounts = new Accounts("testemail@yahoo.com", new ArrayList<BankAccount>(),
                new ArrayList<CreditCard>(), new ArrayList<Loan>());
        accounts.getBankAccounts().add(new BankAccount("Chase", "Checking", 10000));
        accounts.getCreditCards().add(new CreditCard("Chase", 15000, 500, 9.5));
        accounts.getLoans().add(new Loan("Chase", "Auto", 5000, 5.3, 24, false));
        accounts.getLoans().add(new Loan("Test Bank", "Mortgage", 50000, 3, 84, false));

        when(accountsRepository.findByEmail(any())).thenReturn(Optional.of(accounts));

        Loan updatedLoan = accountsService.getAllLoans("testemail@yahoo.com").get(1);
        updatedLoan.setBankName("Updated Bank Name");

        accountsService.updateLoanByIndex("testemail@yahoo.com", 1, updatedLoan);

        assertTrue(accountsService.getAllLoans("testemail@yahoo.com").get(1).getBankName().equals("Updated Bank Name"));
    }

    // JUNIT-ACCOUNTS-012
    // deleteBankAccountByIndex test
    @Test
    public void deleteBankAccountByIndexTest() {
        Accounts accounts = new Accounts("testemail@yahoo.com", new ArrayList<BankAccount>(),
                new ArrayList<CreditCard>(), new ArrayList<Loan>());
        accounts.getBankAccounts().add(new BankAccount("Chase", "Checking", 10000));
        accounts.getCreditCards().add(new CreditCard("Chase", 15000, 500, 9.5));
        accounts.getLoans().add(new Loan("Chase", "Auto", 5000, 5.3, 24, false));
        accounts.getBankAccounts().add(new BankAccount("Test Bank", "Checking", 999999));

        when(accountsRepository.findByEmail(any())).thenReturn(Optional.of(accounts));

        BankAccount updatedBankAccount = accountsService.getAllBankAccounts("testemail@yahoo.com").get(1);
        updatedBankAccount.setBankName("Updated Bank Name");

        accountsService.updateBankAccountByIndex("testemail@yahoo.com", 1, updatedBankAccount);

        accountsService.deleteBankAccountByIndex("testemail@yahoo.com", 0);
        assertTrue(accountsService.getAllBankAccounts("testemail@yahoo.com").get(0).getBankName()
                .equals("Updated Bank Name"));
    }

    // JUNIT-ACCOUNTS-013
    // deleteCreditCardByIndex test
    @Test
    public void deleteCreditCardByIndexTest() {
        Accounts accounts = new Accounts("testemail@yahoo.com", new ArrayList<BankAccount>(),
                new ArrayList<CreditCard>(), new ArrayList<Loan>());
        accounts.getBankAccounts().add(new BankAccount("Chase", "Checking", 10000));
        accounts.getCreditCards().add(new CreditCard("Chase", 15000, 500, 9.5));
        accounts.getLoans().add(new Loan("Chase", "Auto", 5000, 5.3, 24, false));
        accounts.getCreditCards().add(new CreditCard("Test Bank", 30000, 0, 8.5));

        when(accountsRepository.findByEmail(any())).thenReturn(Optional.of(accounts));

        CreditCard updatedCreditCard = accountsService.getAllCreditCards("testemail@yahoo.com").get(1);
        updatedCreditCard.setBankName("Updated Bank Name");

        accountsService.updateCreditCardByIndex("testemail@yahoo.com", 1, updatedCreditCard);

        accountsService.deleteCreditCardByIndex("testemail@yahoo.com", 0);
        assertTrue(accountsService.getAllCreditCards("testemail@yahoo.com").get(0).getBankName()
                .equals("Updated Bank Name"));
    }

    // JUNIT-ACCOUNTS-014
    // deleteLoanByIndex test
    @Test
    public void deleteLoanByIndexTest() {
        Accounts accounts = new Accounts("testemail@yahoo.com", new ArrayList<BankAccount>(),
                new ArrayList<CreditCard>(), new ArrayList<Loan>());
        accounts.getBankAccounts().add(new BankAccount("Chase", "Checking", 10000));
        accounts.getCreditCards().add(new CreditCard("Chase", 15000, 500, 9.5));
        accounts.getLoans().add(new Loan("Chase", "Auto", 5000, 5.3, 24, false));
        accounts.getLoans().add(new Loan("Test Bank", "Mortgage", 50000, 3, 84, false));

        when(accountsRepository.findByEmail(any())).thenReturn(Optional.of(accounts));

        Loan updatedLoan = accountsService.getAllLoans("testemail@yahoo.com").get(1);
        updatedLoan.setBankName("Updated Bank Name");

        accountsService.updateLoanByIndex("testemail@yahoo.com", 1, updatedLoan);

        accountsService.deleteLoanByIndex("testemail@yahoo.com", 0);
        assertTrue(accountsService.getAllLoans("testemail@yahoo.com").get(0).getBankName().equals("Updated Bank Name"));
    }
}
