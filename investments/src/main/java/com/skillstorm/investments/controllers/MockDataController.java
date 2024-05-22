package com.skillstorm.investments.controllers;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.skillstorm.investments.models.MockData;
import com.skillstorm.investments.services.MockDataService;

@RestController
@RequestMapping(value="/alpha")
@CrossOrigin("*")
public class MockDataController {

    @Autowired
    MockDataService mockDataService;

    // Gets mock stock data by symbol
    @GetMapping("/{symbol}")
    public Map<String,?>getMockDataBySymbol(@PathVariable String symbol) {
        MockData data = mockDataService.getMockDataBySymbol(symbol);

        // creates a Map to mimic Alpha Vantage's API calls
        Map<String, String> stockMap = new HashMap<String, String>();

        stockMap.put("01. symbol", data.getSymbol());
        stockMap.put("02. open", data.getOpen());
        stockMap.put("03. high", data.getHigh());
        stockMap.put("04. low", data.getLow());
        stockMap.put("05. price", data.getPrice());
        stockMap.put("06. volume", data.getVolume());
        stockMap.put("07. latest trading day", data.getLatestTradingDay());
        stockMap.put("08. previous close", data.getPreviousClose());
        stockMap.put("09. change", data.getChange());
        stockMap.put("10. change percent", data.getChangePercent());

        // creates a Map in a Map to mimic their call heading
        Map<String, Map<String, String>> returnMap = new HashMap<String, Map<String, String>>();

        returnMap.put("Global Quote", stockMap);

        return returnMap;
    }
    
}
