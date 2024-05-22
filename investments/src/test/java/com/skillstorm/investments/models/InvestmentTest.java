package com.skillstorm.investments.models;

import org.junit.jupiter.api.Test;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class InvestmentTest {

    // JUNIT-INVESTMENT-006
    @Test
    public void testValidation() {
        // Create a ValidatorFactory and get a Validator instance
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        Validator validator = factory.getValidator();

        // Create an Investment instance with invalid data
        Investment investment = new Investment("", "", "", -1, -1);

        // Validate the Investment instance using the Validator
        Set<ConstraintViolation<Investment>> violations = validator.validate(investment);

        // Assert that the number of violations matches the expected amount
        assertEquals(6, violations.size());
    }
}
