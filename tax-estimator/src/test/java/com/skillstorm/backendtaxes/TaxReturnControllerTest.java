package com.skillstorm.backendtaxes;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.skillstorm.backendtaxes.controllers.TaxReturnController;
import com.skillstorm.backendtaxes.models.Deduction;
import com.skillstorm.backendtaxes.models.IncomeSource;
import com.skillstorm.backendtaxes.models.TaxReturn;
import com.skillstorm.backendtaxes.services.TaxReturnService;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TaxReturnController.class)
public class TaxReturnControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private TaxReturnService service;


    // JUNIT-TAX-001
    // test findAllReturns
    @Test
    public void testFindAllReturns() throws Exception {
        List<TaxReturn> mockReturns = new ArrayList<>();
        when(service.findAllReturns()).thenReturn(mockReturns);

        mockMvc.perform(get("/taxes"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray());
    }

    // JUNIT-TAX-002
    // test findOnPath
    @Test
    public void testFindReturnByEmailFromPath() throws Exception {
        IncomeSource[] sources = new IncomeSource[] {
            new IncomeSource(123123123, "test", "W2", "WY", 112345, 12000, 2000),
            new IncomeSource(123123124, "test", "1099", "WI", 12000, 0, 0),
            new IncomeSource(123123125, "test", "W2", "AZ", 23000, 1000, 1000)
        };
        Deduction[] aboveLineDeductions = new Deduction[] {new Deduction("401K", 0)};
        Deduction[] belowLineDeductions = new Deduction[] {new Deduction("Standard Deduction", 12950)};
        double[] educationalExpenditures = new double[] {3000, 4500, 2000};
        TaxReturn mockReturn = new TaxReturn("test@example.com", "S", 3, 0, 3, educationalExpenditures, sources, belowLineDeductions, aboveLineDeductions );
        when(service.findReturnByEmail(mockReturn.getEmail())).thenReturn(mockReturn);

        mockMvc.perform(get("/taxes/{email}", mockReturn.getEmail()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.email").value(mockReturn.getEmail()));
    }

    // JUNIT-TAX-003
    // test deleteReturnByEmail
    @Test
    public void testDeleteReturnByEmailFromPath() throws Exception {
        String email = "test@example.com";

        mockMvc.perform(delete("/taxes/delete/{email}", email))
                .andExpect(status().isNoContent());

        verify(service, times(1)).deleteReturnByEmail(email);
    }

    // JUNIT-TAX-004
    // test newReturn
    @Test
    public void testNewReturn() throws Exception {
        IncomeSource[] sources = new IncomeSource[] {
            new IncomeSource(123123123, "test", "W2", "WY", 112345, 12000, 2000),
            new IncomeSource(123123124, "test", "1099", "WI", 12000, 0, 0),
            new IncomeSource(123123125, "test", "W2", "AZ", 23000, 1000, 1000)
        };
        Deduction[] aboveLineDeductions = new Deduction[] {new Deduction("401K", 0)};
        Deduction[] belowLineDeductions = new Deduction[] {new Deduction("Standard Deduction", 12950)};
        double[] educationalExpenditures = new double[] {3000, 4500, 2000};
        TaxReturn mockReturn = new TaxReturn("test@example.com", "S", 3, 0, 3, educationalExpenditures, sources, belowLineDeductions, aboveLineDeductions );

        when(service.saveTaxReturn(any(TaxReturn.class))).thenReturn(mockReturn);

        mockMvc.perform(post("/taxes/new")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(mockReturn)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.email").value(mockReturn.getEmail()));
    }

    // JUNIT-TAX-005
    // test updateTaxReturn
    @Test
    public void testUpdateTaxReturn() throws Exception {
        IncomeSource[] sources = new IncomeSource[] {
            new IncomeSource(123123123, "test", "W2", "WY", 112345, 12000, 2000),
            new IncomeSource(123123124, "test", "1099", "WI", 12000, 0, 0),
            new IncomeSource(123123125, "test", "W2", "AZ", 23000, 1000, 1000)
        };
        Deduction[] aboveLineDeductions = new Deduction[] {new Deduction("401K", 0)};
        Deduction[] belowLineDeductions = new Deduction[] {new Deduction("Standard Deduction", 12950)};
        double[] educationalExpenditures = new double[] {3000, 4500, 2000};
        TaxReturn mockReturn = new TaxReturn("test@example.com", "S", 3, 0, 3, educationalExpenditures, sources, belowLineDeductions, aboveLineDeductions );
        when(service.saveTaxReturn(any(TaxReturn.class))).thenReturn(mockReturn);

        mockMvc.perform(put("/taxes/update")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(mockReturn)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.email").value(mockReturn.getEmail()));
    }

    // JUNIT-TAX-006
    // test deleteIncomeSourceByEmailAndEIN
    @Test
    public void testDeleteIncomeSourceByEmailAndEIN() throws Exception {
        String email = "test@example.com";
        int ein = 123456789;

        mockMvc.perform(delete("/taxes/delete/{email}/{ein}", email, ein))
                .andExpect(status().isNoContent());

        verify(service, times(1)).deleteIncomeSourceByEmailAndEIN(email, ein);
    }

    // JUNIT-TAX-007
    // test updateTaxReturnIncomeSource
    @Test
    public void testUpdateTaxReturnIncomeSource() throws Exception {
        String email = "test@example.com";
        IncomeSource updatedIncomeSource = new IncomeSource(123456789, "test", "W2", "WY", 120000, 10000, 0);
        when(service.saveTaxReturnIncomeSource(eq(email), any(IncomeSource.class))).thenReturn(updatedIncomeSource);

        mockMvc.perform(put("/taxes/update/{email}", email)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatedIncomeSource)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.empID").value(123456789));
    }

}

