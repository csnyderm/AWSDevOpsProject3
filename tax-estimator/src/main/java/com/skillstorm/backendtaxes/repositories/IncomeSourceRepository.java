package com.skillstorm.backendtaxes.repositories;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.skillstorm.backendtaxes.models.TaxReturn;

public interface IncomeSourceRepository extends MongoRepository<TaxReturn, String>{
    
}
