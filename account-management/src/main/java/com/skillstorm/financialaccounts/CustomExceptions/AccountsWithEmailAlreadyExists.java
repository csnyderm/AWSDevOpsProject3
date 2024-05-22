package com.skillstorm.financialaccounts.CustomExceptions;

// exception that is thrown when an accounts object is attempted to be added to the db, but
// an accounts object with the associated email already exists in the db
public class AccountsWithEmailAlreadyExists extends IllegalArgumentException {

    public AccountsWithEmailAlreadyExists(String message) {
        super(message);
    }
}
