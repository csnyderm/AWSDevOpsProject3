package com.skillstorm.financialaccounts.services;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.skillstorm.financialaccounts.CustomExceptions.AccountsWithEmailAlreadyExists;
import com.skillstorm.financialaccounts.CustomExceptions.AccountsWithEmailDoNotExistException;
import com.skillstorm.financialaccounts.CustomExceptions.IdNotFoundException;
import com.skillstorm.financialaccounts.models.Accounts;
import com.skillstorm.financialaccounts.models.BankAccount;
import com.skillstorm.financialaccounts.models.CreditCard;
import com.skillstorm.financialaccounts.models.Loan;
import com.skillstorm.financialaccounts.repositories.AccountsRepository;

@Service
public class AccountsService {

    @Autowired
    AccountsRepository accountsRepository;

    @Autowired
    SequenceGeneratorService sequenceGeneratorService;

    // get all accounts
    public List<Accounts> getAllAccounts() {
        return accountsRepository.findAll();
    }

    // get Accounts by email
    public Accounts findByEmail(String email) {
        Optional<Accounts> accounts = accountsRepository.findByEmail(email);
        if (accounts.isPresent()) {
            return accounts.get();
        } else {
            throw new AccountsWithEmailDoNotExistException("No accounts found for the provided email address.");
        }
    }

    // create new accounts
    public Accounts createAccounts(Accounts accounts) {
        Optional<Accounts> existingAccounts = accountsRepository.findByEmail(accounts.getEmail());
        if (existingAccounts.isPresent()) {
            throw new AccountsWithEmailAlreadyExists(
                    "The user associated with the email provided already has existing accounts.");
        } else {
            // loop through bank accounts, credit cards, loans, and generate IDs
            for (int i = 0; i < accounts.getBankAccounts().size(); i++) {
                accounts.getBankAccounts().get(i)
                        .setId(sequenceGeneratorService.generateSequence(BankAccount.SEQUENCE_NAME));
            }
            for (int i = 0; i < accounts.getCreditCards().size(); i++) {
                accounts.getCreditCards().get(i)
                        .setId(sequenceGeneratorService.generateSequence(CreditCard.SEQUENCE_NAME));
            }
            for (int i = 0; i < accounts.getLoans().size(); i++) {
                accounts.getLoans().get(i).setId(sequenceGeneratorService.generateSequence(Loan.SEQUENCE_NAME));
            }
            return accountsRepository.save(accounts);
        }
    }

    // update accounts object
    public Accounts updateAccounts(Accounts accounts) {
        Optional<Accounts> existingAccounts = accountsRepository.findByEmail(accounts.getEmail());
        if (existingAccounts.isPresent()) {
            // loop through bank accounts, credit cards, loans, and make sure they all have
            // IDs, if not then generate
            for (int i = 0; i < accounts.getBankAccounts().size(); i++) {
                if (accounts.getBankAccounts().get(i).getId() == null) {
                    accounts.getBankAccounts().get(i)
                            .setId(sequenceGeneratorService.generateSequence(BankAccount.SEQUENCE_NAME));
                }
            }
            for (int i = 0; i < accounts.getCreditCards().size(); i++) {
                if (accounts.getCreditCards().get(i).getId() == null) {
                    accounts.getCreditCards().get(i)
                            .setId(sequenceGeneratorService.generateSequence(CreditCard.SEQUENCE_NAME));
                }
            }
            for (int i = 0; i < accounts.getLoans().size(); i++) {
                if (accounts.getLoans().get(i).getId() == null) {
                    accounts.getLoans().get(i).setId(sequenceGeneratorService.generateSequence(Loan.SEQUENCE_NAME));
                }
            }
            return accountsRepository.save(accounts);
        } else {
            throw new AccountsWithEmailDoNotExistException("No accounts found for the provided email address.");
        }
    }

    // delete accounts object
    public void deleteAccounts(Accounts accounts) {
        accountsRepository.delete(accounts);
    }

    // get all bankAccounts by email
    public List<BankAccount> getAllBankAccounts(String email) {
        Optional<Accounts> accounts = accountsRepository.findByEmail(email);
        if (accounts.isPresent()) {
            return accounts.get().getBankAccounts();
        } else {
            throw new AccountsWithEmailDoNotExistException("No accounts found for the provided email address.");
        }
    }

    // get all creditCards by email
    public List<CreditCard> getAllCreditCards(String email) {
        Optional<Accounts> accounts = accountsRepository.findByEmail(email);
        if (accounts.isPresent()) {
            return accounts.get().getCreditCards();
        } else {
            throw new AccountsWithEmailDoNotExistException("No accounts found for the provided email address.");
        }
    }

    // get all loans by email
    public List<Loan> getAllLoans(String email) {
        Optional<Accounts> accounts = accountsRepository.findByEmail(email);
        if (accounts.isPresent()) {
            return accounts.get().getLoans();
        } else {
            throw new AccountsWithEmailDoNotExistException("No accounts found for the provided email address.");
        }
    }

    // create new bank account
    public BankAccount createBankAccount(String email, BankAccount bankAccount) {
        Optional<Accounts> accounts = accountsRepository.findByEmail(email);
        if (accounts.isPresent()) {
            List<BankAccount> bankAccounts = accounts.get().getBankAccounts();
            bankAccount.setId(sequenceGeneratorService.generateSequence(BankAccount.SEQUENCE_NAME));
            bankAccounts.add(bankAccount);
            accountsRepository.save(accounts.get());
            return bankAccount;
        } else {
            throw new AccountsWithEmailDoNotExistException("No accounts found for the provided email address.");
        }
    }

    // create new credit card
    public CreditCard createCreditCard(String email, CreditCard creditCard) {
        Optional<Accounts> accounts = accountsRepository.findByEmail(email);
        if (accounts.isPresent()) {
            List<CreditCard> creditCards = accounts.get().getCreditCards();
            creditCard.setId(sequenceGeneratorService.generateSequence(CreditCard.SEQUENCE_NAME));
            creditCards.add(creditCard);
            accountsRepository.save(accounts.get());
            return creditCard;
        } else {
            throw new AccountsWithEmailDoNotExistException("No accounts found for the provided email address.");
        }
    }

    // create new loan
    public Loan createLoan(String email, Loan loan) {
        Optional<Accounts> accounts = accountsRepository.findByEmail(email);
        if (accounts.isPresent()) {
            List<Loan> loans = accounts.get().getLoans();
            loan.setId(sequenceGeneratorService.generateSequence(Loan.SEQUENCE_NAME));
            loans.add(loan);
            accountsRepository.save(accounts.get());
            return loan;
        } else {
            throw new AccountsWithEmailDoNotExistException("No accounts found for the provided email address.");
        }
    }

    // update bank account by id
    public BankAccount updateBankAccount(String email, BankAccount updatedBankAccount) {
        boolean existingBankAccountFound = false;
        Optional<Accounts> accounts = accountsRepository.findByEmail(email);
        if (accounts.isPresent()) {
            List<BankAccount> bankAccounts = accounts.get().getBankAccounts();
            for (int i = 0; i < bankAccounts.size(); i++) {
                if (bankAccounts.get(i).getId().equals(updatedBankAccount.getId())) {
                    bankAccounts.set(i, updatedBankAccount);
                    accountsRepository.save(accounts.get());
                    existingBankAccountFound = true;
                }
            }
            if(existingBankAccountFound) {
            return updatedBankAccount;
            }
            else {
                throw new IdNotFoundException("There is no existing bank account associated with the given email that possesses the ID of the bank account that was passed in the body of this request.");
            }
        } else {
            throw new AccountsWithEmailDoNotExistException("No accounts found for the provided email address.");
        }
    }

    // update credit card by id
    public CreditCard updateCreditCard(String email, CreditCard updatedCreditCard) {
        boolean existingCreditCardFound = false;
        Optional<Accounts> accounts = accountsRepository.findByEmail(email);
        if (accounts.isPresent()) {
            List<CreditCard> creditCards = accounts.get().getCreditCards();
            for (int i = 0; i < creditCards.size(); i++) {
                if (creditCards.get(i).getId().equals(updatedCreditCard.getId())) {
                    creditCards.set(i, updatedCreditCard);
                    accountsRepository.save(accounts.get());
                    existingCreditCardFound = true;
                }
            }
            if(existingCreditCardFound) {
                return updatedCreditCard;
            }
            else {
                throw new IdNotFoundException("There is no existing credit card associated with the given email that possesses the ID of the credit card that was passed in the body of this request.");
            }
        } else {
            throw new AccountsWithEmailDoNotExistException("No accounts found for the provided email address.");
        }
    }

    // update loan by id
    public Loan updateLoan(String email, Loan updatedLoan) {
        boolean existingLoanFound = false;
        Optional<Accounts> accounts = accountsRepository.findByEmail(email);
        if (accounts.isPresent()) {
            List<Loan> loans = accounts.get().getLoans();
            for (int i = 0; i < loans.size(); i++) {
                if (loans.get(i).getId().equals(updatedLoan.getId())) {
                    loans.set(i, updatedLoan);
                    accountsRepository.save(accounts.get());
                    existingLoanFound = true;
                }
            }
            if(existingLoanFound) {
                return updatedLoan;
            }
            else {
                throw new IdNotFoundException("There is no existing loan associated with the given email that possesses the ID of the loan that was passed in the body of this request.");
            }
        } else {
            throw new AccountsWithEmailDoNotExistException("No accounts found for the provided email address.");
        }
    }

    // delete bank account by id
    public void deleteBankAccountById(String email, String bankAccountId) {
        boolean existingBankAccountFound = false;
        Optional<Accounts> accounts = accountsRepository.findByEmail(email);
        if (accounts.isPresent()) {
            List<BankAccount> bankAccounts = accounts.get().getBankAccounts();
            if (accounts.isPresent()) {
                for (int i = 0; i < bankAccounts.size(); i++) {
                    if (bankAccounts.get(i).getId().equals(bankAccountId)) {
                        bankAccounts.remove(i);
                        accountsRepository.save(accounts.get());
                        existingBankAccountFound = true;
                    }
                }
            }
            if(!existingBankAccountFound) {
                throw new IdNotFoundException("There is no existing bank account associated with the given email that possesses the ID of the bank account that was passed in the body of this request.");
            }
        } else {
            throw new AccountsWithEmailDoNotExistException("No accounts found for the provided email address.");
        }
    }

    // delete credit card by id
    public void deleteCreditCardById(String email, String creditCardId) {
        boolean existingCreditCardFound = false;
        Optional<Accounts> accounts = accountsRepository.findByEmail(email);
        if (accounts.isPresent()) {
            List<CreditCard> creditCards = accounts.get().getCreditCards();
            for (int i = 0; i < creditCards.size(); i++) {
                if (creditCards.get(i).getId().equals(creditCardId)) {
                    creditCards.remove(i);
                    accountsRepository.save(accounts.get());
                    existingCreditCardFound = true;
                }
            }
            if(!existingCreditCardFound) {
                throw new IdNotFoundException("There is no existing credit card associated with the given email that possesses the ID of the credit card that was passed in the body of this request.");
            }
        } else {
            throw new AccountsWithEmailDoNotExistException("No accounts found for the provided email address.");
        }
    }

    // delete loan by id
    public void deleteLoanById(String email, String loanId) {
        boolean existingLoanFound = false;
        Optional<Accounts> accounts = accountsRepository.findByEmail(email);
        if (accounts.isPresent()) {
            List<Loan> loans = accounts.get().getLoans();
            for (int i = 0; i < loans.size(); i++) {
                if (loans.get(i).getId().equals(loanId)) {
                    loans.remove(i);
                    accountsRepository.save(accounts.get());
                    existingLoanFound = true;
                }
            }
            if(!existingLoanFound) {
                throw new IdNotFoundException("There is no existing loan associated with the given email that possesses the ID of the loan that was passed in the body of this request.");
            }
        } else {
            throw new AccountsWithEmailDoNotExistException("No accounts found for the provided email address.");
        }
    }

    // update bank account by index
    public BankAccount updateBankAccountByIndex(String email, int bankAccountIndex, BankAccount updatedBankAccount) {
        Optional<Accounts> accounts = accountsRepository.findByEmail(email);
        if (accounts.isPresent()) {
            List<BankAccount> bankAccounts = accounts.get().getBankAccounts();
            if (bankAccountIndex >= 0 && bankAccountIndex < bankAccounts.size()) {
                bankAccounts.set(bankAccountIndex, updatedBankAccount);
                accountsRepository.save(accounts.get());
                return updatedBankAccount;
            } else {
                throw new RuntimeException("Invalid index");
            }
        } else {
            throw new AccountsWithEmailDoNotExistException("No accounts found for the provided email address.");
        }
    }

    // update credit card by index
    public CreditCard updateCreditCardByIndex(String email, int creditCardIndex, CreditCard updatedCreditCard) {
        Optional<Accounts> accounts = accountsRepository.findByEmail(email);
        if (accounts.isPresent()) {
            List<CreditCard> creditCards = accounts.get().getCreditCards();
            if (creditCardIndex >= 0 && creditCardIndex < creditCards.size()) {
                creditCards.set(creditCardIndex, updatedCreditCard);
                accountsRepository.save(accounts.get());
                return updatedCreditCard;
            } else {
                throw new RuntimeException("Invalid index");
            }
        } else {
            throw new AccountsWithEmailDoNotExistException("No accounts found for the provided email address.");
        }
    }

    // update loan by index
    public Loan updateLoanByIndex(String email, int loanIndex, Loan updatedLoan) {
        Optional<Accounts> accounts = accountsRepository.findByEmail(email);
        if (accounts.isPresent()) {
            List<Loan> loans = accounts.get().getLoans();
            if (loanIndex >= 0 && loanIndex < loans.size()) {
                loans.set(loanIndex, updatedLoan);
                accountsRepository.save(accounts.get());
                return updatedLoan;
            } else {
                throw new RuntimeException("Invalid index");
            }
        } else {
            throw new AccountsWithEmailDoNotExistException("No accounts found for the provided email address.");
        }
    }

    // delete bank account by index
    public void deleteBankAccountByIndex(String email, int bankAccountIndex) {
        Optional<Accounts> accounts = accountsRepository.findByEmail(email);
        if (accounts.isPresent()) {
            List<BankAccount> bankAccounts = accounts.get().getBankAccounts();
            if (bankAccountIndex >= 0 && bankAccountIndex < bankAccounts.size()) {
                bankAccounts.remove(bankAccountIndex);
                accountsRepository.save(accounts.get());
            } else {
                throw new RuntimeException("Invalid index");
            }
        } else {
            throw new AccountsWithEmailDoNotExistException("No accounts found for the provided email address.");
        }
    }

    // delete credit card by index
    public void deleteCreditCardByIndex(String email, int creditCardIndex) {
        Optional<Accounts> accounts = accountsRepository.findByEmail(email);
        if (accounts.isPresent()) {
            List<CreditCard> creditCards = accounts.get().getCreditCards();
            if (creditCardIndex >= 0 && creditCardIndex < creditCards.size()) {
                creditCards.remove(creditCardIndex);
                accountsRepository.save(accounts.get());
            } else {
                throw new RuntimeException("Invalid index");
            }
        } else {
            throw new AccountsWithEmailDoNotExistException("No accounts found for the provided email address.");
        }
    }

    // delete loan by index
    public void deleteLoanByIndex(String email, int loanIndex) {
        Optional<Accounts> accounts = accountsRepository.findByEmail(email);
        if (accounts.isPresent()) {
            List<Loan> loans = accounts.get().getLoans();
            if (loanIndex >= 0 && loanIndex < loans.size()) {
                loans.remove(loanIndex);
                accountsRepository.save(accounts.get());
            } else {
                throw new RuntimeException("Invalid index");
            }
        } else {
            throw new AccountsWithEmailDoNotExistException("No accounts found for the provided email address.");
        }
    }
}
