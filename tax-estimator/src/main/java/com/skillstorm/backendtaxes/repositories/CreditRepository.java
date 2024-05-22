package com.skillstorm.backendtaxes.repositories;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.skillstorm.backendtaxes.models.TaxReturn;

public interface CreditRepository extends MongoRepository<TaxReturn, String> {
    
}
