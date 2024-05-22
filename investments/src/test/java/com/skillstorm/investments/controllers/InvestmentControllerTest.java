package com.skillstorm.investments.controllers;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.skillstorm.investments.InvestmentsApplication;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.skillstorm.investments.models.Investment;
import com.skillstorm.investments.services.InvestmentService;

import java.util.ArrayList;
import java.util.List;

@SpringBootTest(classes = InvestmentsApplication.class)
@AutoConfigureMockMvc
public class InvestmentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private InvestmentService investmentService;

    // JUNIT-INVESTMENT-001
    @Test
    public void testGetInvestmentsByEmail() throws Exception {
        List<Investment> investments = new ArrayList<>();

        // Mock the service behavior
        when(investmentService.getInvestmentsByEmail("test@example.com")).thenReturn(investments); // Replace with expected result

        // Perform the GET request and verify the response
        mockMvc.perform(get("/investments/email/test@example.com"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    // JUNIT-INVESTMENT-002
    @Test
    public void testNewInvestment() throws Exception {
        Investment investment = new Investment("test@example.com", "Apple Inc.", "AAPL", 10, 150.0); // Create an example investment
        String jsonInvestment = objectMapper.writeValueAsString(investment);

        // Mock the service behavior
        when(investmentService.addInvestment(any())).thenReturn(investment);

        // Perform the POST request and verify the response
        mockMvc.perform(post("/investments/new")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonInvestment))
                .andExpect(status().isCreated());
    }

    // JUNIT-INVESTMENT-003
    @Test
    public void testUpdateInvestments() throws Exception {
        Investment updatedInvestment = new Investment("1", "test@example.com", "Apple Inc.", "AAPL", 15, 200.0); // Create an example updated investment
        String jsonUpdatedInvestment = objectMapper.writeValueAsString(updatedInvestment);

        // Mock the service behavior
        when(investmentService.updateInvestments(any())).thenReturn(updatedInvestment);

        // Perform the PUT request and verify the response
        mockMvc.perform(put("/investments/update")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonUpdatedInvestment))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    // JUNIT-INVESTMENT-004
    @Test
    public void testDeleteInvestment() throws Exception {
        Investment existingInvestment = new Investment("1", "test@example.com", "Apple Inc.", "AAPL", 10, 150.0);

        mockMvc.perform(delete("/investments/delete")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(existingInvestment)))
                .andExpect(status().isNoContent());

        verify(investmentService).deleteInvestment(existingInvestment);
    }

    // JUNIT-INVESTMENT-005
    @Test
    public void testDeleteAllInvestments() throws Exception {
        String email = "test@example.com";

        mockMvc.perform(delete("/investments/deleteAll/{email}", email)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        verify(investmentService).deleteInvestments(email);
    }

    private String asJsonString(final Object obj) {
        try {
            final ObjectMapper mapper = new ObjectMapper();
            return mapper.writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
