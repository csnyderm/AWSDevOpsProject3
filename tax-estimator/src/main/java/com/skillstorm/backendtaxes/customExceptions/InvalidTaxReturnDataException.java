package com.skillstorm.backendtaxes.customExceptions;

public class InvalidTaxReturnDataException extends IllegalArgumentException {
    public InvalidTaxReturnDataException(String message) {
        super(message);
    }
}
