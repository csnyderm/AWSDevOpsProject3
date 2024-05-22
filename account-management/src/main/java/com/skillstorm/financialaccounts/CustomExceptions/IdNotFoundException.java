package com.skillstorm.financialaccounts.CustomExceptions;

// this exception is thrown for any update or delete service where the object
// passed in the body of the request has an ID that does not exist in the database
public class IdNotFoundException extends IllegalArgumentException {
    public IdNotFoundException(String message) {
        super(message);
    }
}
