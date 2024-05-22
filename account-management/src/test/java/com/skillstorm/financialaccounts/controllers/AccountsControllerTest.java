package com.skillstorm.financialaccounts.controllers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.skillstorm.financialaccounts.models.Accounts;
import com.skillstorm.financialaccounts.models.BankAccount;
import com.skillstorm.financialaccounts.models.CreditCard;
import com.skillstorm.financialaccounts.models.Loan;
import com.skillstorm.financialaccounts.services.AccountsService;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.ArrayList;

@WebMvcTest(AccountsController.class)
public class AccountsControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AccountsService accountsService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    // JUNIT-ACCOUNTS-001
    // get("/accounts/email/testemail@yahoo.com") test
    @Test
    public void getAccountsByEmailTest() throws Exception {
        Accounts accounts = new Accounts();

        when(accountsService.findByEmail("testemail@yahoo.com")).thenReturn(accounts);

        mockMvc.perform(get("/accounts/email/testemail@yahoo.com"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    // JUNIT-ACCOUNTS-002
    // post("/accounts/newAccounts") test
    @Test
    public void testCreateAccounts() throws Exception {
        Accounts accounts = new Accounts("testemail@yahoo.com", new ArrayList<BankAccount>(), new ArrayList<CreditCard>(), new ArrayList<Loan>());

        when(accountsService.createAccounts(any())).thenReturn(accounts);

        mockMvc.perform(post("/accounts/newAccounts")
            .contentType("application/json")
            .content(asJsonString(accounts)))
            .andExpect(status().isCreated());
    }

    // JUNIT-ACCOUNTS-003
    // put("/accounts/updateAccounts") test
    @Test
    public void updateAccountsTest() throws Exception {
        Accounts updatedAccounts = new Accounts("testemail@yahoo.com", new ArrayList<BankAccount>(), new ArrayList<CreditCard>(), new ArrayList<Loan>());
        updatedAccounts.getBankAccounts().add(new BankAccount("1", "Chase", "Checking", 10000));
        updatedAccounts.getCreditCards().add(new CreditCard("1", "Chase", 15000, 500, 9.5));
        updatedAccounts.getLoans().add(new Loan("1", "Chase", "Auto", 5000, 5.3, 24, false));
        String updatedAccountsJson = objectMapper.writeValueAsString(updatedAccounts);

        when(accountsService.updateAccounts(any())).thenReturn(updatedAccounts);

        mockMvc.perform(put("/accounts/updateAccounts")
            .contentType(MediaType.APPLICATION_JSON)
            .content(updatedAccountsJson))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    // JUNIT-ACCOUNTS-004
    // delete("/accounts/deleteAccounts") test
    @Test
    public void testDeleteAccounts() throws Exception {
        Accounts existingAccounts = new Accounts("testemail@yahoo.com", new ArrayList<BankAccount>(), new ArrayList<CreditCard>(), new ArrayList<Loan>());
        existingAccounts.getBankAccounts().add(new BankAccount("1", "Chase", "Checking", 10000));
        existingAccounts.getCreditCards().add(new CreditCard("1", "Chase", 15000, 500, 9.5));
        existingAccounts.getLoans().add(new Loan("1", "Chase", "Auto", 5000, 5.3, 24, false));

        mockMvc.perform(delete("/accounts/deleteAccounts")
            .contentType(MediaType.APPLICATION_JSON)
            .content(asJsonString(existingAccounts)))
            .andExpect(status().isNoContent());

        verify(accountsService).deleteAccounts(existingAccounts);
    }


    // Helper method to convert object to JSON string
    private String asJsonString(final Object obj) {
        try {
            return new ObjectMapper().writeValueAsString(obj);
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
