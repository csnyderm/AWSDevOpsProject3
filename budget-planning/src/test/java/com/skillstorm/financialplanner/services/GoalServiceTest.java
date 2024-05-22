package com.skillstorm.financialplanner.services;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.junit.jupiter.api.Assertions.*;

import com.skillstorm.financialplanner.models.Goal;
import com.skillstorm.financialplanner.repositories.GoalRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@SpringBootTest
public class GoalServiceTest {

    @MockBean
    private GoalRepository goalRepository;

    @InjectMocks
    private GoalService goalService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testGetAllGoals() {
        List<Goal> mockGoals = new ArrayList<>();
        when(goalRepository.findAll()).thenReturn(mockGoals);

        List<Goal> goals = goalService.getAllGoals();

        assertEquals(mockGoals, goals);
    }

    // JUNIT-PLAN-16
    @Test
    public void testGetGoalById() {
        String id = "1";
        Goal mockGoal = new Goal("1", "test@1.com", "vacation", 1000, 500);
        when(goalRepository.findById(id)).thenReturn(Optional.of(mockGoal));

        Optional<Goal> foundGoal = goalRepository.findById(id);
        assertTrue(foundGoal.isPresent());
        assertEquals(mockGoal, foundGoal.get());
    }

    // JUNIT-PLAN-17
    @Test
    public void testGetGoalsByEmail() {
        Goal mockGoal = new Goal("1", "test@1.com", "vacation", 1000, 500);
        when(goalRepository.findById(anyString())).thenReturn(Optional.of(mockGoal));

        Goal result = goalService.getGoalById("1");

        assertEquals(mockGoal, result);
    }

    // JUNIT-PLAN-18
    @Test
    public void testAddGoal() {
        Goal mockGoal = new Goal("1", "test@1.com", "vacation", 1000, 500);
        when(goalRepository.save(any())).thenReturn(mockGoal);

        Goal result = goalService.addGoal(mockGoal);

        assertEquals(mockGoal, result);
    }

    // JUNIT-PLAN-19
    @Test
    public void testUpdateGoal() {
        Goal existingGoal = new Goal("1", "test@1.com", "vacation", 1000, 500);

        Goal updatedGoal = new Goal("1", "test@1.com", "vacation", 1000, 900);

        when(goalRepository.findById(anyString())).thenReturn(Optional.of(existingGoal));
        when(goalRepository.save(any())).thenReturn(updatedGoal);

        Goal result = goalService.updateGoal(updatedGoal);

        assertEquals(updatedGoal, result);
    }

    // JUNIT-PLAN-20
    @Test
    public void testDeleteGoal() {
        Goal mockGoal = new Goal("1", "test@1.com", "vacation", 1000, 500);

        // Do nothing when delete is called on the repository
        doNothing().when(goalRepository).delete(mockGoal);

        goalService.deleteGoal(mockGoal);

        // Verify that delete was called once on the repository
        verify(goalRepository, times(1)).delete(mockGoal);
    }

}