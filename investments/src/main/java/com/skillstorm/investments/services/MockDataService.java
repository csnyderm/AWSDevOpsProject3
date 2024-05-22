package com.skillstorm.investments.services;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.skillstorm.investments.models.MockData;
import com.skillstorm.investments.repositories.MockDataRepository;

@Service
public class MockDataService {

    @Autowired
    MockDataRepository mockDataRepository;

    public MockData getMockDataBySymbol(String symbol) {
        Optional<MockData> optionalData = mockDataRepository.findBySymbol(symbol);
        return optionalData .orElseThrow(() -> new RuntimeException("Mock Data not found"));
    }
    
}
