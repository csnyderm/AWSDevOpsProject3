package com.skillstorm.financialplanner.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.skillstorm.financialplanner.models.Goal;
import com.skillstorm.financialplanner.services.GoalService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(GoalController.class)
public class GoalControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private GoalService goalService;

    @Test
    public void testGetAllGoals() throws Exception {
        List<Goal> mockGoals = new ArrayList<>();
        mockGoals.add(new Goal("1", "test@1.com", "vacation", 1000, 500));
        when(goalService.getAllGoals()).thenReturn(mockGoals);

        mockMvc.perform(get("/goals")
                        .content(objectMapper.writeValueAsString(mockGoals)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray());
    }

    //JUNIT-PLAN-011
    @Test
    public void testGetGoalById() throws Exception {
        Goal mockGoal = new Goal("1", "test@1.com", "vacation", 1000, 500);
        when(goalService.getGoalById(mockGoal.get_id())).thenReturn(mockGoal);

        mockMvc.perform(get("/goals/id/{id}", mockGoal.get_id()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$._id").value(mockGoal.get_id()));
    }

    //JUNIT-PLAN-012
    @Test
    public void testGetGoalsByEmail() throws Exception {
        List<Goal> mockGoals = new ArrayList<>();
        mockGoals.add(new Goal("1", "test@1.com", "vacation", 1000, 500));
        when(goalService.getGoalsByEmail(anyString())).thenReturn(mockGoals);

        mockMvc.perform(get("/goals/email/{email}", "test@1.com"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray());
    }

    //JUNIT-PLAN-013
    @Test
    public void testAddGoal() throws Exception {
        Goal mockGoal = new Goal("1", "test@1.com", "vacation", 1000, 500);
        when(goalService.addGoal(any(Goal.class))).thenReturn(mockGoal);

        mockMvc.perform(post("/goals/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(mockGoal)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.email").value(mockGoal.getEmail()))
                .andExpect(jsonPath("$.name").value(mockGoal.getName()));
    }

    //JUNIT-PLAN-014
    @Test
    public void testUpdateGoal() throws Exception {
        Goal existingGoal = new Goal("1", "test@1.com", "vacation", 1000, 500);
        Goal goal = new Goal();

        // Stub the goalService.updateGoal() method to return a goal
        when(goalService.updateGoal(goal)).thenReturn(goal);

        // Send a PUT request to the /update endpoint
        mockMvc.perform(put("/goals/update")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(goal)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().json(objectMapper.writeValueAsString(goal)));
    }

    //JUNIT-PLAN-015
    @Test
    public void testDeleteGoal() throws Exception {
        Goal goal = new Goal("1", "test@1.com", "vacation", 1000, 500);
        mockMvc.perform(delete("/goals/delete")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(goal)))
                .andExpect(MockMvcResultMatchers.status().isNoContent());

        verify(goalService, times(1)).deleteGoal(goal);
    }

}