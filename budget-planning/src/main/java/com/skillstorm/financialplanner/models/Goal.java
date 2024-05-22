package com.skillstorm.financialplanner.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;

import javax.validation.constraints.Email;
import javax.validation.constraints.PositiveOrZero;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Goal {
    @Id
    private String _id;

    @Email(message = "Must be a valid email address")
    private String email;
    
    private String name;

    @PositiveOrZero(message = "Goal amount must be a non-negative number")
    private double goalAmount;

    @PositiveOrZero(message = "Amount saved must be a non-negative number")
    private double amountSaved;

    public Goal(@Email(message = "Must be a valid email address") String email, String name,
            @PositiveOrZero(message = "Goal amount must be a non-negative number") double goalAmount,
            @PositiveOrZero(message = "Amount saved must be a non-negative number") double amountSaved) {
        this.email = email;
        this.name = name;
        this.goalAmount = goalAmount;
        this.amountSaved = amountSaved;
    }

}