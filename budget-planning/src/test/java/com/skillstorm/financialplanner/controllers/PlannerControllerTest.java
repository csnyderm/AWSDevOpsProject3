package com.skillstorm.financialplanner.controllers;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.skillstorm.financialplanner.models.Expense;
import com.skillstorm.financialplanner.models.Planner;
import com.skillstorm.financialplanner.services.PlannerService;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PlannerController.class)
public class PlannerControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private PlannerService plannerService;

    // JUNIT-PLAN-001
    @Test
    public void testGetAllPlanners() throws Exception {
        List<Planner> mockReturns = new ArrayList<>();
        when(plannerService.getAllPlanners()).thenReturn(mockReturns);

        mockMvc.perform(get("/planner"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray());

    }

    // JUNIT-PLAN-002
    @Test
    public void testGetPlannerByEmail() throws Exception {
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
        when(plannerService.getPlannerByEmail(planner.getEmail())).thenReturn(planner);

        mockMvc.perform(get("/planner/email/{email}", planner.getEmail()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.email").value(planner.getEmail()));

    }

    // JUNIT-PLAN-003
    @Test
    public void testAddPlanner() throws Exception {
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
        when(plannerService.addPlanner(any(Planner.class))).thenReturn(planner);

        mockMvc.perform(post("/planner/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(planner)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.email").value(planner.getEmail()));

    }

    // JUNIT-PLAN-004
    @Test
    public void testUpdatePlanner() throws Exception {
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
        when(plannerService.updatePlanner(any(Planner.class))).thenReturn(planner);

        mockMvc.perform(put("/planner/update")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(planner)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.email").value(planner.getEmail()));

    }

    // JUNIT-PLAN-005
    @Test
    public void testDeletePlanner() throws Exception {
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

        mockMvc.perform(MockMvcRequestBuilders
                .delete("/planner/delete")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(planner)))
                .andExpect(MockMvcResultMatchers.status().isNoContent());

        verify(plannerService, times(1)).deletePlanner(planner);
    }
    
}
