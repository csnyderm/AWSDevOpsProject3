package com.skillstorm.investments.services;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.skillstorm.investments.models.Investment;
import com.skillstorm.investments.repositories.InvestmentRepository;

@Service
public class InvestmentService {

    @Autowired
    InvestmentRepository investmentRepository;

    public List<Investment> getInvestmentsByEmail(String email) {
        Optional<List<Investment>> optionalInvestment = investmentRepository.findByEmail(email);
        return optionalInvestment.orElseThrow(() -> new RuntimeException("Investments not found"));
    }

    public Investment addInvestment(Investment investment) {
        // Check if an investment with the same symbol already exists
        Optional<Investment> existingInvestment = investmentRepository.findByEmailAndSymbol(investment.getEmail(), investment.getSymbol());

        if (existingInvestment.isPresent()) {
            throw new RuntimeException("Investment with the same symbol already exists");
        }

        return investmentRepository.save(investment);
    }

    // update investments
    public Investment updateInvestments(Investment updatedInvestment) {        
        Investment existingInvestment = investmentRepository.findByEmailAndSymbol(updatedInvestment.getEmail(), updatedInvestment.getSymbol()).orElseThrow(() -> new RuntimeException("Investment not found."));        

        existingInvestment.setStockName(updatedInvestment.getStockName());
        existingInvestment.setSymbol(updatedInvestment.getSymbol());
        existingInvestment.setShares(updatedInvestment.getShares());
        existingInvestment.setPurchasePrice(updatedInvestment.getPurchasePrice());        

        return investmentRepository.save(existingInvestment);
    }  
    
    // delete investment by its symbol
    public void deleteInvestment(Investment investment) { 
        Investment existingInvestment = investmentRepository.findByEmailAndSymbol(investment.getEmail(), investment.getSymbol()).orElseThrow(() -> new RuntimeException("Investment not found."));        
        investmentRepository.delete(existingInvestment);
    }

    // delete entire list of investment
    public void deleteInvestments(String email) {
        List<Investment> investments = investmentRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("Investments not found"));
        investmentRepository.deleteAll(investments);
    }
    
}
