package com.skillstorm.investments.services;

import com.skillstorm.investments.models.Investment;
import com.skillstorm.investments.repositories.InvestmentRepository;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class InvestmentServiceTest {

    @Mock
    private InvestmentRepository investmentRepository;

    @InjectMocks
    private InvestmentService investmentService;

    // JUNIT-INVESTMENT-007
    @Test
    public void testGetInvestmentsByEmail() {
        String email = "test@example.com";
        List<Investment> expectedInvestments = new ArrayList<>();
        when(investmentRepository.findByEmail(email)).thenReturn(Optional.of(expectedInvestments));

        List<Investment> actualInvestments = investmentService.getInvestmentsByEmail(email);

        assertEquals(expectedInvestments, actualInvestments);
    }

    // JUNIT-INVESTMENT-008
    @Test
    public void testAddInvestment() {
        Investment investment = new Investment("1", "test@example.com", "Apple Inc.", "AAPL", 10, 150.0);
        when(investmentRepository.save(investment)).thenReturn(investment);

        Investment savedInvestment = investmentService.addInvestment(investment);

        assertEquals(investment, savedInvestment);
    }

    // JUNIT-INVESTMENT-009
    @Test
    public void testUpdateInvestments() {
        Investment existingInvestment = new Investment("1", "test@example.com", "Apple Inc.", "AAPL", 10, 150.0);
        Investment updatedInvestment = new Investment("1", "test@example.com", "Apple Inc.", "AAPL", 8, 150.0);
        when(investmentRepository.findByEmailAndSymbol(any(), any())).thenReturn(Optional.of(existingInvestment));
        when(investmentRepository.save(existingInvestment)).thenReturn(updatedInvestment);

        Investment resultInvestment = investmentService.updateInvestments(updatedInvestment);

        assertEquals(updatedInvestment, resultInvestment);
    }

    // JUNIT-INVESTMENT-010
    @Test
    public void testDeleteInvestment() {
        Investment investment = new Investment("1", "test@example.com", "Apple Inc.", "AAPL", 10, 150.0);
        // Mock the behavior of the investmentRepository
        when(investmentRepository.findByEmailAndSymbol(anyString(), anyString())).thenReturn(Optional.of(investment));

        investmentService.deleteInvestment(investment);

        verify(investmentRepository, times(1)).delete(investment);
    }

    // JUNIT-INVESTMENT-011
    @Test
    public void testDeleteInvestments() {
        String email = "test@example.com";
        List<Investment> investments = new ArrayList<>();
        when(investmentRepository.findByEmail(email)).thenReturn(Optional.of(investments));

        investmentService.deleteInvestments(email);

        verify(investmentRepository, times(1)).deleteAll(investments);
    }

}
