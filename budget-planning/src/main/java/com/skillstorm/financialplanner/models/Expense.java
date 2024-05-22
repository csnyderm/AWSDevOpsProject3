package com.skillstorm.financialplanner.models;


import javax.validation.constraints.PositiveOrZero;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Expense {
    private String category;
    private String expenseName;

    @PositiveOrZero
    private double actualExpense;

    @PositiveOrZero
    private double desiredExpense;


    private String dueDate;

}