package com.skillstorm.financialplanner.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import com.skillstorm.financialplanner.models.Expense;
import com.skillstorm.financialplanner.models.Planner;
import com.skillstorm.financialplanner.repositories.PlannerRepository;

@SpringBootTest
public class PlannerServiceTest {

    @MockBean
    private PlannerRepository plannerRepository;

    @InjectMocks
    private PlannerService plannerService;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    // JUNIT-PLAN-006
    @Test
    public void testGetAllPlanners() {
        List<Planner> mockPlanners = new ArrayList<>();
        when(plannerRepository.findAll()).thenReturn(mockPlanners);

        List<Planner> planners = plannerService.getAllPlanners();

        assertEquals(mockPlanners, planners);
    }

    // JUNIT-PLAN-007
    @Test
    public void testGetPlannerByEmail() {
        List<String> categories = new ArrayList<>();

        categories.add("housing");
		categories.add("transportation");
		categories.add("insurance");

        Expense housingExpense = new Expense("housing", "housing expense", 1500, 1000, "00/00/0000");
		Expense transportationExpense = new Expense("transportation", "transportation expense", 300, 250, "00/00/0000");
		Expense insuranceExpense = new Expense("insurance", "insurance expense", 500, 400, "00/00/0000");

        List<Expense> expenses = new ArrayList<>();

        expenses.add(housingExpense);
		expenses.add(transportationExpense);
		expenses.add(insuranceExpense);

        Planner planner = new Planner("plannerEmail@test.com", categories, expenses, 3000);
        when(plannerRepository.findByEmail(planner.getEmail())).thenReturn(Optional.of(planner));

        Planner found = plannerService.getPlannerByEmail(planner.getEmail());

        assertEquals(planner, found);
    }

    // JUNIT-PLAN-008
    @Test
    public void testAddPlanner() {
        List<String> categories = new ArrayList<>();

        categories.add("housing");
		categories.add("transportation");
		categories.add("insurance");

        Expense housingExpense = new Expense("housing", "housing expense", 1500, 1000, "00/00/0000");
		Expense transportationExpense = new Expense("transportation", "transportation expense", 300, 250, "00/00/0000");
		Expense insuranceExpense = new Expense("insurance", "insurance expense", 500, 400, "00/00/0000");

        List<Expense> expenses = new ArrayList<>();

        expenses.add(housingExpense);
		expenses.add(transportationExpense);
		expenses.add(insuranceExpense);

        Planner planner = new Planner("plannerEmail@test.com", categories, expenses, 3000);
        when(plannerRepository.save(any(Planner.class))).thenReturn(planner);

        Planner savedPlanner = plannerService.addPlanner(planner);

        assertNotNull(savedPlanner);
    }

    // JUNIT-PLAN-009
    @Test
    public void testUpdatePlanner() {
        List<String> categories = new ArrayList<>();

        categories.add("housing");
		categories.add("transportation");
		categories.add("insurance");

        Expense housingExpense = new Expense("housing", "housing expense", 1500, 1000, "00/00/0000");
		Expense transportationExpense = new Expense("transportation", "transportation expense", 300, 250, "00/00/0000");
		Expense insuranceExpense = new Expense("insurance", "insurance expense", 500, 400, "00/00/0000");

        List<Expense> expenses = new ArrayList<>();

        expenses.add(housingExpense);
		expenses.add(transportationExpense);
		expenses.add(insuranceExpense);

        Planner planner = new Planner("plannerEmail@test.com", categories, expenses, 3000);
        when(plannerRepository.save(any(Planner.class))).thenReturn(planner);

    }

    // JUNIT-PLAN-010
    @Test
    public void testDeletePlanner() {
        List<String> categories = new ArrayList<>();

        categories.add("housing");
		categories.add("transportation");
		categories.add("insurance");

        Expense housingExpense = new Expense("housing", "housing expense", 1500, 1000, "00/00/0000");
		Expense transportationExpense = new Expense("transportation", "transportation expense", 300, 250, "00/00/0000");
		Expense insuranceExpense = new Expense("insurance", "insurance expense", 500, 400, "00/00/0000");

        List<Expense> expenses = new ArrayList<>();

        expenses.add(housingExpense);
		expenses.add(transportationExpense);
		expenses.add(insuranceExpense);

        Planner planner = new Planner("plannerEmail@test.com", categories, expenses, 3000);
        when(plannerRepository.findByEmail(planner.getEmail())).thenReturn(Optional.of(planner));

        plannerService.deletePlanner(planner);

        verify(plannerRepository, times(1)).delete(planner);
    }
    
}
