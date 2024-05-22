package com.skillstorm.backendtaxes.customExceptions;

public class TaxReturnWithEmailDoesNotExistException extends IllegalArgumentException {
    public TaxReturnWithEmailDoesNotExistException(String message) {
        super(message);
    }
}
