package com.skillstorm.backendtaxes;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.mock.mockito.MockBean;

import com.skillstorm.backendtaxes.models.Deduction;
import com.skillstorm.backendtaxes.models.IncomeSource;
import com.skillstorm.backendtaxes.models.TaxReturn;
import com.skillstorm.backendtaxes.repositories.TaxReturnRepository;
import com.skillstorm.backendtaxes.services.TaxReturnService;

import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class TaxReturnServiceTest {

    @MockBean
    private TaxReturnRepository repo;

    @InjectMocks
    private TaxReturnService service;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    // JUNIT-TAX-008
    // test findAllReturns
    @Test
    public void testFindAllReturns() {
        List<TaxReturn> mockReturns = new ArrayList<>();
        when(repo.findAll()).thenReturn(mockReturns);

        List<TaxReturn> returns = service.findAllReturns();

        assertEquals(mockReturns, returns);
    }

    // JUNIT-TAX-009
    // test findReturnByEmail
    @Test
    public void testFindReturnByEmail() {
        String email = "test@example.com";
        IncomeSource[] sources = new IncomeSource[] {
            new IncomeSource(123123123, "test", "W2", "WY", 112345, 12000, 2000),
            new IncomeSource(123123124, "test", "1099", "WI", 12000, 0, 0),
            new IncomeSource(123123125, "test", "W2", "AZ", 23000, 1000, 1000)
        };
        Deduction[] aboveLineDeductions = new Deduction[] {new Deduction("401K", 0)};
        Deduction[] belowLineDeductions = new Deduction[] {new Deduction("Standard Deduction", 12950)};
        double[] educationalExpenditures = new double[] {3000, 4500, 2000};
        TaxReturn mockReturn = new TaxReturn("test@example.com", "S", 3, 0, 3, educationalExpenditures, sources, belowLineDeductions, aboveLineDeductions );
        when(repo.findByEmail(email)).thenReturn(Optional.of(mockReturn));

        TaxReturn foundReturn = service.findReturnByEmail(email);

        assertEquals(mockReturn, foundReturn);
    }

    // JUNIT-TAX-010
    // test deleteReturnByEmail
    @Test
    public void testDeleteReturnByEmail() {
        String email = "test@example.com";
        IncomeSource[] sources = new IncomeSource[] {
            new IncomeSource(123123123, "test", "W2", "WY", 112345, 12000, 2000),
            new IncomeSource(123123124, "test", "1099", "WI", 12000, 0, 0),
            new IncomeSource(123123125, "test", "W2", "AZ", 23000, 1000, 1000)
        };
        Deduction[] aboveLineDeductions = new Deduction[] {new Deduction("401K", 0)};
        Deduction[] belowLineDeductions = new Deduction[] {new Deduction("Standard Deduction", 12950)};
        double[] educationalExpenditures = new double[] {3000, 4500, 2000};
        TaxReturn mockReturn = new TaxReturn("test@example.com", "S", 3, 0, 3, educationalExpenditures, sources, belowLineDeductions, aboveLineDeductions );
        when(repo.findByEmail(email)).thenReturn(Optional.of(mockReturn));

        service.deleteReturnByEmail(email);

        verify(repo, times(1)).delete(mockReturn);
    }

    // JUNIT-TAX-011
    // test saveTaxReturn basic function
    @Test
    public void testSaveTaxReturn() {
        IncomeSource[] sources = new IncomeSource[] {
            new IncomeSource(123123123, "test", "W2", "WY", 112345, 12000, 2000),
            new IncomeSource(123123124, "test", "1099", "WI", 12000, 0, 0),
            new IncomeSource(123123125, "test", "W2", "AZ", 23000, 1000, 1000)
        };
        Deduction[] aboveLineDeductions = new Deduction[] {new Deduction("401K", 0)};
        Deduction[] belowLineDeductions = new Deduction[] {new Deduction("Standard Deduction", 12950)};
        double[] educationalExpenditures = new double[] {3000, 4500, 2000};
        TaxReturn mockReturn = new TaxReturn("test@example.com", "S", 3, 0, 3, educationalExpenditures, sources, belowLineDeductions, aboveLineDeductions );
        when(repo.save(any(TaxReturn.class))).thenReturn(mockReturn);
        when(repo.findByEmail(anyString())).thenReturn(Optional.of(mockReturn));

        TaxReturn savedReturn = service.saveTaxReturn(mockReturn);

        assertNotNull(savedReturn);
    }

    // JUNIT-TAX-012
    // test saveTaxReturn federal tax calculations
    @Test
    public void testSaveTaxReturnFederalTaxCalculations() {
        IncomeSource[] sources = new IncomeSource[] {
            new IncomeSource(123123123, "test", "W2", "WY", 112345, 12000, 2000),
            new IncomeSource(123123124, "test", "1099", "WI", 12000, 0, 0),
            new IncomeSource(123123125, "test", "W2", "AZ", 23000, 1000, 1000)
        };
        Deduction[] aboveLineDeductions = new Deduction[] {new Deduction("401K", 0)};
        Deduction[] belowLineDeductions = new Deduction[] {new Deduction("Standard Deduction", 12950)};
        double[] educationalExpenditures = new double[] {3000, 4500, 2000};
        TaxReturn mockReturn = new TaxReturn("test@example.com", "S", 3, 0, 3, educationalExpenditures, sources, belowLineDeductions, aboveLineDeductions );
        when(repo.save(any(TaxReturn.class))).thenReturn(mockReturn);
        when(repo.findByEmail(anyString())).thenReturn(Optional.of(mockReturn));

        TaxReturn savedReturn = service.saveTaxReturn(mockReturn);

        assertNotNull(savedReturn);
        assertEquals(6654.80, Math.round((savedReturn.getTotalFedOwed()*100.0)) / 100.0);
    }

    // JUNIT-TAX-013
    // test saveTaxReturn state tax calculations
    @Test
    public void testSaveTaxReturnStateTaxCalculations() {
        IncomeSource[] sources = new IncomeSource[] {
            new IncomeSource(123123123, "test", "W2", "WI", 112345, 12000, 2000),
            new IncomeSource(123123124, "test", "1099", "WI", 12000, 0, 500),
            new IncomeSource(123123125, "test", "W2", "AZ", 23000, 1000, 1000)
        };
        Deduction[] aboveLineDeductions = new Deduction[] {new Deduction("401K", 0)};
        Deduction[] belowLineDeductions = new Deduction[] {new Deduction("Standard Deduction", 12950)};
        double[] educationalExpenditures = new double[] {3000, 4500, 2000};
        TaxReturn mockReturn = new TaxReturn("test@example.com", "S", 3, 0, 0, educationalExpenditures, sources, belowLineDeductions, aboveLineDeductions );
        when(repo.save(any(TaxReturn.class))).thenReturn(mockReturn);
        when(repo.findByEmail(anyString())).thenReturn(Optional.of(mockReturn));

        TaxReturn savedReturn = service.saveTaxReturn(mockReturn);

        assertNotNull(savedReturn);
        assertEquals(2208.94, Math.round((savedReturn.getTotalStateOwed()*100.0)) / 100.0);
    }

    // JUNIT-TAX-014
    // test saveTaxReturn federal tax calculations for a different filing status
    @Test
    public void testSaveTaxReturnFilingStatusFederalTaxCalculations() {
        IncomeSource[] sources = new IncomeSource[] {
            new IncomeSource(123123123, "test", "W2", "WY", 112345, 12000, 2000),
            new IncomeSource(123123124, "test", "1099", "WI", 12000, 0, 0),
            new IncomeSource(123123125, "test", "W2", "AZ", 23000, 1000, 1000)
        };
        Deduction[] aboveLineDeductions = new Deduction[] {new Deduction("401K", 0)};
        Deduction[] belowLineDeductions = new Deduction[] {new Deduction("Standard Deduction", 27700)};
        double[] educationalExpenditures = new double[] {3000, 4500, 2000};
        TaxReturn mockReturn = new TaxReturn("test@example.com", "MJ", 3, 0, 3, educationalExpenditures, sources, belowLineDeductions, aboveLineDeductions );
        when(repo.save(any(TaxReturn.class))).thenReturn(mockReturn);
        when(repo.findByEmail(anyString())).thenReturn(Optional.of(mockReturn));

        TaxReturn savedReturn = service.saveTaxReturn(mockReturn);

        assertNotNull(savedReturn);
        assertEquals(-7640.10, Math.round((savedReturn.getTotalFedOwed()*100.0)) / 100.0);
    }

    // JUNIT-TAX-015
    // test saveTaxReturn state tax calculations for a different filing status
    @Test
    public void testSaveTaxReturnFilingStatusStateTaxCalculations() {
        IncomeSource[] sources = new IncomeSource[] {
            new IncomeSource(123123123, "test", "W2", "WI", 112345, 12000, 2000),
            new IncomeSource(123123124, "test", "1099", "WI", 12000, 0, 500),
            new IncomeSource(123123125, "test", "W2", "AZ", 23000, 1000, 1000)
        };
        Deduction[] aboveLineDeductions = new Deduction[] {new Deduction("401K", 0)};
        Deduction[] belowLineDeductions = new Deduction[] {new Deduction("Standard Deduction", 27700)};
        double[] educationalExpenditures = new double[] {0, 0, 0};
        TaxReturn mockReturn = new TaxReturn("test@example.com", "MJ", 3, 0, 0, educationalExpenditures, sources, belowLineDeductions, aboveLineDeductions );
        when(repo.save(any(TaxReturn.class))).thenReturn(mockReturn);
        when(repo.findByEmail(anyString())).thenReturn(Optional.of(mockReturn));

        TaxReturn savedReturn = service.saveTaxReturn(mockReturn);

        assertNotNull(savedReturn);
        assertEquals(1919.56, Math.round((savedReturn.getTotalStateOwed()*100.0)) / 100.0);
    }

    // JUNIT-TAX-016
    // test deleteIncomeSourceByEmailAndEIN
    @Test
    public void testDeleteIncomeSourceByEmailAndEIN() {
        String email = "test@example.com";
        int ein = 123123123;
        IncomeSource[] sources = new IncomeSource[] {
            new IncomeSource(123123123, "test", "W2", "WY", 112345, 12000, 2000),
            new IncomeSource(123123124, "test", "1099", "WI", 12000, 0, 0),
            new IncomeSource(123123125, "test", "W2", "AZ", 23000, 1000, 1000)
        };
        Deduction[] aboveLineDeductions = new Deduction[] {new Deduction("401K", 0)};
        Deduction[] belowLineDeductions = new Deduction[] {new Deduction("Standard Deduction", 12950)};
        double[] educationalExpenditures = new double[] {3000, 4500, 2000};
        TaxReturn mockReturn = new TaxReturn("test@example.com", "S", 3, 0, 3, educationalExpenditures, sources, belowLineDeductions, aboveLineDeductions );
        when(repo.findByEmail(email)).thenReturn(Optional.of(mockReturn));

        service.deleteIncomeSourceByEmailAndEIN(email, ein);

        assertEquals(2, mockReturn.getIncomeSources().length);
    }

    // JUNIT-TAX-017
    // test saveTaxReturnIncomeSource
    @Test
    public void testSaveTaxReturnIncomeSource() {
        String email = "test@example.com";
        IncomeSource updatedIncomeSource = new IncomeSource(123123123, "test", "W2", "WY", 120000, 10000, 0);
        IncomeSource[] sources = new IncomeSource[] {
            new IncomeSource(123123123, "test", "W2", "WY", 112345, 12000, 2000),
            new IncomeSource(123123124, "test", "1099", "WI", 12000, 0, 0),
            new IncomeSource(123123125, "test", "W2", "AZ", 23000, 1000, 1000)
        };
        Deduction[] aboveLineDeductions = new Deduction[] {new Deduction("401K", 0)};
        Deduction[] belowLineDeductions = new Deduction[] {new Deduction("Standard Deduction", 12950)};
        double[] educationalExpenditures = new double[] {3000, 4500, 2000};
        TaxReturn mockReturn = new TaxReturn("test@example.com", "S", 3, 0, 3, educationalExpenditures, sources, belowLineDeductions, aboveLineDeductions );
        when(repo.findByEmail(email)).thenReturn(Optional.of(mockReturn));

        service.saveTaxReturnIncomeSource(email, updatedIncomeSource);

        IncomeSource currentIncomeSource = mockReturn.getIncomeSources()[0];
        assertEquals(3, mockReturn.getIncomeSources().length);
        assertEquals(updatedIncomeSource.getIncome(), currentIncomeSource.getIncome());
    }
}

