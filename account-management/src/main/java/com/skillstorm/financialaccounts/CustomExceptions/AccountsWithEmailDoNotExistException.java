package com.skillstorm.financialaccounts.CustomExceptions;

// this exception is thrown for any service method that calls the findByEmail
// method and does not receive an accounts object
public class AccountsWithEmailDoNotExistException extends IllegalArgumentException {

    public AccountsWithEmailDoNotExistException(String message) {
        super(message);
    }

}
